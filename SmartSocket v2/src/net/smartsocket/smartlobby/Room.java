package net.smartsocket.smartlobby;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.RemoteCall;

/**
 *
 * @author XaeroDegreaz
 */
public class Room {

	/**
	 * @return the defaultRoom
	 */
	public static Room getDefaultRoom() {
		return defaultRoom;
	}

	//# We mark this transient because we don't want users serialized when sending out room lists.
	//# If a user wants the list of users in the room, they must explicitly call getUserList from their client.
	public transient Map<String, User> userList = Collections.synchronizedMap( new HashMap<String, User>() );
	public transient Map<String, User> kickList = Collections.synchronizedMap( new HashMap<String, User>() );
	private static transient Room defaultRoom = null;
	private static transient long currentRoomId = 0;
	public long roomID = 0;
	public int maxUsers = 16;
	private int currentUsers = 0;
	//# Defining this allows developers to run multiple unique SmartLobby instances.
	//# Without this, we would need to make userList and roomList static fields in SmartLobby's class.
	private transient SmartLobby slInstance;
	private User owner;
	private boolean userCreated = false;
	private String name;
	private transient String password = "";
	public boolean isPrivate = false;
	public boolean isAcceptingBroadcastMessages = true;
	public boolean isAcceptingNewJoiners = true;
	public boolean isDefault = false;
	private transient Gson gson = new Gson();
	public JsonObject customData;

	public Room( JsonObject room, SmartLobby slInstance, User user ) {
		this.slInstance = slInstance;

		try {
			name = room.get( "name" ).getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			password = room.get( "password" ).getAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			maxUsers = room.get( "maxUsers" ).getAsInt();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			customData = room.get( "customData" ).getAsJsonObject();
		} catch (Exception e) {
		}
		
		try {
			isAcceptingBroadcastMessages = room.get( "isAcceptingBroadcastMessages" ).getAsBoolean();
		} catch (Exception e) {
		}
		
		if ( (password != null) && !password.equals( "" ) ) {
			this.isPrivate = true;
			this.password = room.get( "password" ).getAsString();
		}
		
		try {
			if( ( defaultRoom == null ) && ( room.get( "default").getAsBoolean() == true ) ) {
				defaultRoom = this;
				
				//# Default rooms should not be password protected...
				this.password = "";
				this.isPrivate = false;
			}else {
				//# Default room already set
			}
		} catch (Exception e) {
		}		

		setOwner( user );
		setRoomId();
		//# Process room object, etc...
	}

	//# Events
	public void onUserJoin( User user ) {		
		try {
			user.getRoom().onUserLeave( user );
		} catch (Exception e) {
			Logger.log( "User does not have a room to leave... " + e.getMessage() );
		}

		this.userList.put( user.getUsername(), user );
		user.setRoom( this );

		setCurrentUsers( userList.size() );

		//# Send a message to this user that they have joined a new room
		RemoteCall call = new RemoteCall( "onRoomJoin" );
		call.put( "room", RemoteCall.serialize( this ) );
		user.getTcpClient().send( call );

		//# Let users of this room know that a new user has joined
		call = new RemoteCall( "onUserJoin" );
		call.put( "user", RemoteCall.serialize( user ) );
		slInstance.sendToList( userList, call, true );

		//# Send to all users connected if their room accepts broadcast messages
		call = new RemoteCall( "onRoomCountUpdate" );
		call.put( "room", RemoteCall.serialize( this ) );
		slInstance.sendToList( slInstance.userList, call, false );
	}

