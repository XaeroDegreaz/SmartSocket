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

    public Loader() {
	try {
	    //# First we need to open up the Config.json file so that we can parse its information
	    StringBuffer fileData = new StringBuffer();
	    BufferedReader reader = new BufferedReader(new FileReader("Config.json"));
	    char[] buffer = new char[4096];
	    int numRead = 0;

	    while ((numRead = reader.read(buffer)) != -1) {
		fileData.append(buffer, 0, numRead);
	    }
	    reader.close();

	    _config = (JSONObject)JSONValue.parse(fileData.toString());

	    JSONArray constants = (JSONArray) _config.get("constants");

	    for (int i = 0; i < constants.size(); i++) {
		JSONObject c = (JSONObject) constants.get(i);
		this._constants.put(c.get("name").toString(), c.get("value"));
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log("Loader", e.toString());
	}

    }
}
