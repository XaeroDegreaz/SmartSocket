/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby event handling class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.events {
	import flash.events.Event;
	
	/**
	 * Events that are specific to SmartLobbyClient 
	 * @author XaeroDegreaz
	 * 
	 */	
	public class SmartLobbyEvent extends Event {
		/**
		 * This is sent to a user when they have created a room. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onCreateRoom:String = "onCreateRoom";
		/**
		 * This is sent to a user when they tried to create a room, but an error was generated. SmartLobbyEvent.data is a RemoteCall object, with the property message<b>:String</b>
		 */		
		public static const onCreateRoomError:String = "onCreateRoomError";
		/**
		 * Contains login information about the user. SmartLobbyEvent.data is a RemoteCall object, with the property username<b>:String</b>
		 */		
		public static const onLogin:String = "onLogin";
		/**
		 * This is thrown when a login error occured. SmartLobbyEvent.data is a RemoteCall object, with the property error<b>:String</b>
		 */		
		public static const onLoginError:String = "onLoginError";
		/**
		 * This is launched when the a provate message has been sent. It gets sent to both the sender, and the target.
		 * SmartLobbyEvent.data is a RemoteCall object, with the properties message<b>:String</b>, and target<b>:String</b>
		 */		
		public static const onMessagePrivate:String = "onMessagePrivate";
		/**
		 * This event is called when a chat message has been sent to this room.
		 * SmartLobbyEvent.data is a RemoteCall object, with the properties message<b>:String</b> and sender<b>:Object</b>. The sender property
		 * can be constructed as a User object: var user:User = new User(SmartLobbyEvent.data.sender)
		 */		
		public static const onMessageRoom:String = "onMessageRoom";
		/**
		 * This is dispatched when a new room has been created. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomAdd:String = "onRoomAdd";
		/**
		 * This is dispatched when a room user count has changed. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomCountUpdate:String = "onRoomCountUpdate";
		/**
		 * This is dispatched when a room had been removed from the server. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomDelete:String = "onRoomDelete";
		/**
		 * This is dispatched when THIS user has joined a room. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomJoin:String = "onRoomJoin";
		/**
		 * This is dispatched when THIS user has left a room. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomLeave:String = "onRoomLeave";
		/**
		 * This is dispatched when a room has locked the room from being joined. SmartLobbyEvent.data is a Room object.
		 */		
		public static const onRoomLockToggled:String = "onRoomLockToggled";
		/**
		 * This is dispatched when a room list from the server is received. SmartLobbyEvent.data is an Array of Room objects
		 */		
		public static const onRoomList:String = "onRoomList";
		/**
		 * This is dispatched when a user has joined THIS room. SmartLobbyEvent.data is a User object.
		 */		
		public static const onUserJoin:String = "onUserJoin";
		/**
		 * This is dispatched when a user is kicked from the room. SmartLobbyEvent.data is an Object, with the properties user<b>:User</b>, and reason<b>:String</b>
		 */		
		public static const onUserKicked:String = "onUserKicked";
		/**
		 * This is dispatched when a user leaves THIS room. SmartLobbyEvent.data is a User object.
		 */		
		public static const onUserLeave:String = "onUserLeave";
		/**
		 * This is dispatched when a user list is retrieved from the server. SmartLobbyEvent.data is a Vector.<User>.
		 */		
		public static const onUserList:String = "onUserList";		
		/**
		 * This could either be a RemoteCall, Room, or User object. Please refer to the individual SmartLobbyEvent for information. 
		 */		
		public var data:*;
		
		public function SmartLobbyEvent(type:String, d:*) {
			this.data = d;
			super(type);		
		}
		
		override public function clone():Event {
			trace("Clone worked");
			return new SmartLobbyEvent(type, this.data);
		}
	}
}