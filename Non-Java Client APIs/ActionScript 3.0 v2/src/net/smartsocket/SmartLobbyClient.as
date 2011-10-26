/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby client class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket {
	import flash.events.*;
	
	import net.smartsocket.protocols.json.RemoteCall;
	import net.smartsocket.smartlobby.Room;
	import net.smartsocket.smartlobby.User;
	import net.smartsocket.smartlobby.events.*;
	
	public class SmartLobbyClient extends SmartSocketClient{
		
		public static var roomList:Array = new Array();
		public static var me:User;
		
		public function SmartLobbyClient() {
			super();
		}		
		
		/*
		 * These methods are only accessible to SmartSocketClient. They are called directly
		 * by the API, perform primary processing of the data received, then dispatch
		 * the event to external event listeners
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
			var room:Room = new Room(call);
			roomList[room.name].isAcceptingNewJoiners = room.isAcceptingNewJoiners;
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
		
		/**
		 * These are the exposed methods of SmartLobbyCLient. These can be called from any
		 * object with a proper reference to a SmartLobbyClient instance.
		 * ##############################################################
		 */
		public function login(username:String, password:String = ""):void {
			var call:RemoteCall = new RemoteCall("login");
			call.username = username;
			call.password = password;
			
			send(call);
		}		
		
		public function joinRoom(roomId:Number = 0, roomName:String = "", password:String = ""):void {
			var call:RemoteCall = new RemoteCall("joinRoom");
			call.roomId = roomId;
			call.roomName = roomName;
			call.password = password;
			
			send(call);
		}
		
		public function kickUser(username:String, reason:String = ""):void {
			var call:RemoteCall = new RemoteCall("kickUser");
			call.slDataListener = "room";
			call.username = "username";
			call.reason = reason;
		}
		
		/**
		 * Retrieves a JSON object populated with User objects in the user's current room.
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
		
		public function getRoomList():void {
			send( new RemoteCall("getRoomList") );
		}
		
		public function createRoom(roomName:String, password:String = "", maxUsers:int = 10, customData:Object = null):void {
			var call:RemoteCall = new RemoteCall("createRoom");
			call.name = roomName;
			call.password = password;
			call.maxUsers = maxUsers;
			call.customData = customData;
			
			send(call);
		}
		
		public function leaveRoom():void {
			send( new RemoteCall("leaveRoom") );
		}
		
		public function toggleRoomLock() {
			var call:RemoteCall = new RemoteCall("toggleRoomLock");
			call.slDataListener = "room";
			send(call);
		}
		
		public function sendRoomMessage(message:String):void {
			var call:RemoteCall = new RemoteCall("sendRoomMessage");
			call.message = message;
			
			send(call);
		}
		
		public function sendPrivateMessage(target:String, message:String):void {
			var call:RemoteCall = new RemoteCall("sendPrivateMessage");
			call.target = target;
			call.message = message;
			
			send(call);
		}		
	}
}