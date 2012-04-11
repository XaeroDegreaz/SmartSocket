package net.smartsocket.smartlobby;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.smartsocket.Logger;
import net.smartsocket.protocols.json.RemoteCall;
import net.smartsocket.smartlobby.events.SmartLobbyEvent;

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
	/**
	 * A map of User objects. These are users in this room.
	 */
	public transient Map<String, User> userList = Collections.synchronizedMap( new HashMap<String, User>() );
	/**
	 * A map of User objects that have been kicked from the room.
	 */
	public transient Map<String, User> kickList = Collections.synchronizedMap( new HashMap<String, User>() );
	private static transient Room defaultRoom = null;
	private static transient long currentRoomId = 0;
	/**
	 * Unique room ID
	 */
	public long roomID = 0;
	/**
	 * Maximum number of joiners on the room
	 */
	public int maxUsers = 16;
	/**
	 * The current number of users in this room
	 */
	private int currentUsers = 0;
	//# Defining this allows developers to run multiple unique SmartLobby instances.
	//# Without this, we would need to make userList and roomList static fields in SmartLobby's class.
	private transient SmartLobby slInstance;
	/**
	 * Get the User object of the current user in the room.
	 */
	private User owner;
	/**
	 * Determine whether or not this room is user created
	 */
	private boolean userCreated = false;
	/**
	 * The string name of this room
	 */
	private String name;
	/**
	 * The password of this room
	 */
	private transient String password = "";
	/**
	 * Determine if the room is a private room (password protected)
	 */
	public boolean isPrivate = false;
	/**
	 * Determines whether or not this room accepts broadcast messages
	 * such as room count update, and new room messages.
	 */
	public boolean isAcceptingBroadcastMessages = true;
	/**
	 * Determines if this room has been manually locked, preventing new joiners.
	 */
	public boolean isAcceptingNewJoiners = true;
	/**
	 * Determines whether or not this room is a default room that will be joined whn a user leaves
	 * their current room without joining another.
	 */
	public boolean isDefault = false;
	private transient Gson gson = new Gson();
	/**
	 * Custom data that developers can put into the room and can be accessed later.
	 * This can be used to hold things such as game flags, etc.
	 */
	public JsonObject customData;

	/**
	 * Construct a new Room object
	 * @param room JsonObject filled with Room properties.
	 * @param slInstance The SmartLobby instance this Room belongs to.
	 * @param user The User object of the creator of the room.
	 */
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
			if ( (defaultRoom == null) && (room.get( "default" ).getAsBoolean() == true) ) {
				defaultRoom = this;

				//# Default rooms should not be password protected...
				this.password = "";
				this.isPrivate = false;
			} else {
				//# Default room already set
			}
		} catch (Exception e) {
		}

		setOwner( user );
		setRoomId();
		//# Process room object, etc...

	}

	//# Events
	/**
	 * Triggered when a User is joining this room
	 * @param user The User object of the joiner
	 */
	public void onUserJoin( User user ) {
		if ( this == user.getRoom() ) {
			return;
		}

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

		//# Event listeners
		slInstance.dispatchEvent( SmartLobbyEvent.onRoomJoin, this );
		slInstance.dispatchEvent( SmartLobbyEvent.onUserJoin, user );
		slInstance.dispatchEvent( SmartLobbyEvent.onRoomCountUpdate, this );
	}

	/**
	 * Triggered when a User is leaving this room
	 * @param user The User object of the leaving user.
	 */
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

			//# Event listener
			slInstance.dispatchEvent( SmartLobbyEvent.onRoomDelete, this );
			return;
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

		//# Event listeners
		slInstance.dispatchEvent( SmartLobbyEvent.onRoomLeave, this );
		slInstance.dispatchEvent( SmartLobbyEvent.onUserLeave, user );
		slInstance.dispatchEvent( SmartLobbyEvent.onRoomCountUpdate, this );

		//# We wait until this very point to remove the player from the list.. This way, they can receive their
		//# own departure event.
		userList.remove( user.getUsername() );
		user.setRoom( null );

		//# Check to see if we need to assign another room owner
		try {
			if ( getOwner() == user ) {
				owner = userList.entrySet().iterator().next().getValue();
				//# TODO Send message to room showing that ownership has changed            
			}
		} catch (Exception e) {
			//# Something went wrong if an exception gets thrown here!
			//# Likely lots of users left the room at the same time, but the counter should accomodate for this.
			//# Just protecting the server from a case like that where next().getValue() would throw null pointer
		}
	}

	/**
	 * Triggered when a Room owner is locking / unlocking a room, preventing / enabling
	 * joining of the Room.
	 * @param user The User object that is calling this method
	 * @param json The JsonObject passed along through. Most likely contains no keys.
	 */
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

		//# Event listeners.
		slInstance.dispatchEvent( SmartLobbyEvent.onRoomLockToggled, call );
	}

	/**
	 * Triggered when attempting to kick a User from the room
	 * @param user The User object initiating the kick
	 * @param json JsonObject containing the key "username" of the target
	 */
	public void kickUser( User user, JsonObject json ) {
		if ( getOwner() != user ) {
			//# Illegal kick command.. maybe log this somewhere.
			Logger.log( "Invalid kick attempt." );
			return;
		}
		//# Grab the user
		User target = slInstance.getUserByUsername( json.get( "username" ).getAsString() );

		//# Make sure they aren't kicking themselves -_-
		if ( user == target ) {
			//return;
		}

		//# Verify that the target is a member of THIS room
		if ( userList.containsValue( target ) ) {
			//# Add to kick list to prevent re-join
			kickList.put( target.getUsername(), target );

			//# Send the notification to all users in the room, including the target
			RemoteCall call = new RemoteCall( "onUserKicked" );
			call.put( "user", RemoteCall.serialize( target ) );
			call.put( "reason", json.get( "reason" ).getAsString() );
			slInstance.sendToList( userList, call, true );

			// Event listeners fire first so listening methods have access to room object while user still in here
			slInstance.dispatchEvent( SmartLobbyEvent.onUserKicked, call );

			//# Send them packing to the default room/lobby
			Room.getDefaultRoom().onUserJoin( target );
		}
	}

	/**
	 * Set the owner of this Room to a User object
	 * @param user The User to set the owner as.
	 */
	public final void setOwner( User user ) {
		owner = user;

		if ( user.getUserID() >= 0 ) {
			userCreated = true;
		}
	}

	/**
	 * Get the owner of this room.
	 * @return
	 */
	public User getOwner() {
		return this.owner;
	}

	/**
	 * Get a list of users for this room. Sends out a JsonObject back to client containing User objects
	 * @param user The User requesting the user list
	 * @param json JsonObject most likely empty, unless client requesting specific user list of specific room.
	 */
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

		//# Event listeners. No idea why someone would want a listener for this.. but here goes..
		slInstance.dispatchEvent( SmartLobbyEvent.onUserList, call );
	}

	/**
	 * @return the currentUsers
	 */
	public int getCurrentUsers() {
		return currentUsers;
	}

	/**
	 * @param size 
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
