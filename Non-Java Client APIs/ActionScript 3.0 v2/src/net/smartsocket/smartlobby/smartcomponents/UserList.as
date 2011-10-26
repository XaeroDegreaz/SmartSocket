package net.smartsocket.smartlobby.smartcomponents {
	import fl.controls.DataGrid;
	
	import flash.events.*;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.SmartLobbyEvent;
	
	public class UserList extends DataGrid {
		
		private var _smartLobbyInstance:SmartLobbyClient;
		
		public function UserList(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			this.enabled = false;
			columns = ["Username"];
			
			if(smartLobbyInstance) {
				this.smartLobbyInstance = smartLobbyInstance;
			}
		}
		
		public function get smartLobbyInstance():SmartLobbyClient {
			return _smartLobbyInstance;
		}
		
		public function set smartLobbyInstance(value:SmartLobbyClient):void	{
			_smartLobbyInstance = value;
			
			//# These events listeners will only used for displaying the content of the rooms.
			//# If you need some other added functionality for these events, feel free to use
			//# additional event handlers for the items below.
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLogin, onLogin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomJoin, onRoomJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserJoin, onUserJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserLeave, onUserLeave);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserList, onUserList);
			
			this.addEventListener(MouseEvent.DOUBLE_CLICK, onMouseClick);
		}
		
		private function onMouseClick(e:MouseEvent):void {
			
		}
		
		private function onLogin(e:SmartLobbyEvent):void {
			this.enabled = true;
		}
		
		private function onRoomJoin(e:SmartLobbyEvent):void {
			_smartLobbyInstance.getUserList();
		}
		
		private function onUserJoin(e:SmartLobbyEvent):void {
			var user:User = e.data;
			dataProvider.addItem({"Username": user.username, "userID": user.userID});
			
			this.sortItemsOn("Username");
		}
		
		private function onUserLeave(e:SmartLobbyEvent):void {
			var user:User = e.data;
			for(var i:int; i < dataProvider.length; i++) {
				var object:Object = dataProvider.getItemAt(i);
				
				if(object["Username"] == user.username) {
					dataProvider.removeItem(object);
					break;
				}
			}
		}
		
		private function onUserList(e:SmartLobbyEvent):void {
			var userList:Vector.<User> = e.data;
			this.dataProvider.removeAll();
			
			for(var i in userList) {
				var user:User = userList[i];
				this.dataProvider.addItem({"Username": user.username, "userID": user.userID});
			}
			
			this.sortItemsOn("Username");
		}
		
	}
}