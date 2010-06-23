/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

import java.io.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author XaeroDegreaz
 */
public class Loader {

    JSONObject _config = null;
    public static JSONObject _constants = new JSONObject();
    JSONArray _extensions = new JSONArray();
    private static final String CONFIG_FILE = "Config.json";
    private static final String DEFAULT_CONFIG = "{ \n"
	    + "    \"constants\":[\n"
	    + "        {\n"
	    + "            \"name\" : \"DATA_PROTOCOL\",\n"
	    + "            \"value\" : \"json\"\n"
	    + "        },\n"
	    + "        {\n"
	    + "            \"name\" : \"USE_ZLIB\",\n"
	    + "            \"value\" : false\n"
	    + "        },\n"
	    + "        {\n"
	    + "            \"name\" : \"SEND_POLICY_FILE\",\n"
	    + "            \"value\" : false\n"
	    + "        }\n"
	    + "    ]\n"
	    + "}";

    public Loader() {
	try {
	    //# First we need to open up the Config.json file so that we can parse its information
	    StringBuffer fileData = loadConfigFile();

	    _config = (JSONObject) JSONValue.parse(fileData.toString());

	    JSONArray constants = (JSONArray) _config.get("constants");

	    for (int i = 0; i < constants.size(); i++) {
		JSONObject c = (JSONObject) constants.get(i);
		this._constants.put(c.get("name").toString(), c.get("value"));
	    }

	} catch (Exception e) {
	    Logger.log("Loader", "The Config.json is corrupt. Please delete it and restart the application.");
	}

    }

    public static StringBuffer loadConfigFile() throws Exception {
	BufferedReader reader;
	try {
	    reader = new BufferedReader(new FileReader(CONFIG_FILE));
	} catch (Exception e) {
	    reader = createConfigFile();
	}

	StringBuffer fileData = new StringBuffer();

	char[] buffer = new char[4096];
	int numRead = 0;

	while ((numRead = reader.read(buffer)) != -1) {
	    fileData.append(buffer, 0, numRead);
	}
	reader.close();

	return fileData;
    }

    public static BufferedReader createConfigFile() {

	BufferedReader reader = null;
	Writer output = null;

	try {
	    output = new BufferedWriter(new FileWriter(CONFIG_FILE));
	    output.write(DEFAULT_CONFIG);
	    output.close();

	    reader = new BufferedReader(new FileReader(CONFIG_FILE));
	} catch (Exception e) {
	    Logger.log("Loader", "Unable to write the configuration file!");
	    return reader;
	}
	
	return reader;
    }
}
