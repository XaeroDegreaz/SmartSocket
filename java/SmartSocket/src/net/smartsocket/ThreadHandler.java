/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Vector;
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
    private BufferedReader in;
    private PrintWriter out;
    public Server _server;
    //static SmartSocketJAVAApp _server;

    public ThreadHandler(Socket socket, Server server) throws IOException {
	this.socket = socket;
	_server = server;
	in = new BufferedReader(
		new InputStreamReader(socket.getInputStream()));
	out = new PrintWriter(
		new OutputStreamWriter(socket.getOutputStream()));

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

    public void run() {
	String line;
	synchronized (handlers) {
	    handlers.addElement(this);
	}
	try {
	    while (!(line = in.readLine()).equalsIgnoreCase("/quit")) {
		/*for (int i = 0; i < handlers.size(); i++) {
		synchronized (handlers) {
		Logger.log("ThreadHandler-Run", line);
		ThreadHandler handler =
		(ThreadHandler) handlers.elementAt(i);
		handler.out.println(line + "\r");
		handler.out.flush();
		}
		}*/
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
		    Object jsonObj = JSONValue.parse(line);
		    JSONArray json = (JSONArray) jsonObj;
		    System.out.println(line.toString());

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
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    Logger.log("ThreadHandler", ioe.toString());
	} finally {
	    try {
		in.close();
		out.close();
		socket.close();
	    } catch (IOException ioe) {
	    } finally {
		synchronized (handlers) {
		    handlers.removeElement(this);
		}

	    }
	}
    }

    public void sendSelf(String data) {
	this.out.println(data + "\r");
	this.out.flush();
    }

    public void sendAll(Object data) {
    }
}

