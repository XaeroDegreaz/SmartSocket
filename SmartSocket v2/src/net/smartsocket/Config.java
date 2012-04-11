package net.smartsocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.*;
import java.net.URL;

/**
 * The Config class loads and/or generates a configuration file that helps make the server
 * a little more customizable without having to recompile the server each time for a new change.
 * The Config class has the ability to ensure that no user changes are lost when upgrading to a
 * newer version of configuration file.
 * @author XaeroDegreaz
 */
public class Config {

	/**
	 * Boolean showing if configuration is loaded. This is needed because when extensions are launched,
	 * the try to launch configuration, and we don't need it loaded multiple times
	 */
	private static boolean configLoaded = false;
	/**
	 * The JSONObject that holds all of the configuration information for the server.
	 */
	public static JsonObject configuration;
	//# Below are the configuration values.
	/**
	 * This configuration property contains the JSONObject pertaining to the crossdomain
	 * policy files used in Flash.<br/><br/>
	 * <b>Values</b><br/>
	 * Boolean <b>enabled</b> - Default true<br/>
	 * String <b>content</b> - Generic XML crossdomain policy file.
	 */
	public static JsonObject crossdomainPolicyFile;
	/**
	 * This configuration property contains the JSONObject pertaining to the TCPExtensions
	 * overall config options.<br/><br/>
	 * <b>Values</b><br/>
	 * String <b>protocol</b> - Acceptable values: json, xml(todo), raw(todo)<br/>
	 * String <b>raw-protocol-delimiters</b> - If the raw protocol is selected, then these will be the delimiters(todo).
	 */
	public static JsonObject tcpExtensions;
	/**
	 * This configuration property contains the JSONObject pertaining to the UDPExtensions
	 * overall config options.<br/><br/>
	 * <b>Values</b><br/>
	 * String <b>protocol</b> - Acceptable values: json, xml(todo), raw(todo)<br/>
	 * String <b>raw-protocol-delimiters</b> - If the raw protocol is selected, then these will be the delimiters(todo).
	 */
	public static JsonObject udpExtensions;
	/**
	 * This configuration property contains the JSONObject pertaining to the auto update
	 * feature.<br/><br/>
	 * <b>Values</b><br/>
	 * Boolean <b>enabled</b> - Default set to false<br/>
	 * String <b>update-url</b> - Location should point to GitHup repo for SmartSocket.
	 */
	public static JsonObject autoUpdate;
	
	public static boolean useGUI = true;
	//#####################################

	/**
	 * Load the configuration file for our server. This method is thread-safe.
	 */
	public synchronized static void load() {
		//# Ensure config file not already loaded...
		if ( configLoaded ) {
			return;
		}

		//# Read configuration file, and assign it to our configuration JSONObject
		try {
			configuration = (JsonObject) new JsonParser().parse( readFile( "config.json" ).toString() );
		} catch (JsonParseException e) {
			//# Someone must have modified (poorly) the JSON configuration file.
			Logger.log( "Malformed JSON in the configuration file.", Logger.CRITICAL );
		} catch (FileNotFoundException e) {
			//# Couldn't find the config file, so we write our default one.
			Logger.log( "Cannot find the configuration file. Creating default config file.", Logger.CRITICAL );
			configuration = (JsonObject) new JsonParser().parse( create().toString() );
		}

		//# Go ahead and assign these values. This is more for developers of SmartSocket than end-user use.
		try {
			Config.autoUpdate = configuration.getAsJsonObject( "auto-update" );
			Config.crossdomainPolicyFile = configuration.getAsJsonObject( "crossdomain-policy-file" );
			Config.tcpExtensions = configuration.getAsJsonObject( "tcp-extensions" );
			Config.udpExtensions = configuration.getAsJsonObject( "udp-extensions" );
		} catch (Exception e) {
		}

		Logger.log( "Configuration loading is complete.", Logger.CRITICAL );
		configLoaded = true;
	}

	/**
	 * Create a configuration file based on the internally stored configuration.
	 * @return The StringBuffer object associated with the internal JSON configuration file.
	 */
	private static StringBuffer create() {
		Logger.log( "Creating default configuration file 'config.json'...", Logger.CRITICAL );
		StringBuffer fileData = getLocalConfig();

		try {
			JsonObject json = (JsonObject) new JsonParser().parse( fileData.toString() );

			//# Remove config version from the output
			//# This config string will be used internally for
			//# tracking versions of cingif files when writing new ones
			//# or receiving new ones.
			json.remove( "config-version" );

			//# Write configuration file.
			FileOutputStream out = new FileOutputStream( "config.json" );
			out.write( json.toString().getBytes() );
			out.close();

		} catch (JsonParseException e) {
			//# Should never get here because the config file is packaged internaly...
			//# If it fails, probably a developer not formatting the config file properly.
			Logger.log( "Compiling and creating the config file failed. Ensure you have properly formatted JSON data before recompiling your extension.", Logger.CRITICAL );
		} catch (IOException e) {
			Logger.log( "Failed to write the configuration file: " + e.getMessage(), Logger.CRITICAL );
		}

		return fileData;
	}

	/**
	 * This method takes a BufferedReader object from a file and loads it into a String.
	 * @param reader The target BufferedReader object
	 * @return The StringBuffer object representative of the BufferedReader
	 */
	private static StringBuffer readFile( BufferedReader reader ) {
		StringBuffer fileData = new StringBuffer();
		char[] buffer = new char[1024];
		int readData = 0;

		try {
			while ( (readData = reader.read( buffer )) != -1 ) {
				fileData.append( buffer, 0, readData );
			}
			reader.close();
		} catch (Exception e) {
			Logger.log( "Unable to load desired stream.", Logger.CRITICAL );
		}

		return fileData;
	}

	/**
	 * This method takes an InputStream object from a file, creates BufferedReader,
	 * then passes it to readFile(BufferedReader reader) for processing.
	 * @param stream The target InputStream
	 * @return The StringBuffer object representative of the InputStream
	 */
	private static StringBuffer readFile( InputStream stream ) {
		return readFile( new BufferedReader( new InputStreamReader( stream ) ) );
	}

	/**
	 * This method takes a String name of a file, creates BufferedReader,
	 * then passes it to readFile(BufferedReader reader) for processing.
	 * @param string 
	 * @return The StringBuffer object representative of the loaded file
	 * @throws FileNotFoundException  
	 */
	public static StringBuffer readFile( String string ) throws FileNotFoundException {
		return readFile( new BufferedReader( new FileReader( string ) ) );
	}
	//# TODO - Figure out the best way to do this configuration checking stuff...

	/**
	 * This method basically checks our configuration file against the configuration file
	 * on the repository to ensure that the developers are always up to date with the latest
	 * configuration file.
	 */
	private static void checkConfig() {
		JsonObject localConfig = (JsonObject) new JsonParser().parse( getLocalConfig().toString() );
		String version = localConfig.get( "config-version" ).toString();
		String[] revisions = version.split( "." );
		int lMajor = Integer.parseInt( revisions[0] );
		int lMinor = Integer.parseInt( revisions[1] );
		int lBug = Integer.parseInt( revisions[2] );
	}

	private static StringBuffer getLocalConfig() {
		InputStream localConfigFileStream = null;
		try {
			//# Grab the file from our resources package.
			URL localConfigFile = Config.class.getResource( "/net/smartsocket/resources/config.json" );
			localConfigFileStream = localConfigFile.openStream();
		} catch (Exception e) {
			Logger.log( "Having a hard time locating internal config file. The program may not work properly.", Logger.CRITICAL );
		}
		return readFile( localConfigFileStream );
	}
}
