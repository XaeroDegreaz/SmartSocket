/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.protocols.json;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author XaeroDegreaz
 */
public class ClientCall extends JSONArray{
    
    JSONObject properties = new JSONObject();

    public ClientCall(String method) {
	add(method);
	add(properties);
    }
}
