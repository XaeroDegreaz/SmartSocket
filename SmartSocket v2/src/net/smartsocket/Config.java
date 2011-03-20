/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket;
import java.io.*;
import java.net.URL;
import org.json.*;


/**
 *
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
    public static JSONObject configuration;

    //# Below are the configuration values.
    /**
     * This configuration property contains the JSONObject pertaining to the crossdomain
     * policy files used in Flash.<br/><br/>
     * <b>Values</b><br/>
     * Boolean <b>enabled</b> - Default true<br/>
     * String <b>content</b> - Generic XML crossdomain policy file.
     */
    public static JSONObject crossdomainPolicyFile;
    /**
     * This configuration property contains the JSONObject pertaining to the TCPExtensions
     * overall config options.<br/><br/>
     * <b>Values</b><br/>
     * String <b>protocol</b> - Acceptable values: json, xml(todo), raw(todo)<br/>
     * String <b>raw-protocol-delimiters</b> - If the raw protocol is selected, then these will be the delimiters(todo).
     */
    public static JSONObject tcpExtensions;
    /**
     * This configuration property contains the JSONObject pertaining to the UDPExtensions
     * overall config options.<br/><br/>
     * <b>Values</b><br/>
     * String <b>protocol</b> - Acceptable values: json, xml(todo), raw(todo)<br/>
     * String <b>raw-protocol-delimiters</b> - If the raw protocol is selected, then these will be the delimiters(todo).
     */
    public static JSONObject udpExtensions;
    /**
     * This configuration property contains the JSONObject pertaining to the auto update
     * feature.<br/><br/>
     * <b>Values</b><br/>
     * Boolean <b>enabled</b> - Default set to false<br/>
     * String <b>update-url</b> - Location should point to GitHup repo for SmartSocket.
     */
    public static JSONObject autoUpdate;
    //#####################################

    /**
     * Load the configuration file for our server. This method is thread-safe.
     */
    public synchronized static void load() {
        //# Ensure config file not already loaded...
        if(configLoaded) {
            return;
        }
        
        //# Read configuration file, and assign it to our configuration JSONObject
        try {
            configuration = new JSONObject( readFile("config.json").toString() );
        }catch(JSONException e) {
            //# Someone must have modified (poorly) the JSON configuration file.
            Logger.log("Malformed JSON in the configuration file.");
        }catch(FileNotFoundException e) {
            //# Couldn't find the config file, so we write our default one.
            Logger.log("Cannot find the configuration file. Creating default config file.");
            configuration = new JSONObject ( create() );
        }

        //# Go ahead and assign these values. This is more for developers of SmartSocket than end-user use.
        try {            
            Config.autoUpdate = configuration.getJSONObject("auto-update");
            Config.crossdomainPolicyFile = configuration.getJSONObject("crossdomain-policy-file");
            Config.tcpExtensions = configuration.getJSONObject("tcp-extensions");
            Config.udpExtensions = configuration.getJSONObject("udp-extensions");
        }catch(Exception e) {
            
        }

        Logger.log("Configuration loading is complete.");
        configLoaded = true;
    }
    /**
     * Create a configuration file based on the internally stored configuration.
     * @return The StringBuffer object associated with the internal JSON configuration file.
     */
    private static StringBuffer create() {
        Logger.log("Creating default configuration file 'config.json'...");
        StringBuffer fileData = null;

        try {
            //# Grap the file from our resources package.
            URL localConfigFile = Config.class.getResource("/net/smartsocket/resources/config.json");
            InputStream localConfigFileStream = localConfigFile.openStream();
            fileData = readFile(localConfigFileStream);

            //# Write configuration file.
            FileOutputStream out = new FileOutputStream("config.json");
            out.write(fileData.toString().getBytes());
            out.close();

        }catch(Exception e) {
            //# Should never get here because the config file is packaged internaly...
            Logger.log("Having a hard time locating internal config file. The program may not work properly.");
        }
        
        return fileData;
    }

    /**
     * This method takes a BufferedReader object from a file and loads it into a String.
     * @param reader The target BufferedReader object
     * @return The StringBuffer object representative of the BufferedReader
     */
    private static StringBuffer readFile(BufferedReader reader) {
        StringBuffer fileData = new StringBuffer();
        char[] buffer = new char[1024];
        int readData = 0;

        try {
            while((readData = reader.read(buffer)) != -1) {
                fileData.append(buffer, 0, readData);
            }
            reader.close();
        }catch(Exception e) {
            Logger.log("Unable to load desired stream.");
        }

        return fileData;
    }

    /**
     * This method takes an InputStream object from a file, creates BufferedReader,
     * then passes it to readFile(BufferedReader reader) for processing.
     * @param stream The target InputStream
     * @return The StringBuffer object representative of the InputStream
     */
    private static StringBuffer readFile(InputStream stream) {
        return readFile( new BufferedReader( new InputStreamReader(stream) ) );
    }

    /**
     * This method takes a String name of a file, creates BufferedReader,
     * then passes it to readFile(BufferedReader reader) for processing.
     * @param stream The target file, by string name.
     * @return The StringBuffer object representative of the loaded file
     */
    private static StringBuffer readFile(String string) throws FileNotFoundException {
        return readFile( new BufferedReader( new FileReader(string) ) );
    }
}
