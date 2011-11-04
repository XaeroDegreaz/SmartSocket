package net.smartsocket.smartlobby.events;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author XaeroDegreaz
 */
public enum SmartLobbyEvent {

	/**
	 * This is sent to a user when they have created a room. SmartLobbyEvent.data is a Room object.
	 */
	onCreateRoom,
	/**
	 * This is sent to a user when they tried to create a room, but an error was generated. SmartLobbyEvent.data is a RemoteCall object, with the property message<b>:String</b>
	 */
	onCreateRoomError,
	/**
	 * Contains login information about the user. SmartLobbyEvent.data is a RemoteCall object, with the property username<b>:String</b>
	 */
	onLogin,
	/**
	 * This is thrown when a login error occured. SmartLobbyEvent.data is a RemoteCall object, with the property error<b>:String</b>
	 */
	onLoginError,
	/**
	 * This is launched when the a provate message has been sent. It gets sent to both the sender, and the target.
	 * SmartLobbyEvent.data is a RemoteCall object, with the properties message<b>:String</b>, and target<b>:String</b>
	 */
	onMessagePrivate,
	/**
	 * This event is called when a chat message has been sent to this room.
	 * SmartLobbyEvent.data is a RemoteCall object, with the properties message<b>:String</b> and sender<b>:Object</b>. The sender property
	 * can be constructed as a User object: var user:User = new User(SmartLobbyEvent.data.sender)
	 */
	onMessageRoom,
	/**
	 * This is dispatched when a new room has been created. SmartLobbyEvent.data is a Room object.
	 */
	onRoomAdd,
	/**
	 * This is dispatched when a room user count has changed. SmartLobbyEvent.data is a Room object.
	 */
	onRoomCountUpdate,
	/**
	 * This is dispatched when a room had been removed from the server. SmartLobbyEvent.data is a Room object.
	 */
	onRoomDelete,
	/**
	 * This is dispatched when THIS user has joined a room. SmartLobbyEvent.data is a Room object.
	 */
	onRoomJoin,
	/**
	 * This is dispatched when THIS user has failed to join a room. SmartLobbyEvent.data is a RemoteCall object.
	 */
	onRoomJoinError,
	/**
	 * This is dispatched when THIS user has left a room. SmartLobbyEvent.data is a Room object.
	 */
	onRoomLeave,
	/**
	 * This is dispatched when a room has locked the room from being joined. SmartLobbyEvent.data is a Room object.
	 */
	onRoomLockToggled,
	/**
	 * This is dispatched when a room list from the server is received. SmartLobbyEvent.data is an Array of Room objects
	 */
	onRoomList,
	/**
	 * This is dispatched when a user has joined THIS room. SmartLobbyEvent.data is a User object.
	 */
	onUserJoin,
	/**
	 * This is dispatched when a user is kicked from the room. SmartLobbyEvent.data is an Object, with the properties user<b>:User</b>, and reason<b>:String</b>
	 */
	onUserKicked,
	/**
	 * This is dispatched when a user leaves THIS room. SmartLobbyEvent.data is a User object.
	 */
	onUserLeave,
	/**
	 * This is dispatched when a user list is retrieved from the server. SmartLobbyEvent.data is a Vector.<User>.
	 */
	onUserList
}