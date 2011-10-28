package net.smartsocket.tests;

import com.google.gson.JsonObject;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.RemoteCall;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.smartlobby.SmartLobby;

/**
 *
 * @author XaeroDegreaz
 */
public class SmartLobbyServerTest extends SmartLobby {

	public static void main( String[] args ) {
		new SmartLobbyServerTest().start();
	}

	public SmartLobbyServerTest() {
		super( 8888 );
	}

	@Override
	protected void onSmartLobbyReady() {
		setConfig();
	}

	public void protocolTest( TCPClient client, JsonObject json ) {
		Logger.log( "Test received." );

		RemoteCall call = new RemoteCall( "onRoomList" );
		call.put( "roomList", RemoteCall.serialize( roomList ) );
		client.send( call );
	}
}
