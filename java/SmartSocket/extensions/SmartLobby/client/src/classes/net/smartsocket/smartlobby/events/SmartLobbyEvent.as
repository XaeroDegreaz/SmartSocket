/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby event handling class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.events
{
	import flash.events.Event;
	
	public class SmartLobbyEvent extends Event
	{
		public static const onCreateRoom:String = "onCreateRoom";
		public static const onMessagePrivate:String = "onMessagePrivate";
		public static const onMessageRoom:String = "onMessageRoom";
		public static const onRoomAdd:String = "onRoomAdd";
		public static const onRoomCountUpdate:String = "onRoomCountUpdate";
		public static const onRoomDelete:String = "onRoomDelete";
		public static const onRoomJoin:String = "onRoomJoin";
		public static const onRoomLeave:String = "onRoomLeave";
		public static const onRoomList:String = "onRoomList";
		public static const onTeamList:String = "onTeamList";
		public static const onTeamListChange:String = "onTeamListChange";
		public static const onTeamReadyStatusChange:String = "onTeamReadyStatusChange";
		public static const onUserJoin:String = "onUserJoin";
		public static const onUserLeave:String = "onUserLeave";
		public static const onUserList:String = "onUserList";
		
		public var data:*;
		
		public function SmartLobbyEvent(type:String, d:*)
		{
			this.data = d;
			super(type);
			trace("Event constructor: "+type);			
		}
		
		override public function clone():Event {
			trace("Clone worked");
			return new SmartLobbyEvent(type, this.data);
		}
	}
}