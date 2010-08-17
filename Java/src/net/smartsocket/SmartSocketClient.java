/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import org.json.simple.*;

/**
 *
 * @author XaeroDegreaz
 */
public abstract class SmartSocketClient extends Socket implements Runnable {

    private static Class _listenerClass;
    private static Object _listenerObject;
    private static boolean _connected = false;

    //# Connect using a timeout.
    public SmartSocketClient(String host, int port, int timeout) throws Exception {
	super();

	try {
	    InetAddress addr = InetAddress.getByName(host);
	    SocketAddress sockaddr = new InetSocketAddress(addr, port);

	    connect(sockaddr, timeout);
	} catch (Exception e) {
	    onConnectFail(e);
	}
	_listenerClass = getClass();
	_listenerObject = this;
    }

    //# Overloaded constructor in case a timeout is not passed.
    public SmartSocketClient(String host, int port) throws Exception {
	super();

	try {
	    InetAddress addr = InetAddress.getByName(host);
	    SocketAddress sockaddr = new InetSocketAddress(addr, port);

	    connect(sockaddr, 0);
	} catch (Exception e) {
	    onConnectFail(e);
	}
	_listenerClass = getClass();
	_listenerObject = this;
    }

    public static void send(Object data) {
	System.out.println("OUTGOING: " + data);
	try {
	    PrintWriter wr = new PrintWriter(
		    new OutputStreamWriter(((SmartSocketClient) _listenerObject).getOutputStream()));
	    wr.print(data.toString() + "\r");
	    wr.flush();
	} catch (Exception e) {
	    System.err.println("Write error (" + e + "): " + data);
	}
    }

    protected abstract void onConnect(String connectMessage);

    protected abstract void onConnectFail(Exception exception);

    protected abstract void onDisconnect(String disconnectMessage);

    public void run() {
	String str;

	//# Keep a loop going trying to read data from the server
	try {
	    BufferedReader rd = new BufferedReader(
		    new InputStreamReader(this.getInputStream()));

	    //# We need to send notice to our listener that we are connected to the server
	    _connected = true;
	    onConnect("Connection established.");

	    while ((str = rd.readLine()) != null) {
		//# Send this to the JSON processing function
		process(str);
	    }
	    //# Close after there is a disconnection from the server.
	    rd.close();
	} catch (Exception e) {
	} finally {
	    if (_connected) {
		System.err.println("Connection lost.");
		onDisconnect("Connection to the server lost.");
	    }
	}
    }

    private void process(String str) {
	System.out.println("INCOMING: " + str);

	//# Setup some variables
	JSONArray strArr = null;
	Method m = null;
	String method = null;
	Object parameters = null;

	//# First we test to make sure that this string can be converted into a JSONArray
	//# If not, we just ignore it because it's invalid, as this is the method of communication
	//# Between SmartSocket and the client.
	try {
	    strArr = (JSONArray) JSONValue.parse(str);
	    method = strArr.get(0).toString();
	    parameters = strArr.get(1);
	} catch (Exception e) {
	    System.err.println("Invalid format. Ignoring: " + str);
	    return;
	}

	//# Prepare to invoke the dynamically called method on our listener
	Class[] args = new Class[1];

	//# Could either be a JSONArray or JSONObject being sent as parameters from Server
	//# Since JSONObject is more common, we'll try that one first
	args[0] = JSONObject.class;

	try {
	    m = _listenerClass.getMethod(method, args);

	    Object params[] = {parameters};
	    m.invoke(_listenerObject, params);

	} catch (Exception e) {
	    //# Didn't work as a JSONObject, let's try JSONArray
	    args[0] = JSONArray.class;

	    try {
		m = _listenerClass.getMethod(method, args);

		Object params[] = {parameters};
		m.invoke(_listenerObject, params);

	    } catch (Exception f) {
		//# Neither one of those worked so that method must not exist on our listener.
		System.err.println("Could not find the requested method: '" + method + "' on: " + _listenerClass.getName());
	    }

	}
    }
}
