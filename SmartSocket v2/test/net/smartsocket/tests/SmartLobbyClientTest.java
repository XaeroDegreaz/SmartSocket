package net.smartsocket.tests;

import com.google.gson.JsonObject;
import net.smartsocket.client.SmartLobbyClient;
import net.smartsocket.protocols.json.RemoteCall;

/**
 * A simple test client not really included in the main packages.
 * Used for debugging, and launching a GUI that will interact with a SmartLobby server.
 * @author XaeroDegreaz
 */
public class SmartLobbyClientTest extends SmartLobbyClient {
    
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
        new SmartLobbyClientTest().run();
    }
    
	/**
	 * Connect to a SmartSocket server running on the following specifics.
	 */
	public SmartLobbyClientTest() {
        super("localhost", 8888, 100);
    }
    
	/**
	 * 
	 * @param json
	 */
	public void onLogin(JsonObject json) {
        System.out.println(json);
    }
    
}
