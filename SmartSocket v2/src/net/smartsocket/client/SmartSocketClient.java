package net.smartsocket.client;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import com.google.gson.*;
import java.lang.reflect.InvocationTargetException;
import net.smartsocket.protocols.binary.RemoteCall;

/**
 * This is the main routing mechanism for Java <b>clients</b> who are using SmartSocket.
 * It has not been thoroughly tested.
 * @author XaeroDegreaz
 */
public abstract class SmartSocketClient extends Socket implements Runnable {

	private static Class _listenerClass;
	private static Object _listenerObject;
	private static boolean _connected = false;
	private DataInputStream in;

	//# Connect using a timeout.
	/**
	 * 
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public SmartSocketClient( String host, int port, int timeout ) {
		super();

		try {
			InetAddress addr = InetAddress.getByName( host );
			SocketAddress sockaddr = new InetSocketAddress( addr, port );

			connect( sockaddr, timeout );
		} catch (Exception e) {
			onConnectFail( e );
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
	public SmartSocketClient( String host, int port ) {
		super();
		try {
			InetAddress addr = InetAddress.getByName( host );
			SocketAddress sockaddr = new InetSocketAddress( addr, port );

			connect( sockaddr, 0 );
		} catch (Exception e) {
			onConnectFail( e );
		}
		_listenerClass = getClass();
		_listenerObject = this;
	}

	/**
	 * 
	 * @param call
	 */
	public static boolean send( net.smartsocket.protocols.binary.RemoteCall call ) {
		System.out.println( "OUTGOING: " + call.properties.toString() );
		try {

			DataOutputStream out = new DataOutputStream( ((SmartSocketClient) _listenerObject).getOutputStream() );
			byte[] fileBytes = new byte[0];

			if ( call.file != null ) {
				long fileLength = call.file.length();
				fileBytes = new byte[(int) fileLength];

				call.properties.addProperty( "fileSize", fileLength );

				FileInputStream fileInputStream = new FileInputStream( call.file );
				fileInputStream.read( fileBytes );
			}

			out.write( 0 );
			out.writeLong( fileBytes.length );

			if ( fileBytes.length != 0 ) {
				out.write( fileBytes );
			}

			out.writeUTF( call.properties.toString() );

			out.flush();
		} catch (Exception e) {
			System.err.println( "Write error (" + e + "): " + call );
			((SmartSocketClient)_listenerObject).onSendFail(call, e);
			return false;
		}
		
		return true;
	}

	/**
	 * 
	 * @param connectMessage
	 */
	protected abstract void onConnect( String connectMessage );

	/**
	 * 
	 * @param exception
	 */
	protected abstract void onConnectFail( Exception exception );

	/**
	 * 
	 * @param disconnectMessage
	 */
	protected abstract void onDisconnect( String disconnectMessage );
	
	/**
	 * Called when failing to send information through output stream
	 * @param disconnectMessage
	 */
	protected abstract void onSendFail(RemoteCall call, Exception e );

	public void run() {
		//# Keep a loop going trying to read data from the server
		try {
			in = new DataInputStream( this.getInputStream() );

			//# We need to send notice to our listener that we are connected to the server
			_connected = true;
			onConnect( "Connection established." );
			
			new Thread(new Runnable() {
				public void run() {
					read( in );
				}				
			}).start();
			
			//# Close after there is a disconnection from the server.
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	/**
	 * Infinite read loop until the client closes their connections
	 */
	private void read( DataInputStream in ) {
		String input = null;
		byte[] bytes = null;
		int red;
		try {
			while ( (red = in.read()) != -1 ) {
				long fileLength = getFileLength();
				byte[] fileBytes = getFileBytes( fileLength );
				String jsonHeader = getJsonHeader();
				
				process( jsonHeader, fileBytes );
			}
			in.close();
			if ( _connected ) {
				System.err.println( "Connection lost." );
				onDisconnect( "Connection to the server lost." );
			}
		} catch (Exception e) {
			e.printStackTrace();
			onDisconnect( "Connection to the server lost." );
		}

	}

	public long getFileLength() throws IOException {
		return in.readLong();
	}

	private byte[] getFileBytes( long fileLength ) throws IOException {
		byte[] fileBytes = new byte[(int) fileLength];

		for ( int i = 0; i < fileLength; i++ ) {
			fileBytes[i] = in.readByte();
		}

		return fileBytes;
	}

	public String getJsonHeader() throws IOException {
		return in.readUTF().trim();
	}

	private void process( String line, byte[] fileBytes ) {
		System.out.println( "INCOMING: " + line );

		//# Reflection
		Method m = null;

		//# Setup method and params
		String methodName = null;
		JsonObject params = null;

		try {
			//# Get the particulars of the JSON call from the client
			params = (JsonObject) new JsonParser().parse( line );
			methodName = params.get( "method" ).getAsString();
			
			if ( fileBytes.length == 0 ) {
				//# Get ready to create dynamic method call to extension
				Class[] classes = new Class[1];
				classes[0] = JsonObject.class;
				
				//# Try to call the method on the desired extension class
				Object[] o = { params };
				m = _listenerClass.getMethod( methodName, classes );
				m.invoke( _listenerObject, o );

			} else {
				//# Get ready to create dynamic method call to extension
				Class[] classes = new Class[2];
				classes[0] = JsonObject.class;
				classes[1] = byte[].class;

				//# Try to call the method on the desired extension class
				Object[] o = { params, fileBytes };
				m = _listenerClass.getMethod( methodName, classes );
				m.invoke( _listenerObject, o );

			}


		} catch (JsonParseException e) {
			System.out.println( "[" + getClass().getSimpleName() + "] Server has tried to pass invalid JSON" );
		} catch (NoSuchMethodException e) {
			System.out.println( "[" + getClass().getSimpleName() + "] The method: " + methodName + " does not exist" );
		} catch (IllegalAccessException e) {
			System.out.println( "[" + getClass().getSimpleName() + "] The method: " + methodName + " is not accessible from this scope." );
		} catch (InvocationTargetException e) {
			System.out.println( "[" + getClass().getSimpleName() + "] The method: \'" + methodName + "\' reports: "
					+ e.getTargetException().getMessage() + " in JSONObject string: " + params.toString() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
