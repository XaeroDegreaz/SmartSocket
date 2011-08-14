package net.smartsocket.protocols.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.smartsocket.Logger;

/**
 * The ClientCall class is a simple way to construct a JSON formatted message to send to a client.<br/><br/>
 * <b>Usage:</b><br/>
 *<code>
 *ClientCall call = new ClientCall("<b>methodNameOnClientApplication</b>");<br/>
 *call.put("<b>propertyName</b>", "<b>propertyValue</b>");<br/>
 *client.send(call);<br/>
 *</code>
 * @author XaeroDegreaz
 */
public class ClientCall {

    public transient JsonObject properties = new JsonObject();
    private static transient Gson gson = new Gson();

    /**
     * Instantiate a new ClientCall object for calling a method on a client.
     * @param method The String name of the method to be called on the client.
     */
    public ClientCall(String method) {
        properties.addProperty("m", method);
    }

    /**
     * Create or modify a property on the client call.
     * @param key The key name of the property
     * @param value The value of the property
     */
    public ClientCall put(String key, String value) {
        properties.addProperty(key, value);
        return this;
    }
    
    public ClientCall put(String key, Boolean value) {
        properties.addProperty(key, value);
        return this;
    }
    
    public ClientCall put(String key, Number value) {
        properties.addProperty(key, value);
        return this;
    }
    
    public ClientCall put(String key, Character value) {
        properties.addProperty(key, value);
        return this;
    }

    /**
     * Create or modify a property on the client call.
     * @param key The key name of the property
     * @param value The value of the property
     */
    public ClientCall put(String key, JsonElement value) {

        try {
            properties.add(key, value);
        } catch (Exception e) {
            Logger.log("Having problems creating Call: " + key + " - " + e.getMessage());
        }

        return this;
    }
    
    public static JsonElement serialize(Object obj) {
        return gson.toJsonTree(obj);
    }
}
