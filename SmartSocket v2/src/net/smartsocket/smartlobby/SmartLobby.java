package net.smartsocket.smartlobby;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.smartsocket.Config;
import net.smartsocket.Logger;
import net.smartsocket.forms.ConsoleForm;
import net.smartsocket.forms.ExtensionConsole;
import net.smartsocket.serverclients.TCPClient;
import net.smartsocket.serverextensions.TCPExtension;
import net.smartsocket.protocols.json.RemoteCall;

/**
 * The SmartLobby class is a class that controls and handles multi-user
 * connectivity via a lobby, chat, game matchmaking, user-list, friends-list, etc.
 * This can be as simple as a chat room, or a full fledged multi-player game.
 * @author XaeroDegreaz
 */
public abstract class SmartLobby extends TCPExtension {

	protected Map<String, User> userList = Collections.synchronizedMap( new HashMap<String, User>() );
	protected Map<String, Room> roomList = Collections.synchronizedMap( new HashMap<String, Room>() );
	protected JsonObject config;
	protected Gson gson = new Gson();
	//private SmartLobby extension;

	public SmartLobby( int port ) {
		super( port );
		//this.extension = extension;
	}

	@Override
	public void onExtensionReady() {
		//ConsoleForm.tabbedPane.add("SmartLobby", new ExtensionConsole("SmartLobby"));
		ConsoleForm.tabbedPane.insertTab( "SmartLobby", null, new ExtensionConsole( "SmartLobby" ), null, 1 );
		ConsoleForm.tabbedPane.insertTab( "Room", null, new ExtensionConsole( "Room" ), null, 2 );
		ConsoleForm.tabbedPane.insertTab( "User", null, new ExtensionConsole( "User" ), null, 3 );
		onSmartLobbyReady();
	}

	/**
	 * Must be implemented by extending classes.
	 */
	protected abstract void onSmartLobbyReady();

	protected void setConfig( String file, String defaultRoomsKey ) {
		try {
			config = (JsonObject) new JsonParser().parse( Config.readFile( file ).toString() );
			setDefaultRooms( (JsonArray) config.get( defaultRoomsKey ) );
		} catch (FileNotFoundException e) {
			Logger.log( "Cannot find the SmartLobby configuration file \'" + file + "\'." );
		} catch (JsonParseException e) {
			Logger.log( "Malformed JSONObject in the SmartLobby config file: " + e.getMessage() );
		}
	}

	protected void setConfig() {
		setConfig( "SmartLobbyConfig.json", "default-rooms" );
	}

	protected void setConfig( JsonObject configJSON ) {
		config = configJSON;
		Logger.log( "Custom JSON Object being used for the SmartLobby configuration!" );
	}

	@Override
	public void onConnect( TCPClient client ) {
		//# No need to really do anything here.
	}

