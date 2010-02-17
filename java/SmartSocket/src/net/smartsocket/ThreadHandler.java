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
    private Socket socket;
    public String unique_identifier = null;
    //public String unique_threadName = null;
    private BufferedReader in;
    public PrintWriter out;
    //# Byte testing...
    // private InputStream in;
    //public OutputStream out;
    byte nullByte = '\u0000';
    public Server _server;
    private OutputStream policy_out;
    private String policy = "<cross-domain-policy>\n<allow-access-from domain='*' to-ports='*'/>\n</cross-domain-policy>";

    public ThreadHandler(Socket socket, Server server) throws IOException {
	this.socket = socket;
	_server = server;

	in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
	out = new PrintWriter(
		new OutputStreamWriter(socket.getOutputStream()));

	//in = socket.getInputStream();
	//out = socket.getOutputStream();

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

    public void run() {
	String line;
	synchronized (handlers) {
	    handlers.addElement(this);
	}
	sendPolicy();
	try {
	    while ((line = in.readLine()) != null) {
		//# let's check for cross domain policy first.
		if (line.contains("<policy-file-request/>")) {
		    unique_identifier = "<policy-file-request/>";

		    //sendPolicy();

		} else {
		    try {
			//# Here we parse the input from the client.
			//# The input will be a JSON array.
		    /*
			 * Example JSON string input received
			 * ["MethodName",
			 *	    {
			 *		"paramName":"paramValue",
			 *		"anotherParam":"anotherValue"
			 *	    }
			 * ]
			 */

			//# Replace bad text...
			if (Loader._constants.get("USE_BASE64").equals(true)) {
			    System.err.println("Trying to use base64");
			    try {
				line = Base64Coder.decodeString(line);
			    } catch (Exception e) {
				Logger.log("ThreadHandler", "Invalid Base64 input detected.");
			    }
			} else {
			    System.err.println("base64 not selected in configuration");
			}

			System.out.println("INCOMING: " + line.toString());

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
			    line = line.replace("<", "&lt;");

			    //# Here is everything for JSON data protocol.
			    Object jsonObj = JSONValue.parse(line);
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

		    } catch (Exception e) {
			e.printStackTrace();
			Logger.log("ThreadHandler", "Removing rogue thread... " + socket.getLocalAddress().getHostAddress());
			//# Destroy rogue thread immediately...
			try {
			    in.close();
			    out.close();
			    socket.close();
			} catch (IOException ioe) {
			    System.out.println("Closing streams seems to have failed: " + ioe);
			} finally {
			    synchronized (handlers) {
				SmartLobby.onDisconnect(unique_identifier);
				handlers.removeElement(this);
			    }

			    try {
				finalize();
			    } catch (Throwable ee) {
				ee.printStackTrace();
			    }

			}
			//##############
		    }

		}
	    }
	} catch (Exception ioe) {
	    ioe.printStackTrace();
	    Logger.log("ThreadHandler", ioe.toString());
	} finally {
	    try {
		in.close();
		out.close();
		socket.close();
	    } catch (IOException ioe) {
		System.out.println("Closing streams seems to have failed: " + ioe);
	    } finally {
		synchronized (handlers) {
		    SmartLobby.onDisconnect(unique_identifier);
		    handlers.removeElement(this);
		}

		try {
		    finalize();
		} catch (Throwable e) {
		    e.printStackTrace();
		}

	    }
	}

    }

    public void send(Object data) {

	try {
	    //# We need to append a \r for SmartLobby on the client side.
	    String toClient;

	    if (Loader._constants.get("USE_BASE64").equals(true)) {
		toClient = Base64Coder.encodeString(data.toString()) + "\r";
	    } else {
		toClient = data.toString() + "\r";
	    }

	    //# Get the data being sent as bytes.
	    out.print(toClient);
	    out.flush();

	    System.out.println("OUTGOING: " + data.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

