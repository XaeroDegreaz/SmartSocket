/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby SmartComponents RoomList class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.smartcomponents {
	import fl.controls.DataGrid;
	import fl.data.DataProvider;
	import fl.events.ListEvent;
	
	import flash.events.MouseEvent;
	import flash.net.drm.VoucherAccessInfo;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.*;
	
	/**
	 * This data grid manages events specific to room displaying. It can be instantiated
	 * with a reference to a SmartLobbyClient instance, or one can be assigned to it manually.
	 * Either way, it is critical for this componenet to have a reference to the object, or
	 * none of the events will be dispatched here.<br/><br/>
	 * 
	 * Listens for events: SmartLobbyEvent.onLogin, SmartLobbyEvent.onRoomList, SmartLobbyEvent.onRoomAdd, SmartLobbyEvent.onRoomDelete, 
	 * SmartLobbyEvent.onRoomCountUpdate, SmartLobbyEvent.onRoomLockToggled
	 * 
	 * @author XaeroDegreaz
	 * 
	 */	
	public class RoomList extends DataGrid {
		
		protected var _smartLobbyInstance:SmartLobbyClient;
		
		/**
		 * Instantiate a RoomList
		 * @param smartLobbyInstance (optional) Reference to an existing SmartLobbyClient object.
		 * 
		 */		
		public function RoomList(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			this.enabled = false;
			columns = ["ID", "Name", "Current", "Max", "Creator", "Private"];
			
			if(smartLobbyInstance) {
				this.smartLobbyInstance = smartLobbyInstance;
			}
		}
		
		/**
		 * Return the instance of the SmartLobbyClient which is bound to this RoomList 
		 * @return The SmartLobbyCLient
		 * 
		 */		
		public function get smartLobbyInstance():SmartLobbyClient {
			return _smartLobbyInstance;
		}
		
		/**
		 * Set the instance of the SmartLobbyClient which is bound to this RoomList.
		 * This also initializes all of the SmartLobbyEvent listeners
		 * 
		 */	
		public function set smartLobbyInstance(value:SmartLobbyClient):void	{
			_smartLobbyInstance = value;
			
			//# These events listeners will only used for displaying the content of the rooms.
			//# If you need some other added functionality for these events, feel free to use
			//# additional event handlers for the items below.
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLogin, onLogin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomList, onRoomList);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomAdd, onRoomAdd);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomDelete, onRoomDelete);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomCountUpdate, onRoomCountUpdate);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomLockToggled, onRoomLockToggled);
			
			this.addEventListener(MouseEvent.DOUBLE_CLICK, onMouseDoubleClick);
		}
		
		/**
		 * Dispatched when user logs in. Basically just using this to enable this component. 
		 * @param e SmartLobbyEvent.onLogin
		 * 
		 */
		protected function onLogin(e:SmartLobbyEvent):void {
			this.enabled = true;
		}
		
		/**
		 * Called when a room row is double clicked. Makes an API call to join that room. 
		 * @param e MouseEvent.DOUBLE_CLICK
		 * 
		 */		
		protected function onMouseDoubleClick(e:MouseEvent):void {
			_smartLobbyInstance.joinRoom(this.selectedItem["ID"], this.selectedItem["Name"]);
		}
		
		/**
		 * Called when receiving a room list. Loops through Array and adds each room to the list. 
		 * @param e SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function onRoomList(e:SmartLobbyEvent):void {
			dataProvider.removeAll();
			
			for(var i in e.data) {
				var room:Room = e.data[i];
				addRoom(room);
			}
			
			this.sortItemsOn("ID");
		}
		
		/**
		 * Called when a new room is added. Adds the room to the list. 
		 * @param e SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function onRoomAdd(e:SmartLobbyEvent):void {
			var room:Room = e.data;			
			addRoom(room);
		}
		
		/**
		 * This function is the helper function that just actually does the adding for the room, and matches column names. 
		 * @param room SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function addRoom(room:Room):void {
			this.dataProvider.addItem({
				"ID": room.roomID,
				"Name": room.name,
				"Max": room.maxUsers,
				"Creator": room.owner.username,
				"Current": room.currentUsers,
				"Private": room.isPrivate
			});
		}
		
		/**
		 * Called when a room has been deleted from server. Removes it from this list. 
		 * @param e SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function onRoomDelete(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			for(var i:int; i < dataProvider.length; i++) {
				var object:Object = dataProvider.getItemAt(i);
				
				if(object["ID"] == room.roomID) {
					dataProvider.removeItem(object);
					break;
				}
			}
		}
		
		/**
		 * Called when a room lock has been toggled. If locked, remove from list. If unlocked, add it to the list. 
		 * @param e SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function onRoomLockToggled(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			if(room.isAcceptingNewJoiners) {
				addRoom(room);
			}else {
				for(var i:int; i < dataProvider.length; i++) {
					var object:Object = dataProvider.getItemAt(i);					
					if(object["ID"] == room.roomID) {
						dataProvider.removeItem(object);
						break;
					}
				}
			}
		}
		
		/**
		 * Called when a user joins, or leaves any room (so long as the room you are in is accepting broadcast messages).
		 * Loops through the rooms in the list, and matches them to the room id, then updates and invalidates the row. 
		 * @param e SmartLobbyEvent.onRoomCountUpdate
		 * 
		 */		
		protected function onRoomCountUpdate(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			for(var i:int; i < dataProvider.length; i++) {
				var object:Object = dataProvider.getItemAt(i);
				
				if(object["ID"] == room.roomID) {
					object["Current"] = room.currentUsers;
					dataProvider.invalidateItem(object);
					break;
				}
			}
		}
	}
}