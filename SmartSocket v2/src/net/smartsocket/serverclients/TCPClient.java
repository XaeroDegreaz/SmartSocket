package net.smartsocket.serverclients;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import net.smartsocket.*;
import net.smartsocket.serverextensions.TCPExtension;
import net.smartsocket.forms.StatisticsTracker;
import net.smartsocket.protocols.json.RemoteCall;
import net.smartsocket.smartlobby.User;

/**
 * The TCPClient class controls the dedicated thread, and input/output for the TCP client.
 * This class does not need to be instantiated by any end users; it's created automatically when the
 * client is accepted by the server socket.
 * @author XaeroDegreaz
 */
public class TCPClient extends AbstractClient {

	/**
	 * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
	 */
	//private static Deque<TCPClient> clients = new LinkedList<TCPClient>();
	private static Map<Object, TCPClient> clients = Collections.synchronizedMap( new HashMap<Object, TCPClient>() );
	private static long inboundBytes = 0;
	private static long outboundBytes = 0;
	//# These are the private vars that never need to be accessed period outside this class.
	private DataInputStream in;
	private DataOutputStream out;
	private Socket _client = null;
	/**
	 * The IP address of this TCPClient
	 */
	private String _ipAddress;
	private TCPExtension _extension = null;

	/**
	 * Creates a new thread for a client on a TCPExtension object.
	 * @param client
	 * @param extension
	 */
	public TCPClient( Socket client, TCPExtension extension ) {
		this._client = client;
		this._extension = extension;

		String cleanIp = client.getRemoteSocketAddress().toString().split( ":", 2 )[0].replace( "/", "" );

		this._ipAddress = cleanIp;
		Logger.log( "Client connected from: " + _ipAddress );
	}

	@Override
	/**
	 * The TCPClient's main thread running method. This method basically sets up the
	 * read operations and thins of that nature.
	 */
	public void run() {
		Logger.log( "There are currently: " + Thread.activeCount() + " thread active in this group." );
		synchronized (this) {
			try {
				//# Setup the uniqueId for this client.
				this.setUniqueId( this.toString() );
			} catch (Exception ex) {
				//# This should never happen since each thread name is unique.......
			}

			if ( Config.useGUI ) {
				//# Update the interface to show new connection
				StatisticsTracker.updateClientsConnectedLabel();
			}

			//# Send an onconnect message to the extension
			_extension.onConnect( this );
			//# Method used to setup initial streams, ie string, json, xml, binary streams, etc
			setupSession();
		}
		//# Begin the read loop for this client's streams..
		sendPolicyFile();
		read();
		destroySession();
	}

	private void sendPolicyFile() {
		byte nullByte = '\u0000';
		try {
			if ( Config.crossdomainPolicyFile.get( "enabled" ).getAsBoolean() ) {
				Logger.log( "Sending crossdomain policy file." );
				out.writeUTF( Config.crossdomainPolicyFile.get( "content" ).getAsString() );
				out.write( nullByte );
				out.flush();
			} else {
				Logger.log( "Not sending cross-domain-policy: " + Config.crossdomainPolicyFile );
			}
		} catch (Exception e) {
			Logger.log( "Error sending crossdomain policy file: " + e.getMessage() );
		}
	}

	/**
	 * Setup the client session
	 */
	private void setupSession() {
		//# Initialize all of the input and output streams here...
		try {
			in = new DataInputStream( _client.getInputStream() );
			out = new DataOutputStream( _client.getOutputStream() );
		} catch (Exception e) {
			//# Really should never get here, but just in case...
			Logger.log( e );
			destroySession();
		}
	}

	/**
	 * Destroy the client session and remove the thread.
	 */
	private void destroySession() {
		synchronized (this) {
			//# Send onDisconnect message to our extension. This should happen before removing the client completely
			//# that way our extension still has a valid TCPClient object to use on other operations
			_extension.onDisconnect( this );

			//# Remove this client from the thread deque
			clients.remove( this.uniqueId );
			_extension.removeClient( this );

			if ( Config.useGUI ) {
				//# Update the interface to show new connection
				StatisticsTracker.updateClientsConnectedLabel();
			}

			//# Tidy up our resources
			try {
				out.close();
				in.close();
				_client.close();
			} catch (Exception e) {
				//# Should never get here, but just in case
				Logger.log( "Having problems closing streams for thread: " + Thread.currentThread().getId() + " - " + e.getMessage() );
			}
		}
	}