	@Override
	public void onDisconnect( TCPClient client ) {
		//# Remove them from their room, if they are in one
		User user = getUserByTCPClient( client );
		//# This is mostly a check for the crossdomain request;
		//# we don't want to try to remove a non-existant user
		if ( user != null ) {
			try {
				User u = userList.remove( user.getUsername() );
				user.getRoom().onUserLeave( user );
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * We use the special onDataSpecial in our SmartLobby extension to route Room
	 * and User events to their designated methods instead of handling them here in
	 * the main extension, and re-passing them to the User or Room object, when necessary.
	 * We return true when onDataSpecial is handling the routing of the call, or false otherwise.
	 * @param client The client sending this message
	 * @param methodName The method to execute
	 * @param params The parameters to send to said method, in JSONObject form
	 */
	@Override
	public boolean onDataSpecial( TCPClient client, String methodName, JsonObject params ) {
		User user = getUserByTCPClient( client );

		//# No need to continue with processing onDataSpecial if this client is not logged in
		//# We simply return out, and let the TCPClient object try to find the method.
		if ( user == null ) {
			return false;
		}

		System.out.println( user );
		Method m = null;

		Class[] c = new Class[2];

		c[0] = User.class;
		c[1] = JsonObject.class;

		Object[] o = { user, params };

		try {
			if ( params.get( "slDataListener" ).getAsString().equals( "room" ) ) {
				m = Room.class.getMethod( methodName, c );
				m.invoke( user.getRoom(), o );
				return true;
			} else if ( params.get( "slDataListener" ).getAsString().equals( "user" ) ) {
				m = User.class.getMethod( methodName, c );
				m.invoke( user, o );
				return true;
			}
		} catch (NoSuchMethodException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		} catch (InvocationTargetException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return false;
	}

	private void setDefaultRooms( JsonArray array ) {
		//# Loop through the array and create some rooms
		//# no username need be set; owner will be null wich will suffice
		for ( int i = 0; i < array.size(); i++ ) {
			JsonObject r = (JsonObject) array.get( i );

			Room rm = new Room( r, this, new User( "Server" ) );
			roomList.put( r.get( "name" ).getAsString(), rm );
		}
	}

	/**
	 * This is a very simple login mechanism that accepts a string name
	 * and creates a User object, then places it in the Collection.
	 * it verifies that there are no other users logged in with the same name beforehand.
	 * If you want more complex login mechanisms, please override this
	 * method in your SmartLobby extension, and be sure to call isUsernameUnique(TCPClient, JsonObject)
	 * in your overridden method to *ensure* that your client is logging in with a unique identifier.
	 * before you
	 * @param client
	 * @param json 
	 */
	public void login( TCPClient client, JsonObject json ) {
		isUsernameUnique( client, json );
	}

	protected Boolean isUsernameUnique( TCPClient client, JsonObject json ) {
		//# Perform some login logic here to see if the username is already in use.
		try {
			client.setUniqueId( json.get( "username" ).getAsString() );
			userList.put( client.getUniqueId().toString(), new User( client, this ) );

			RemoteCall call = new RemoteCall( "onLogin", json.get( "directTo" ).getAsString() );
			call.put( "username", json.get( "username" ).getAsString() );
			client.send( call );

		} catch (Exception e) {
			Logger.log( "SmartLobby username taken: " + e );

			RemoteCall call = new RemoteCall( "onLoginError" );
			call.put( "error", "Username taken." );
			client.send( call );
			return false;
		}

		return true;
	}

	/**
	 * Send a full list of users in all rooms to the specified client
	 * @param client
	 * @param json 
	 */
	public void getFullUserList( TCPClient client, JsonObject json ) {
	}

	/**
	 * Send a full room list to the client
	 * @param client
	 * @param json 
	 */
	public void getRoomList( TCPClient client, JsonObject json ) {
		RemoteCall call = new RemoteCall( "onRoomList" );
		call.put( "roomList", RemoteCall.serialize( roomList ) );
		client.send( call );
	}

	public void createRoom( TCPClient client, JsonObject json ) {
		User user = getUserByTCPClient( client );

		//# Make sure that only one room with same name registered
		if ( roomList.containsKey( json.get( "name" ).getAsString() ) ) {
			RemoteCall call = new RemoteCall( "onCreateRoomError" );
			call.put( "message", "A room already exists with that name." );
			client.send( call );
			return;
		}

		//# Make sure this user is not the owner of another room		
		if ( user.getRoom().getOwner() == user ) {
			RemoteCall call = new RemoteCall( "onCreateRoomError" );
			call.put( "message", "You cannot create more than one room." );
			client.send( call );
			return;
		}

		Room room = new Room( json, this, user );

		roomList.put( json.get( "name" ).getAsString(), room );

		//# Let the user know they have successfully created a room
		RemoteCall call = new RemoteCall( "onCreateRoom" );
		call.put( "room", RemoteCall.serialize( room ) );
		client.send( call );

		//# Tell all SmartLobby clients thht a new room has been created
		call = new RemoteCall( "onRoomAdd" );
		call.put( "room", RemoteCall.serialize( room ) );
		sendToList( userList, call, false );

		//# Tell the user that they have joined a room
		room.onUserJoin( getUserByTCPClient( client ) );
	}

	//# Would like to have joinRoom inside the Room object, but think it's easier to maintain out here
	public void joinRoom( TCPClient client, JsonObject json ) {
		Room room = getRoomByName( json.get( "roomName" ).getAsString() );

		//# Going to do some pre-join error checking, starting with making sure this room exists.
		if ( room != null ) {
			//# Make sure the room is accepting new joiners (perhaps game already in session, or something)
			//# Also, we need to ensure that users that have been kicked cannot re-join
			if ( !room.isAcceptingNewJoiners || room.userList.containsValue( getUserByTCPClient( client ) ) ) {
				RemoteCall call = new RemoteCall( "onJoinRoomError" );
				call.put( "message", "Room is locked-out." );
				client.send( call );
				return;
			}

			//# Check if room is at capacity
			if ( room.getCurrentUsers() >= room.maxUsers ) {
				RemoteCall call = new RemoteCall( "onJoinRoomError" );
				call.put( "message", "Room is full." );
				client.send( call );
				return;
			}

			//# Check if private and needs password
			if ( room.isPrivate ) {
				System.out.println( "Room is private." );
				if ( !json.get( "password" ).getAsString().equals( room.getPassword() ) ) {
					RemoteCall call = new RemoteCall( "onJoinRoomError" );
					call.put( "message", "Invalid password." );
					client.send( call );
					return;
				}
			}
		} else {
			RemoteCall call = new RemoteCall( "onJoinRoomError" );
			call.put( "message", "Room does not exist." );
			client.send( call );
			return;
		}

		//# The light at the end of the tunnel.
		room.onUserJoin( getUserByTCPClient( client ) );
	}

	public void sendRoomMessage( TCPClient client, JsonObject json ) {
		User user = getUserByTCPClient( client );
		RemoteCall call = new RemoteCall( "onMessageRoom" );
		call.put( "sender", RemoteCall.serialize( user ) );
		call.put( "message", json.get( "message" ).getAsString() );

		sendToList( user.getRoom().userList, call, true );
	}

	public void sendPrivateMessage( TCPClient client, JsonObject json ) {
		User user = getUserByTCPClient( client );
		User target = getUserByUsername( json.get( "target" ).getAsString() );

		if ( target != null ) {
			RemoteCall call = new RemoteCall( "onMessagePrivate" );
			call.put( "sender", user.getUsername() );
			call.put( "message", json.get( "message" ).getAsString() );

			client.send( call );
			target.getTcpClient().send( call );
		} else {
		}
	}

	public void leaveRoom( TCPClient client, JsonObject json ) {
		User user = getUserByTCPClient( client );
		user.getRoom().onUserLeave( user );
	}

	//# Helper methods..
	public Room getRoomByName( String name ) {
		return roomList.get( name );
	}

	public User getUserByTCPClient( TCPClient client ) {
		return userList.get( client.getUniqueId().toString() );
	}

	public User getUserByUsername( String username ) {
		return userList.get( username );
	}

	public void sendToList( Map<String, User> list, RemoteCall call, boolean isForceBroadcast ) {
		for ( Map.Entry<String, User> entryUser : list.entrySet() ) {
			User user = entryUser.getValue();
			//# Only users in rooms accepting remote call packets will receive this event
			//# unless specifically overridden (by a room event inside a locked out room, for instance)
			if ( user.getRoom().isAcceptingBroadcastMessages || isForceBroadcast ) {
				user.getTcpClient().send( call );
			} else {
				System.out.println( "It shows: " + user.getRoom().isAcceptingBroadcastMessages );
			}
		}
	}
}
