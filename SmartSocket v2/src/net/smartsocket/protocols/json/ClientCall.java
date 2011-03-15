package net.smartsocket.protocols.json;

import net.smartsocket.Logger;
import org.json.*;

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
public class ClientCall extends JSONArray{
    
    private JSONObject properties = new JSONObject();

    /**
     * Instantiate a new ClientCall object for calling a method on a client.
     * @param method The String name of the method to be called on the client.
     */
    public ClientCall(String method) {
	put(method);
	put(properties);
    }

    /**
     * Create or modify a property on the client call.
     * @param key The key name of the property
     * @param value The value of the property
     */
    public ClientCall put(String key, Object value) {
        
        try {
            properties.put(key, value);
        }catch(Exception e ) {
            Logger.log("Having problems creating Call: "+key+" - "+e.getMessage());
        }
	
	return this;
    }
}
