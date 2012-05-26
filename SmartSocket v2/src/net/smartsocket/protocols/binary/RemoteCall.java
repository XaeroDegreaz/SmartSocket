package net.smartsocket.protocols.binary;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import net.smartsocket.Logger;

/**
 * The RemoteCall class is a simple way to construct a JSON formatted message to send to a client This call is capable of sending files through the socket.<br/><br/>
 * <b>Usage:</b><br/>
 *<code>
 *RemoteCall call = new RemoteCall("<b>methodNameOnClientApplication</b>");<br/>
 *call.put("<b>propertyName</b>", "<b>propertyValue</b>");<br/>
 *client.send(call);<br/>
 *</code>
 * @author XaeroDegreaz
 */
public class RemoteCall {

	/**
	 * The core container of the RemoteCall object.
	 */
	public transient JsonObject properties = new JsonObject();
	private static transient Gson gson = new Gson();
	/**
	 * An optional file to send through the socket.
	 */
	public transient File file;

	/**
	 * Instantiate a new RemoteCall object for calling a method on a client.
	 * @param method The String name of the method to be called on the client.
	 * @param directTo The String name of the SmartSocketClient dataListener to direct this message to.
	 */
	public RemoteCall( String method, String directTo ) {
		properties.addProperty( "method", method );
		properties.addProperty( "directTo", directTo );
	}

	/**
	 * Instantiate a new RemoteCall object for calling a method on a client.
	 * @param method The String name of the method to be called on the client.
	 * @param directTo The JsonElement of the SmartSocketClient dataListener to direct this message to.
	 */
	public RemoteCall( String method, JsonElement directTo ) {
		properties.addProperty( "method", method );
		properties.addProperty( "directTo", directTo.getAsString() );
	}

	/**
	 * Instantiate a new RemoteCall object for calling a method on a client.
	 * @param method The name of the method you are going to call on the client / server
	 */
	public RemoteCall( String method ) {
		properties.addProperty( "method", method );
	}

	/**
	 * Create or modify a property on the client call.
	 * @param key The key name of the property
	 * @param value The value of the property
	 * @return RemoteCall  
	 */
	public RemoteCall put( String key, String value ) {
		properties.addProperty( key, value );
		return this;
	}

	/**
	 * Create or modify a property on the client call.
	 * @param key
	 * @param value
	 * @return RemoteCall
	 */
	public RemoteCall put( String key, Boolean value ) {
		properties.addProperty( key, value );
		return this;
	}

	/**
	 * Create or modify a property on the client call.
	 * @param key
	 * @param value
	 * @return RemoteCall
	 */
	public RemoteCall put( String key, Number value ) {
		properties.addProperty( key, value );
		return this;
	}

	/**
	 * Create or modify a property on the client call.
	 * @param key
	 * @param value
	 * @return RemoteCall
	 */
	public RemoteCall put( String key, Character value ) {
		properties.addProperty( key, value );
		return this;
	}

	/**
	 * Create or modify a property on the client call.
	 * @param key The key name of the property
	 * @param value The value of the property
	 * @return RemoteCall
	 */
	public RemoteCall put( String key, JsonElement value ) {

		try {
			properties.add( key, value );
		} catch (Exception e) {
			Logger.log( "Having problems creating Call: " + key + " - " + e.getMessage() );
		}

		return this;
	}
	
	/**
	 * Add a file to be sent along with the socket transmission.
	 * @param file The file to send
	 * @return RemoteCall
	 */
	public RemoteCall put(File file) {
		this.file = file;
		return this;
	}

	/**
	 * Serialize any object into a proper JSON Object
	 * @param obj
	 * @return
	 */
	public static JsonElement serialize( Object obj ) {
		return gson.toJsonTree( obj );
	}
	
	public Object[] toByteArray() {
		long fileSize = file.length();
		byte[] fileBytes = new byte[ (int)fileSize ];
		FileInputStream fis;
		
		try {
			fis = new FileInputStream( file );
			fis.read( fileBytes );
		} catch (Exception e) {
			Logger.log( e.getMessage() );
		}		
		
		return new Object[]{fileSize, fileBytes, properties.toString() };
	}
}
