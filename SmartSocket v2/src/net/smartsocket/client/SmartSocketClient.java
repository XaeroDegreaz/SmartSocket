package net.smartsocket.client;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import com.google.gson.*;
import java.lang.reflect.InvocationTargetException;
import net.smartsocket.protocols.json.RemoteCall;

/**
 * This is the main routing mechanism for Java <b>clients</b> who are using SmartSocket.
 * It has not been thoroughly tested.
 * @author XaeroDegreaz
 */
public abstract class SmartSocketClient extends Socket implements Runnable {

    private static Class _listenerClass;
    private static Object _listenerObject;
    private static boolean _connected = false;

    //# Connect using a timeout.
	/**
	 * 
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public SmartSocketClient(String host, int port, int timeout) {
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
	/**
	 * 
	 * @param host
	 * @param port
	 */
	public SmartSocketClient(String host, int port) {
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

	/**
	 * 
	 * @param call
	 */
	public static void send(RemoteCall call) {
	System.out.println("OUTGOING: " + call.properties.toString());
	try {
	    PrintWriter wr = new PrintWriter(
		    new OutputStreamWriter(((SmartSocketClient) _listenerObject).getOutputStream()));
	    wr.print(call.properties.toString() + "\r");
	    wr.flush();
	} catch (Exception e) {
	    System.err.println("Write error (" + e + "): " + call);
	}
    }

	/**
	 * 
	 * @param connectMessage
	 */
	protected abstract void onConnect(String connectMessage);

	/**
	 * 
	 * @param exception
	 */
	protected abstract void onConnectFail(Exception exception);

	/**
	 * 
	 * @param disconnectMessage
	 */
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
            e.printStackTrace();
	} finally {
	    if (_connected) {
		System.err.println("Connection lost.");
		onDisconnect("Connection to the server lost.");
	    }
	}
    }

    private void process(String line) {
	System.out.println("INCOMING: " + line);

	//# Get ready to create dynamic method call to extension
        Class[] classes = new Class[1];
        classes[0] = JsonObject.class;
        
        //# Reflection
        Method m = null;

        //# Setup method and params
        String methodName = null;
        JsonObject params = null;
        
        try {
            //# Get the particulars of the JSON call from the client
            params = (JsonObject)new JsonParser().parse(line);
            methodName = params.get("method").getAsString();

            Object[] o = {params};
            m = _listenerClass.getMethod(methodName, classes);
            m.invoke(_listenerObject, o);
            
            
        }catch(JsonParseException e) {
            System.out.println("["+getClass().getSimpleName()+"] Server has tried to pass invalid JSON");
        }catch(NoSuchMethodException e) {
            System.out.println("["+getClass().getSimpleName()+"] The method: "+methodName+" does not exist");
        }catch(IllegalAccessException e) {
           System.out.println("["+getClass().getSimpleName()+"] The method: "+methodName+" is not accessible from this scope.");
        }catch(InvocationTargetException e) {
           System.out.println("["+getClass().getSimpleName()+"] The method: \'"+methodName+"\' reports: "+
                    e.getTargetException().getMessage()+" in JSONObject string: "+params.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
