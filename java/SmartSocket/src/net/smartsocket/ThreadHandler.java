/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collection;
import java.util.Vector;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
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
    //public PrintWriter out;
    //# Byte testing...
    // private InputStream in;
    public OutputStream out;
    byte nullByte = '\u0000';
    public Server _server;
    private OutputStream policy_out;
    private String policy = "<cross-domain-policy>\n<allow-access-from domain='*' to-ports='*'/>\n</cross-domain-policy>";

    public ThreadHandler(Socket socket, Server server) throws IOException {
	this.socket = socket;
	_server = server;

	in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
//	out = new PrintWriter(
//		new OutputStreamWriter(socket.getOutputStream()));

	//in = socket.getInputStream();
	out = socket.getOutputStream();

	policy_out = socket.getOutputStream();

	try {
	    Class[] args = new Class[1];
	    args[0] = ThreadHandler.class;
	    Method m = _server._extension.getMethod("onConnect", args);
	    m.invoke(_server._extensionInstance, this);
	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log(_server.extension.get("name").toString(), "This extension does not have an onConnect method!");
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
		System.out.println("INCOMING: " + line.toString());
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
			line = Base64Coder.decodeString(line);
			
			line = line.replace("<", "&lt;");

			Object jsonObj = JSONValue.parse(line);
			JSONArray json = (JSONArray) jsonObj;

			//# We will call this method in our extension.
			String method = json.get(0).toString();
			//# We will send these parameters to the above method.
			JSONObject parameters = (JSONObject) json.get(1);

			//# Prepare to invoke the dynamically called method
			Class[] args = new Class[2];
			args[0] = ThreadHandler.class;
			args[1] = JSONObject.class;
			Method m = _server._extension.getMethod(method, args);

			//# Here we finally invoke the method on our extension with the data collected from above.
			Object params[] = {this, parameters};
			m.invoke(_server._extensionInstance, params);

		    } catch (Exception e) {
			e.printStackTrace();
			Logger.log("ThreadHandler", e.toString());
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
		System.out.println("Closing streams seems to have failed: "+ioe);
	    } finally {
		synchronized (handlers) {
		    SmartLobby.onDisconnect(unique_identifier);
		    handlers.removeElement(this);
		}

		try {
		    finalize();
		}catch(Throwable e) {
		    e.printStackTrace();
		}

	    }
	}

    }

    public void send(Object data) {

	try {
	    //# We need to append a \r for SmartLobby on the client side.
	    String toClient = Base64Coder.encodeString(data.toString()) + "\r";
	    //# Get the data being sent as bytes.
	    byte[] bytes = toClient.getBytes("UTF-8");

	    out.write(bytes);
	    out.flush();
	    System.out.println("SENT: " + data.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}

