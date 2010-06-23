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
    
    private JSONObject properties = new JSONObject();

    /**
     * Instantiate a new ClientCall object for calling a method on a client.
     * @param method The String name of the method to be called on the client.
     */
    public ClientCall(String method) {
	add(method);
	add(properties);
    }

    /**
     * Create or modify a property on the client call.
     * @param key The key name of the property
     * @param value The value of the property
     */
    public void put(Object key, Object value) {
	properties.put(key, value);
    }
}
