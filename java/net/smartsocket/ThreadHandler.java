/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Vector;
import org.xml.sax.DocumentHandler;
import org.json.simple.*;

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
		
		//String s = "{\"c\":\"login\"}";
		System.out.println("Receiving: "+line);
		Object obj = JSONValue.parse(line);
		JSONObject obj2 = (JSONObject) obj;

		String command = obj2.get("c").toString();

		Class[] args = new Class[2];
		args[0] = Socket.class;
		args[1] = Object.class;

		Object params[] = {socket, obj};

		Method m = _server._extension.getMethod(command, args);
		m.invoke(_server._extensionInstance, params);

		}catch(Exception e) {
		    e.printStackTrace();
		}

	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
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
}