	public void onUserLeave( User user ) {
		//# We set this here before all of the other calls.
		//# We have to fake this size here, even though we haven't removed the user yet
		setCurrentUsers( userList.size() - 1 );

		//# Check to see if we need to remove this room
		//# server generated rooms will not remove.
		if ( getCurrentUsers() <= 0 && userCreated ) {
			slInstance.roomList.remove( this.name );
			//# TODO Send roomlist update to rooms that need to receive this event			
			RemoteCall call = new RemoteCall( "onRoomDelete" );
			call.put( "room", RemoteCall.serialize( this ) );
			slInstance.sendToList( slInstance.userList, call, false );
			return;
		}

		//# Check to see if we need to assign another room owner
		if ( getOwner() == user ) {
			owner = null;
			User newOwner = userList.entrySet().iterator().next().getValue();
			//# TODO Send message to room showing that ownership has changed            
		}

		//# Send a message to this user that they have joined a new room
		RemoteCall call = new RemoteCall( "onRoomLeave" );
		call.put( "room", RemoteCall.serialize( this ) );
		user.getTcpClient().send( call );

		//# Send message to all users in this room
		call = new RemoteCall( "onUserLeave" );
		call.put( "user", RemoteCall.serialize( user ) );
		slInstance.sendToList( userList, call, true );

		//# Send to all users connected if their room accepts broadcast messages
		call = new RemoteCall( "onRoomCountUpdate" );
		call.put( "room", RemoteCall.serialize( this ) );
		slInstance.sendToList( slInstance.userList, call, false );

		//# We wait until this very point to remove the player from the list.. This way, they can receive their
		//# own departure event.
		userList.remove( user.getUsername() );
		user.setRoom( null );
	}

	public void toggleRoomLock( User user, JsonObject json ) {
		if ( getOwner() != user ) {
			//# Illegal lock command.. maybe log this somewhere.
			return;
		}

		isAcceptingNewJoiners = (isAcceptingNewJoiners) ? false : true;

		RemoteCall call = new RemoteCall( "onRoomLockToggled" );
		call.put( "roomID", this.roomID );
		call.put( "name", this.name );
		call.put( "isAcceptingNewJoiners", isAcceptingNewJoiners );

		slInstance.sendToList( slInstance.userList, call, false );
	}

	public void kickUser( User user, JsonObject json ) {
		if ( getOwner() != user ) {
			//# Illegal kick command.. maybe log this somewhere.
			Logger.log("Invalid kick attempt.");
			return;
		}
		//# Grab the user
		User target = slInstance.getUserByUsername( json.get( "username" ).getAsString() );
		
		//# Make sure they aren't kicking themselves -_-
		if( user == target ) {
			//return;
		}
		
		//# Verify that the target is a member of THIS room
		if ( userList.containsValue( target ) ) {
			//# Add to kick list to prevent re-join
			kickList.put( target.getUsername(), target );

			//# Send the notification to all users in the room, including the target
			RemoteCall call = new RemoteCall( "onUserKicked" );
			call.put( "user", RemoteCall.serialize( target ) );
			call.put( "reason", json.get( "reason").getAsString() );
			slInstance.sendToList( userList, call, true );			
			
			//# Send them packing to the default room/lobby
			Room.getDefaultRoom().onUserJoin( target );
		}
	}

	public final void setOwner( User user ) {
		owner = user;

		if ( user.getUserID() >= 0 ) {
			userCreated = true;
		}
	}

	public User getOwner() {
		return this.owner;
	}

	public void getUserList( User user, JsonObject json ) {
		//# This block allows us to determine whether or not a request is being made
		//# for a userlist on this room, or a specific room.
		if ( json.get( "roomName" ) != null ) {
			Room target = slInstance.getRoomByName( json.get( "roomName" ).getAsString() );

			//# Remove the room name to prevent infinite recursion...
			json.remove( "roomName" );

			target.getUserList( user, json );
		}

		//# We must recreate it in a temp variable since we marked it transient in the class.
		Map tmp = userList;
		RemoteCall call = new RemoteCall( "onUserList" );
		call.put( "userList", RemoteCall.serialize( tmp ) );
		call.put( "roomName", this.name );
		user.getTcpClient().send( call );
	}

	/**
	 * @return the currentUsers
	 */
	public int getCurrentUsers() {
		return currentUsers;
	}

	/**
	 * @param currentUsers the currentUsers to set
	 */
	public synchronized void setCurrentUsers( int size ) {
		this.currentUsers = size;
	}

	/**
	 * @return the currentRoomId
	 */
	public long getRoomId() {
		return currentRoomId;
	}

	/**
	 * @param currentRoomId the currentRoomId to set
	 */
	public final synchronized void setRoomId() {
		this.roomID = currentRoomId;
		currentRoomId++;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword( String password ) {
		this.password = password;
	}
}
