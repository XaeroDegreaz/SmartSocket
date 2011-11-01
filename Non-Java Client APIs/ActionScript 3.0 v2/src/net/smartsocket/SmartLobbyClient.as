/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby client class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket {
	import flash.events.*;
	
	import net.smartsocket.protocols.json.RemoteCall;
	import net.smartsocket.smartlobby.Room;
	import net.smartsocket.smartlobby.User;
	import net.smartsocket.smartlobby.events.*;
	
	/**
	 * This class extends the base functionality of SmartSocketClient for use in systems such as a chat lobby, room list,
	 * user list, game launching, etc. You can ether sub-class this class, or instantiate it elsewhere, depending on your
	 * design needs. You can put this thing in motion really quickly by adding SmartComponents to your stage, which
	 * already have most of any logic you could really want in a chat lobby / room list. Those components can also
	 * be sub-classed in order to provide more in-depth handling of SmartLobbyEvents.
	 * @author XaeroDegreaz
	 * 
	 */	
	public class SmartLobbyClient extends SmartSocketClient{
		/**
		 * This holds an Array of all Room objects. SmartLobbyClient.roomList["room name"] = Room(roomObject) 
		 */		
		public static var roomList:Array = new Array();
		/**
		 * A simple static property that allows easy access to the User object of the current user. 
		 */		
		public static var me:User;
		
		public function SmartLobbyClient() {
			super();
		}		
		
		/*
		 * These methods are only accessible to SmartSocketClient. They are called directly
		 * by the API, perform primary processing of the data received, then dispatch
		 * the event to external event listeners (IE, the ones in your sub classes, or other listening objects)
		 */
		internal function onCreateRoom(call:RemoteCall):void {
			var room:Room = new Room(call.room);			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onLogin(call:RemoteCall):void {
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], call ) );			
		}
		
		internal function onLoginError(call:RemoteCall):void {
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], call ) );			
		}
		
		internal function onMessagePrivate(call:RemoteCall):void {
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], call ) );
		}
		
		internal function onMessageRoom(call:RemoteCall):void {
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], call ) );
		}
		
		internal function onRoomAdd(call:RemoteCall):void {
			var room:Room = new Room(call.room);
			roomList[room.name] = room;
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomLockToggled(call:RemoteCall):void {
			var room:Room = roomList[call.name];
			room.isAcceptingNewJoiners = call.isAcceptingNewJoiners;
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomCountUpdate(call:RemoteCall):void {
			var room:Room = roomList[call.room.name];
			room.currentUsers = call.room.currentUsers;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomDelete(call:RemoteCall):void {
			var room:Room = roomList[call.room.name];
			roomList[room.name] = null;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomJoin(call:RemoteCall):void {
			var room:Room = new Room(call.room);
			roomList[room.name] = room;
			me.room = room;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomLeave(call:RemoteCall):void {
			var room:Room = roomList[call.name];
			me.room = null;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], room ) );
		}
		
		internal function onRoomList(call:RemoteCall):void {
			//# Using keyed arrays here so we can always reference a Room object by room name.
			roomList = new Array();
			
			for(var i in call.roomList) {
				var room:Room = new Room(call.roomList[i]);
				roomList[room.name] = room;
			}
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], roomList as Array ) );
		}
		
		internal function onUserJoin(call:RemoteCall):void {
			var user:User = new User(call.user);
			//# Do I really want to store this information? Perhaps developers should
			//# Come up with their own way of storing user lists.
			//# TODO Revisit.
			//me.room.userList[user.username] = user;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], user ) );
		}
		
		internal function onUserKicked(call:RemoteCall):void {
			var user:User = new User(call.user);
			var reason:String = call.reason;
			var object:Object = {"user": user, "reason": reason};
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], object ) );
		}
		
		internal function onUserLeave(call:RemoteCall):void {
			var user:User = new User(call.user);
			//# Do I really want to store this information? Perhaps developers should
			//# Come up with their own way of storing user lists.
			//# TODO Revisit.
			//me.room.userList[user.username] = null;
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], user ) );
		}
		
		internal function onUserList(call:RemoteCall):void {
			var userList:Object = call.userList;
			//# Using a Vector here, because it's faster (performance increase with a couple hundred users in a room?)
			var vector:Vector.<User> = new Vector.<User>;
			
			for(var i in userList) {
				var user:User = new User(userList[i]);
				vector.push(user);
				
				if(me.room.name == call.roomName) {
					me.room.userList[user.username] = user;
				}
			}			
			
			dispatchEvent( new SmartLobbyEvent( SmartLobbyEvent[call.method], vector ) );
		}
		
		/*
		 * These are the exposed methods of SmartLobbyCLient. These can be called from any
		 * object with a proper reference to a SmartLobbyClient instance.
		 * ##############################################################
		 */
		
		/**
		 * Send a login call to the server. 
		 * @param username The username
		 * @param password (optional) password
		 * 
		 */		
		public function login(username:String, password:String = ""):void {
			var call:RemoteCall = new RemoteCall("login");
			call.username = username;
			call.password = password;
			
			send(call);
		}		
		
		/**
		 * Send a joinRoom command to the serer 
		 * @param roomId (optional) ID Number of the room (not fully implemented)
		 * @param roomName The name of the target room
		 * @param password The password, if any for this room.
		 * 
		 */		
		public function joinRoom(roomId:Number = 0, roomName:String = "", password:String = ""):void {
			var call:RemoteCall = new RemoteCall("joinRoom");
			call.roomId = roomId;
			call.roomName = roomName;
			call.password = password;
			
			send(call);
		}
		
		/**
		 * Send a kick command to the server, kicking and banning a user from the room
		 * for as long as this room is active.<br/><br/>
		 * 
		 * Triggers the SmartLobbyEvent.onUserKick event.
		 * 
		 * @param username The target username to kick (case sensitive!)
		 * @param reason (optional) Reason, if any (not implemented)
		 * 
		 */		
		public function kickUser(username:String, reason:String = ""):void {
			var call:RemoteCall = new RemoteCall("kickUser");
			call.slDataListener = "room";
			call.username = username;
			call.reason = reason;
			send(call);
		}
		
		/**
		 * Retrieves a JSON object populated with User objects in the user's <b>current</b> room.
		 * To retrieve a specific room's user list, call the getUserList method on that room's
		 * Room object.<br/><br/>
		 * 
		 * Triggers the SmartLobbyEvent.onUserList event.
		 * 
		 */		
		public function getUserList():void {
			var call:RemoteCall = new RemoteCall("getUserList");
			call.slDataListener = "room";
			
			send( call );
		}		
		
		/**
		 * Retrieves a JSON object populated with Room objects that are active on the server<br/><br/>
		 * 
		 * Triggers the SmartLobbyEvent.onRoomList event.
		 * 
		 */	
		public function getRoomList():void {
			send( new RemoteCall("getRoomList") );
		}
		
		/**
		 * Send a createRoom call to the server, which will create a room, and send off alerts to everyone
		 * in a room that accepts broadcast messages.
		 *  
		 * @param roomName Desired room name
		 * @param password (optional) Password
		 * @param maxUsers (optional) Maximum users allowed (default 10)
		 * @param customData (optional) Custom data that can be stored on the room object. The data should be a valid JSON object.
		 * 
		 */		
		public function createRoom(roomName:String, password:String = "", maxUsers:int = 10, customData:Object = null):void {
			var call:RemoteCall = new RemoteCall("createRoom");
			call.name = roomName;
			call.password = password;
			call.maxUsers = maxUsers;
			call.customData = customData;
			
			send(call);
		}
		
		/**
		 * Send a leaveRoom command to the server. The user will then leave their current room. 
		 * 
		 */		
		public function leaveRoom():void {
			send( new RemoteCall("leaveRoom") );
		}
		
		/**
		 * Toggle the locking mechanism on the room (this is different than a password).
		 * A locked room can no longer be joined by any users, and if using the SmartComponent RoomList
		 * it will be removed from the DataGrid (if locked). It will be added again when unlocked.
		 * The primary purpose of this method, is to allow the room to be locked, say after a game launches.
		 * 
		 */		
		public function toggleRoomLock():void {
			var call:RemoteCall = new RemoteCall("toggleRoomLock");
			call.slDataListener = "room";
			send(call);
		}
		
		/**
		 * Broadcase a message to everyone in the room 
		 * @param message The message
		 * 
		 */		
		public function sendRoomMessage(message:String):void {		
			var call:RemoteCall = new RemoteCall("sendRoomMessage");
			call.message = message;
			
			send(call);
		}
		
		/**
		 * Send a message to a specific user 
		 * @param target The target to send to
		 * @param message The message
		 * 
		 */		
		public function sendPrivateMessage(target:String, message:String):void {
			var call:RemoteCall = new RemoteCall("sendPrivateMessage");
			call.target = target;
			call.message = message;
			
			send(call);
		}		
	}
}