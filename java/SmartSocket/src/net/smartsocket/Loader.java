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
    JSONObject _constants = new JSONObject();
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

	    try {
		File u;
		u = new File("" + _constants.get("EXTENSION_DIR"));
		ClassPathHacker.addFile(u);
	    } catch (Exception e) {
		e.printStackTrace();
		Logger.log("Loader", "Error setting classpath.");
	    }

	    //# Setup the server to compile classes.
	    com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();

	    //# Setup extensions...
	    _extensions = (JSONArray) _config.get("extensions");
	    
	    for (int i = 0; i < _extensions.size(); i++) {
		JSONObject e = (JSONObject) _extensions.get(i);
		System.out.println(i);

		if ((Boolean)e.get("enabled") == true) {
		    if ((String)e.get("extends") == null) {
			Logger.log("Loader", "No parent class for " + e.get("name"));
		    }else {
			String[] f = new String[]{"./" + _constants.get("EXTENSION_DIR") + "/" + e.get("extends") + "/" + e.get("extends") + ".java"};
			javac.compile(f);
			
			Logger.log("Loader", "Compiling parent extension " + e.get("extends") + " for " + e.get("name"));
		    }

		    String[] f = new String[]{"./" + _constants.get("EXTENSION_DIR") + "/" + e.get("name") + "/" + e.get("name") + ".java"};
		    javac.compile(f);
		    Logger.log("Loader", "Compiling extension  " + e.get("name") + " to listen on " + e.get("port"));

		}else {
		    Logger.log("Loader", "Should be trying to remove "+i+" : "+_extensions.get(i).toString());
		   // _extensions.remove(i);
		    //i--;
		}
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    Logger.log("Loader", e.toString());
	}

    }
}
