/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby Room class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby {
	import net.smartsocket.SmartSocketClient;
	import net.smartsocket.protocols.json.RemoteCall;
	
	/**
	 * An object representation of a room object sent from the server 
	 * @author XaeroDegreaz
	 * 
	 */	
	public class Room {
		/**
		 * An Array holding User objects for people that are in that room. Probably will be deleted in next version 
		 */		
		public var userList:Array = new Array();
		/**
		 * Name of the room 
		 */		
		public var name:String = "";
		/**
		 * This field is <b>only used</b> during the construction of the Room object
		 * before sending it to the server for creation -- no Room object will
		 * <b>ever</b> hold a password value when being sent <b>from</b> the server.
		 */		
		public var password:String = "";
		/**
		 * The maximum amount of users to be allowed into this room. 
		 */		
		public var maxUsers:int = 10;
		/**
		 * The amount of user currently in this room 
		 */		
		public var currentUsers:int;
		/**
		 * The unique identifier for this room 
		 */		
		public var roomID:int;
		/**
		 * True if this room is password protected on the server 
		 */		
		public var isPrivate:Boolean;
		/**
		 * This flag basically determines whether or not the room is in "locked" status. Locked room cannot be joined, even with a password. 
		 */		
		public var isAcceptingNewJoiners:Boolean;
		/**
		 * The User object of the person who created the room. 
		 */		
		public var owner:User;
		/**
		 * The custom data object is a JSON object with custom variables for developers to tuck stuff into; game flags, etc. 
		 */		
		public var customData:Object = {};
		
		/**
		 * An object to be de-serialized and used to construct the properties of this Room method.
		 * This is not a dynamic class, so unused object properties are just ignored. 
		 * @param object
		 * 
		 */	
		public function Room(object:Object = null) {
			for (var i in object) {
				try {
					if(i == "owner") {
						owner = new User(object[i]);
					}else {
						this[i] = object[i];
					}
				}catch(e:Error) {
					trace("Room does not have the property ["+i+"]");
				}
			}
		}
		
		/**
		 * This will get a room list specifically for this Room object. This returned userlist will not trigger the onUserList listener 
		 * (which is used soley to list all users in your current room. However, it will trigger onExplicitUserList.
		 * 
		 */		
		public function getUserList():void {
			var call:RemoteCall = new RemoteCall("getUserList");
			call.slDataListener = "room";
			call.roomName = name;
			
			SmartSocketClient.send( call );
		}
		
	}
}