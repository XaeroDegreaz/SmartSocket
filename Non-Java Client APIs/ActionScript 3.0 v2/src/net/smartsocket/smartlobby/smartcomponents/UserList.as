/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby SmartComponents UserList class.

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
	import fl.events.DataGridEvent;
	import fl.events.ListEvent;
	
	import flash.events.*;
	import flash.ui.ContextMenu;
	import flash.ui.ContextMenuItem;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.SmartLobbyEvent;
	
	/**
	 * This class component listens for events on the server regarding user joining, and leaving. It shows a list
	 * of users in the current room. The visible field is Username. Hidden fields are userId<b>:int</b> and userObject<b>:User</b><br/><br/>
	 * 
	 * Listens for events: SmartLobbyEvent.onLogin, SmartLobbyEvent.onRoomJoin, SmartLobbyEvent.onUserJoin, SmartLobbyEvent.onUserLeave, SmartLobbyEvent.onUserList
	 * @author XaeroDegreaz
	 * 
	 */	
	public class UserList extends DataGrid {
		
		protected var _smartLobbyInstance:SmartLobbyClient;
		
		//# We use this as a way to keep track over hovered users for the context menu items.
		private var lastUserFocused:User;
		
		/**
		 * New UserList instance. 
		 * @param smartLobbyInstance (optional) Reference to an existing SmartLobbyClient object
		 * 
		 */		
		public function UserList(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			this.enabled = false;
			columns = ["Username"];
			
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
			var context:ContextMenu = new ContextMenu();
			var itemKick:ContextMenuItem = new ContextMenuItem("");
			var itemPM:ContextMenuItem = new ContextMenuItem("");
			
			//# These events listeners will only used for displaying the content of the rooms.
			//# If you need some other added functionality for these events, feel free to use
			//# additional event handlers for the items below.
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLogin, onLogin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomJoin, onRoomJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserJoin, onUserJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserLeave, onUserLeave);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserList, onUserList);			
			this.addEventListener(ListEvent.ITEM_ROLL_OVER, onItemRollOver);
			this.addEventListener(ListEvent.ITEM_ROLL_OUT, onItemRollOut);			
			itemKick.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, onKickItemSelect);
			itemPM.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, onPMItemSelect);			
			
			context.hideBuiltInItems();			
			context.customItems.push( itemKick, itemPM );			
			this.contextMenu = context;
		}
		
		/**
		 * Called when an item in the list is rolled over. Using this to track the last User object for things like PM and kicking. 
		 * @param e ListEvent.ITEM_ROLL_OVER
		 * 
		 */		
		protected function onItemRollOver(e:ListEvent):void {
			lastUserFocused = User(e.item["userObject"]);
			this.contextMenu.customItems[0].caption = "Kick "+e.item["Username"];
			this.contextMenu.customItems[1].caption = "PM "+e.item["Username"];
		}
		
		/**
		 * Called when an item in the list is rolled out. Using this to clear context menu items so that not menu shows up when not on user. 
		 * @param e ListEvent.ITEM_ROLL_OUT
		 * 
		 */	
		protected function onItemRollOut(e:ListEvent):void {
			this.contextMenu.customItems[0].caption = "";
			this.contextMenu.customItems[1].caption = "";
		}
		
		/**
		 * Called when the kick item is selected from the context menu. Simply makes the API call to kick the user.
		 * @param e ContextMenuEvent.MENU_ITEM_SELECT
		 * 
		 */		
		protected function onKickItemSelect(e:ContextMenuEvent):void {
			_smartLobbyInstance.kickUser(lastUserFocused.username);
		}
		
		/**
		 * Called when clicking the PM option on the context menu. Basically adds some text to the TextField that is formated for a PM
		 * (if a TextField is defined by ChatBox.setInputTextField(textField)). If no TextField is set, then you'll need to implement your own
		 * private message workings.
		 * @param e ContextMenuEvent.MENU_ITEM_SELECT
		 * 
		 */		
		protected function onPMItemSelect(e:ContextMenuEvent):void {
			//# Add the private message component
			if( ChatBox.getInputTextField() ) {
				ChatBox.getInputTextField().text = "/w "+lastUserFocused.username+" ";
				
				stage.focus = ChatBox.getInputTextField();
				
				ChatBox.getInputTextField().setSelection(
					ChatBox.getInputTextField().length, ChatBox.getInputTextField().length
				);				
			}
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
		 * Called after joining a room. API call to getUserList().
		 * @param e SmartLobbyEvent.onRoomJoin
		 * 
		 */		
		protected function onRoomJoin(e:SmartLobbyEvent):void {
			_smartLobbyInstance.getUserList();
		}
		
		/**
		 * Called when a user joins. Adds them to the list. 
		 * @param e SmartLobbyEvent.onUserJoin
		 * 
		 */		
		protected function onUserJoin(e:SmartLobbyEvent):void {
			var user:User = e.data;
			dataProvider.addItem({"Username": user.username, "userID": user.userID});
			
			this.sortItemsOn("Username");
		}
		
		/**
		 * Called when user leaves. Removes them from the list. 
		 * @param e SmartLobbyEvent.onUserLeave
		 * 
		 */		
		protected function onUserLeave(e:SmartLobbyEvent):void {
			var user:User = e.data;
			for(var i:int; i < dataProvider.length; i++) {
				var object:Object = dataProvider.getItemAt(i);
				
				if(object["Username"] == user.username) {
					dataProvider.removeItem(object);
					break;
				}
			}
		}
		
		/**
		 * Called when list of users is received from server. Loops through the Array and adds them to the lsit. 
		 * @param e SmartLobbyEvent.onUserList
		 * 
		 */		
		protected function onUserList(e:SmartLobbyEvent):void {
			var userList:Vector.<User> = e.data;
			this.dataProvider.removeAll();
			
			for(var i in userList) {
				var user:User = userList[i];
				this.dataProvider.addItem({"Username": user.username, "userID": user.userID, "userObject": user});
			}
			
			this.sortItemsOn("Username");
		}
		
	}
}