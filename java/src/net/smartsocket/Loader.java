/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author XaeroDegreaz
 */
public class Loader {
    public static HashMap _constants = new HashMap();
    public static HashMap _extensions = new HashMap();

    public Loader () {
	try {
	    File file = new File("Config.xml");
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.parse(file);

	    doc.getDocumentElement().normalize();

	    NodeList constantsList = doc.getElementsByTagName("constants");
	    NodeList extensionsList = doc.getElementsByTagName("extensions");

	    NodeList constants = constantsList.item(0).getChildNodes();
	    NodeList extensions = extensionsList.item(0).getChildNodes();



	    for (int i = 0; i < constants.getLength(); i++) {
		Node e = constants.item(i);
		if(e.getNodeType() == Node.ELEMENT_NODE) {
		    _constants.put(e.getNodeName(), e.getTextContent());
		    Logger.log("Loader", "Setting constant "+e.getNodeName()+" to "+e.getTextContent());
		    
		}
	    }

	    for (int i = 0; i < extensions.getLength(); i++) {
		Node e = extensions.item(i);
		if(e.getNodeType() == Node.ELEMENT_NODE) {
		    NamedNodeMap a = e.getAttributes();

		    _extensions.put(
			    a.getNamedItem("name").getNodeValue(),
			    a.getNamedItem("port").getNodeValue()
			    );
		    Logger.log("Loader", "Setting up extension  "+a.getNamedItem("name").getNodeValue()+" to listen on "+a.getNamedItem("port").getNodeValue());
		    
		}
	    }

	}catch(Exception e) {
	    e.printStackTrace();
	    Logger.log("Loader", e.toString());
	}

    }

}
