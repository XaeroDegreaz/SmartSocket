package net.smartsocket.tests;

import com.google.gson.JsonObject;
import net.smartsocket.Config;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.RemoteCall;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.smartlobby.Room;
import net.smartsocket.smartlobby.SmartLobby;
import net.smartsocket.smartlobby.events.SmartLobbyEvent;

/**
 * This is a simple class that uses SmartLobby as the base of all of its functionality.
 * This is actually where most of the actual launching and debugging of the server is done.
 * @author XaeroDegreaz
 */
public class SmartLobbyServerTest extends SmartLobby {

	/**
	 * 
	 * @param args
	 */
	public static void main( String[] args ) {
		Config.useGUI = false;
		new SmartLobbyServerTest().start();
	}

	/**
	 * 
	 */
	public SmartLobbyServerTest() {
		super( 8888 );
	}

	@Override
	protected void onSmartLobbyReady() {
		setConfig();
		//# Don't want all of the traffic messages.
		Logger.setLogLevel( Logger.DEBUG );
		
		this.addEventListener( SmartLobbyEvent.onRoomAdd, "someTest", this);
	}
	
	/**
	 * Just a little test to see if event listeners are functioning correctly.
	 * @param o
	 */
	public void someTest(Room o) {
		System.out.println("Event received; onRoomAdd event listener works.");
	}
}
