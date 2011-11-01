/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby User class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby {
	/**
	 * The User object contains some easy to access information about a particular user retrieved from the server 
	 * @author XaeroDegreaz
	 * 
	 */	
	public class User {
		
		/**
		 * The name of the user 
		 */		
		public var username:String;
		/**
		 * The unique ID# of the user 
		 */		
		public var userID:int;
		/**
		 * The friends list will contain a list of other user objects who are friended by this user (to be implemented) 
		 */		
		public var friendsList;
		/**
		 * Quick reference to the Room object of the user. Currently only tracked on the SmartLobbyClient.me User object
		 */		
		public var room:Room;
		
		/**
		 * An object to be de-serialized and used to construct the properties of this User method.
		 * This is not a dynamic class, so unused object properties are just ignored. 
		 * @param object
		 * 
		 */		
		public function User(object:Object) {			
			for (var i in object) {
				try {
					this[i] = object[i];
				}catch(e:Error) {
					trace("User does not have the property ["+i+"]");
				}
			}			
		}
	}
}