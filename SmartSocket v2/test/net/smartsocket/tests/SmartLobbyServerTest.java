/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.tests;

import com.google.gson.JsonObject;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.ClientCall;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.smartlobby.SmartLobby;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobbyServerTest extends SmartLobby {
    
    public static void main(String[] args) {
        new SmartLobbyServerTest().start();
    }
    
    public SmartLobbyServerTest() {
        super(8888);
    }

    @Override
    protected void onSmartLobbyReady() {
        setConfig();
    }
    
    public void protocolTest(TCPClient client, JsonObject json) {
        Logger.log("Test received.");
        
        ClientCall call = new ClientCall("onRoomList");
        call.put("roomList", ClientCall.serialize(roomList));
        client.send(call);
    }
    
}
