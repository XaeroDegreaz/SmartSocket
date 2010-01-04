package net.smartsocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;

public class Server implements Runnable {

    protected int serverPort;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    public String extension;
    public Class _extension;
    public Object _extensionInstance;
    public static Connection conn = null;

    public Server(String extension, int port) {
	this.serverPort = port;
	this.extension = extension;
	
	try {
	   
	    ClassLoader cl = Server.class.getClassLoader();	    
	    Class c = cl.loadClass(extension + "." + extension);	    

	    this._extensionInstance = c.newInstance();
	    this._extension = _extensionInstance.getClass();

	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log("Server", e.toString());
	}
	//# Create a new isntance of the extension's class

    }

    public void run() {
	synchronized (this) {
	    this.runningThread = Thread.currentThread();
	}
	openServerSocket();

	while (!isStopped()) {

	    Socket clientSocket = null;
	    try {
		clientSocket = this.serverSocket.accept();

		try {
		    Class[] args = new Class[1];
		    args[0] = Socket.class;
		    Method m = _extension.getMethod("onConnect", args);
		    m.invoke(_extensionInstance, clientSocket);
		} catch (Exception e) {
		    Logger.log(extension, "This extension does not have an onConnect method!");
		}

	    } catch (IOException e) {
		if (isStopped()) {
		    System.out.println("Server Stopped.");
		    Logger.log("Server", "Server stopped.");
		    return;
		}
		Logger.log("Server", "Error accepting client connection " + this.serverPort + " [" + e.toString() + "]");
		throw new RuntimeException(
			"Error accepting client connection", e);
	    }
	    Logger.log("Server", "Spawning new connection on port " + this.serverPort);

	    try {
		new Thread(new ThreadHandler(clientSocket, this)).start();
	    } catch (Exception e) {
		Logger.log("Server", "Error creating server for port " + this.serverPort + " [" + e.toString() + "]");
		e.printStackTrace();
	    }
	}
	System.out.println("Server Stopped.");
	Logger.log("Server", "Server stopped.");
    }

    private synchronized boolean isStopped() {
	return this.isStopped;
    }

    public synchronized void stop() {
	this.isStopped = true;
	try {
	    this.serverSocket.close();
	} catch (IOException e) {
	    Logger.log("Server", "Error closing server " + this.serverPort + " [" + e.toString() + "]");
	    throw new RuntimeException("Error closing server", e);
	}
    }

    private void openServerSocket() {
	try {
	    this.serverSocket = new ServerSocket(this.serverPort);
	    //Logger.log("Server", "Server initialized for "+this.extension);
	} catch (IOException e) {
	    Logger.log("Server", "Cannot open port " + this.serverPort + " [" + e.toString() + "]");
	    throw new RuntimeException("Cannot open port " + this.serverPort, e);

	}
    }
}