	/**
	 * Infinite read loop until the client closes their connections
	 */
	private void read() {
		String input = null;
		byte[] bytes = null;
		int red;
		try {
			while ( (red = in.read()) != -1 ) {
				Logger.log( "Client input stream open." );

				long fileLength = getFileLength();
				byte[] fileBytes = getFileBytes( fileLength );
				String jsonHeader = getJsonHeader();

				Logger.log( "Server message: " + fileLength + " / " + jsonHeader );

				process( jsonHeader, fileBytes );
			}
			Logger.log( "Client input stream closed." + red );
		} catch (Exception e) {
			Logger.log( "Client " + Thread.currentThread().getId() + " disconnected." + e.getMessage() );
			e.printStackTrace();
		}

	}

	public long getFileLength() {
		
		try {
			return in.readLong();
		} catch (Exception e) {
		}
		
		return 0;
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

	/**
	 * The method processes incoming data from the client, and routes them to the proper methods on the server extension.
	 * @param line
	 */
	private void process( String line, byte[] fileBytes ) {
		//# Add the size of this line of text to our inboundByte variable for gui usage
		setInboundBytes( getInboundBytes() + line.getBytes().length + fileBytes.length );
		Logger.log( "Client " + Thread.currentThread().getId() + " says: " + line );

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
				Class[] classes = new Class[2];
				classes[0] = TCPClient.class;
				classes[1] = JsonObject.class;
				
				//# First let's send this message to the extensions onDataSpecial to see if
				//# the extension wants to process this message in its own special way.
				if ( _extension.onDataSpecial( this, methodName, params ) == false ) {

					//# Try to call the method on the desired extension class
					//# This is only executed if onDataSpecial returns false on our extension.
					Object[] o = { this, params };
					m = _extension.getExtension().getMethod( methodName, classes );
					m.invoke( _extension.getExtensionInstance(), o );
				}
			} else {
				//# We are here becuase we are also trying to pass a file to a method on our extension for writing.
				//# Get ready to create dynamic method call to extension
				Class[] classes = new Class[3];
				classes[0] = TCPClient.class;
				classes[1] = JsonObject.class;
				classes[2] = byte[].class;
				
				//# First let's send this message to the extensions onDataSpecial to see if
				//# the extension wants to process this message in its own special way.
				if ( _extension.onDataSpecial( this, methodName, params ) == false ) {

					//# Try to call the method on the desired extension class
					//# This is only executed if onDataSpecial returns false on our extension.
					Object[] o = { this, params, fileBytes };
					m = _extension.getExtension().getMethod( methodName, classes );
					m.invoke( _extension.getExtensionInstance(), o );
				}
			}

		} catch (JsonParseException e) {
			Logger.log( "[" + _extension.getExtensionName() + "] Client has tried to pass invalid JSON" );
		} catch (NoSuchMethodException e) {
			Logger.log( "[" + _extension.getExtensionName() + "] The method: " + methodName + " does not exist" );
		} catch (IllegalAccessException e) {
			Logger.log( "[" + _extension.getExtensionName() + "] The method: " + methodName + " is not accessible from this scope." );
		} catch (InvocationTargetException e) {
			Logger.log( "[" + _extension.getExtensionName() + "] The method: \'" + methodName + "\' reports: "
					+ e.getTargetException().getMessage() + " in JSONObject string: " + params.toString() );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The RemoteCall message to send to this client. This method should no longer be used <b>unless</b>
	 * your client is a Flash client using the ActionScript 3.0 SmartSocket Client API, or you need
	 * text-only data transfers.
	 * @param message
	 * @see net.smartsocket.protocols.json.RemoteCall
	 * @deprecated Use send(net.smartsocket.protocols.binary.RemoteCall call) for non-Flash clients.
	 */
	public void send( net.smartsocket.protocols.json.RemoteCall message ) {
		//# Add the size of this line of text to our inboundByte variable for gui usage
		setOutboundBytes( getOutboundBytes() + message.properties.toString().getBytes().length );
		try {
			out.write( (message.properties.toString() + _extension.getNewlineCharacter()).getBytes() );
			out.flush();
		} catch (IOException ex) {
			java.util.logging.Logger.getLogger( TCPClient.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}

	/**
	 * The RemoteCall message to send to this client. This send method is capable of sending files through
	 * the socket as well. This send method is not compatible with clients using the ActionScript 3.0 SmartSocket Client API
	 * @param message
	 * @see net.smartsocket.protocols.binary.RemoteCall
	 */
	public void send( net.smartsocket.protocols.binary.RemoteCall call ) {
		System.out.println( "OUTGOING: " + call.properties.toString() );
		try {
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
			
			if(fileBytes.length != 0) {
				out.write( fileBytes );
			}
			
			out.writeUTF( call.properties.toString() );
			
			out.flush();
		} catch (Exception e) {
			System.err.println( "Write error (" + e + "): " + call );
		}
	}

	/**
	 * Send a RemoteCall to a selected list of TCPClients
	 * @param userList
	 * @param message 
	 */
	public static void send( Map<String, User> userList, RemoteCall message ) {
		for ( Map.Entry<String, User> user : userList.entrySet() ) {
			user.getValue().getTcpClient().send( message );
		}
	}

	/**
	 * A list of all TCPClient objects that are running across all extensions
	 * @return the clients
	 */
	public static Map<Object, TCPClient> getClients() {
		return clients;
	}

	/**
	 * The number of inbound bytes that have been received since the last wipe on the StatisticsTracker poll.
	 * @return the inboundBytes
	 */
	public static long getInboundBytes() {
		return inboundBytes;
	}

	/**
	 * The number of inbound bytes that have been received since the last wipe on the StatisticsTracker poll.
	 * @param aInboundBytes the inboundBytes to set
	 */
	public static void setInboundBytes( long aInboundBytes ) {
		if ( Config.useGUI ) {
			inboundBytes = aInboundBytes;
		}
	}

	/**
	 * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
	 * @return the outboundBytes
	 */
	public static long getOutboundBytes() {
		return outboundBytes;
	}

	/**
	 * The number of outbound bytes that have been received since the last wipe on the StatisticsTracker poll.
	 * @param aOutboundBytes the outboundBytes to set
	 */
	public static void setOutboundBytes( long aOutboundBytes ) {
		if ( Config.useGUI ) {
			outboundBytes = aOutboundBytes;
		}
	}

	/**
	 * This method assigns a unique identifier to this client, like a login name, or other object.
	 * This method also adds the client to the clients list both for the static TCPClient.clients
	 * as well as the client list for the TCPExtension instance.
	 * @param uniqueId the uniqueId to set
	 * @throws Exception  
	 */
	public void setUniqueId( Object uniqueId ) throws Exception {
		//# First we add the new key, but check to make sure it's not in use.
		if ( !TCPClient.getClients().containsKey( uniqueId ) ) {
			//Logger.log("Unique ID change successful: "+this.uniqueId+"=>"+uniqueId);
			TCPClient.getClients().put( uniqueId, this );
			_extension.addClient( this );
		} else {
			throw new Exception( "Unique identifier " + uniqueId + " already in use." );
		}

		//# Remove the old one.
		if ( TCPClient.getClients().containsKey( this.uniqueId ) ) {
			Logger.log( "Client already had uniqueId. Cleaning up old resource." );
			TCPClient.getClients().remove( this.uniqueId );
			_extension.removeClient( this );
		}

		//# Finally, set the new id.
		this.uniqueId = uniqueId;
	}

	/**
	 * The IP address of this TCPClient
	 * @return the _ipAddress
	 */
	public String getIpAddress() {
		return _ipAddress;
	}

	/**
	 * The IP address of this TCPClient
	 * @param ipAddress the _ipAddress to set
	 */
	public void setIpAddress( String ipAddress ) {
		this._ipAddress = ipAddress;
	}
}
