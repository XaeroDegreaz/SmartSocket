package net.smartsocket.client;

import net.smartsocket.protocols.binary.RemoteCall;


/**
 * This will be the equivalent of the AS3 SmartLobbyClient when finished.
 * @author XaeroDegreaz
 */
public abstract class SmartLobbyClient extends SmartSocketClient {
    //# TODO - Create the necessary client routines used in SmartLobby
    
	/**
	 * 
	 * @param host
	 * @param port
	 * @param timeout
	 */
	public SmartLobbyClient(String host, int port, int timeout) {
        super(host, port, timeout);
    }

	/**
	 * 
	 * @param connectMessage
	 */
	@Override
    protected void onConnect(String connectMessage) {
        System.out.println(connectMessage);
        
        RemoteCall call = new RemoteCall("login");
        call.put("username", "Your Name");
        send(call);
    }

	/**
	 * 
	 * @param exception
	 */
	@Override
    protected void onConnectFail(Exception exception) {
        System.out.println(exception);;
    }

	/**
	 * 
	 * @param disconnectMessage
	 */
	@Override
    protected void onDisconnect(String disconnectMessage) {
        System.out.println(disconnectMessage);;
    }
    
}
