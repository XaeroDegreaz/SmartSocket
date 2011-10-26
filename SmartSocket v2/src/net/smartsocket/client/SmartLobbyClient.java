package net.smartsocket.client;

import net.smartsocket.protocols.json.RemoteCall;


/**
 *
 * @author XaeroDegreaz
 */
public abstract class SmartLobbyClient extends SmartSocketClient {
    //# TODO - Create the necessary client routines used in SmartLobby
    
    public SmartLobbyClient(String host, int port, int timeout) {
        super(host, port, timeout);
    }

    @Override
    protected void onConnect(String connectMessage) {
        System.out.println(connectMessage);
        
        RemoteCall call = new RemoteCall("login");
        call.put("username", "Your Name");
        send(call);
    }

    @Override
    protected void onConnectFail(Exception exception) {
        System.out.println(exception);;
    }

    @Override
    protected void onDisconnect(String disconnectMessage) {
        System.out.println(disconnectMessage);;
    }
    
}
