/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Vector;
import java.util.zip.*;
import net.smartsocket.extensions.smartlobby.SmartLobby;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author XaeroDegreaz
 */
public class ThreadHandler extends Thread implements Runnable {

    static Vector handlers = new Vector(10);
    public Socket socket;
    public String unique_identifier = null;
    //public String unique_threadName = null;
    private BufferedReader in;
    public PrintWriter out;
    //private InputStream binaryIn;
    //private OutputStream binaryOut;
    //# Byte testing...
    // private InputStream in;
    //public OutputStream out;
    byte nullByte = '\u0000';
    public Server _server;
    private OutputStream policy_out;
    private String policy = "<cross-domain-policy><allow-access-from domain='*' to-ports='*'/></cross-domain-policy>";
    //# zlib streams
    DeflaterOutputStream zlibOut;
    InflaterInputStream zlibIn;

    public ThreadHandler(Socket socket, Server server) throws IOException {
	this.socket = socket;
	_server = server;

	in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
	out = new PrintWriter(
		new OutputStreamWriter(socket.getOutputStream()));

	policy_out = socket.getOutputStream();

	try {
	    Class[] args = new Class[1];
	    args[0] = ThreadHandler.class;
	    Method m = Server.extension.getMethod("onConnect", args);
	    m.invoke(Server.extensionInstance, this);
	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log(Server.extension.getName(), "This extension does not have an onConnect method!");
	}

    }

    public void sendPolicy() {
	try {
	    policy_out.write(policy.getBytes());
	    policy_out.write(nullByte);
	    policy_out.flush();
	    System.err.println("Sent policy");
	} catch (IOException ex) {
	    System.err.println("Error sending policy file");
	}
    }

    private void read() throws IOException {
	System.out.println("Waiting for data from client...");
	String line;

	if (Loader._constants.get("USE_ZLIB").equals(true)) {
	    //# Wrap Inflate / Deflate streams to our in and out variables
	    in = new BufferedReader(
		    new InputStreamReader(new InflaterInputStream(socket.getInputStream())));
	    //# For some reason, Flash doesn't seem to be able to read the zlib data when I use this type of
	    //# output stream...
	    //# TODO Make this work.
	    //out = new PrintWriter(
	    //new OutputStreamWriter(new DeflaterOutputStream(socket.getOutputStream())), true);
	}

	while (!(line = in.readLine()).equals(null)) {
	    //	    
	    try {
		processJSON(line);

		//# have to keep resetting Zlib streams after each read for some reason...
		//# TOTO Fix this crap here...
		if (Loader._constants.get("USE_ZLIB").equals(true)) {
		    in = new BufferedReader(
			    new InputStreamReader(new InflaterInputStream(socket.getInputStream())));
		}

	    } catch (Exception e) {
		System.out.println("No process?.");
		e.printStackTrace();
		//removeThread();
	    }
	}
	System.out.println("Done reading?!");
    }

    private void removeThread() {
	Logger.log("ThreadHandler", "Removing rogue thread... " + socket.getLocalAddress().getHostAddress());
	//# Destroy rogue thread immediately...
	try {
	    in.close();
	    out.close();
	    socket.close();
	} catch (IOException e) {
	    System.out.println("Closing streams seems to have failed: " + e);
	} finally {

	    try {
		synchronized (handlers) {
		    SmartLobby.onDisconnect(unique_identifier);
		    handlers.removeElement(this);
		}
	    } catch (Exception e) {
	    }
	}

    }

    private void processJSON(String string) throws Exception {

	System.out.println("INCOMING: " + string.toString());

	//# Prepare to invoke the dynamically called method on our extension
	Class[] args = new Class[2];
	Method m;
	String method = "";
	Object parameters = null;

	//# The ThreadHandler object must always be passed as the 1st parameter!
	args[0] = ThreadHandler.class;

	//# We need to check everything for usage of the JSON protocol in the config
	//# And change the way we send data accordingly.
	if (Loader._constants.get("DATA_PROTOCOL").equals("json")) {
	    args[1] = JSONObject.class;

	    //# We only use this since we are using JSON for SmartLobby and need to prevent HTML
	    string = string.replace("<", "&lt;");

	    //# Here is everything for JSON data protocol.
	    Object jsonObj = JSONValue.parse(string);
	    JSONArray json = (JSONArray) jsonObj;

	    //# We will call this method in our extension.
	    method = json.get(0).toString();
	    //# We will send these parameters to the above method.
	    parameters = (JSONObject) json.get(1);
	}

	//# TODO add XML support for a protocol.

	/**
	 * If you are using a custom protocol, you should define all of the parsing logic somewhere
	 * in here. You must create a value for args[1]
	 */
	//# We try to detect whether or not the method exists with the correct parameter objects defined above.
	m = Server.extension.getMethod(method, args);

	//# Here we finally invoke the method on our extension with the data collected from above.
	Object params[] = {this, parameters};
	m.invoke(Server.extensionInstance, params);

    }

    public void run() {
	String line;
	synchronized (handlers) {
	    handlers.addElement(this);
	}

	if (Loader._constants.get("SEND_POLICY_FILE").equals(true)) {
	    sendPolicy();
	}

	try {
	    System.out.println("Setting up the proper read channel...");

	    read();

	} catch (Exception ioe) {
	    ioe.printStackTrace();
	    Logger.log("ThreadHandler", "Client disconnected.");
	} finally {
	    Logger.log("ThreadHandler", "Cleaning up departing client streams...");
	    removeThread();

	    try {
		finalize();
	    } catch (Throwable e) {
		e.printStackTrace();
	    }

	}
    }

    public void send(Object data) {

	try {
	    //# We need to append a \r for SmartLobby on the client side.
	    String toClient;
	    toClient = data.toString() + "\r";

	    //# Get the data being sent as bytes.
	    if (Loader._constants.get("USE_ZLIB").equals(true)) {
		socket.getOutputStream().write(ZLIBCompress.compress(toClient));
		socket.getOutputStream().flush();
	    } else {
		out.print(toClient);
		out.flush();
	    }
	    System.out.println("OUTGOING: " + data.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

