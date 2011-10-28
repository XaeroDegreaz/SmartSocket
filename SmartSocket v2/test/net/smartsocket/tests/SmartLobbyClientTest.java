package net.smartsocket.tests;

import com.google.gson.JsonObject;
import net.smartsocket.client.SmartLobbyClient;
import net.smartsocket.protocols.json.RemoteCall;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobbyClientTest extends SmartLobbyClient {
    
    public static void main(String[] args) {
        new SmartLobbyClientTest().run();
    }
    
    public SmartLobbyClientTest() {
        super("localhost", 8888, 100);
    }
    
    public void onLogin(JsonObject json) {
        System.out.println(json);
    }
    
}
