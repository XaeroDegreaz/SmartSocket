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
	
	public class RoomList extends DataGrid {
		
		private var _smartLobbyInstance:SmartLobbyClient;
		
		/**
		 * This data grid manages events specifit to room displaying. It can be instantiated
		 * with a reference to a SmartLobbyClient instance, or one can be assigned to it manually.
		 * Either way, it is critical for this componenet to have a reference to the object, or
		 * none of the events will be dispatched here.
		 * @param smartLobbyInstance
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

		public function get smartLobbyInstance():SmartLobbyClient {
			return _smartLobbyInstance;
		}

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
			
			this.addEventListener(MouseEvent.DOUBLE_CLICK, onMouseClick);
		}
		
		private function onLogin(e:SmartLobbyEvent):void {
			this.enabled = true;
		}
		
		private function onMouseClick(e:MouseEvent):void {
			_smartLobbyInstance.joinRoom(this.selectedItem["ID"], this.selectedItem["Name"]);
		}
		
		private function onRoomList(e:SmartLobbyEvent):void {
			dataProvider.removeAll();
			
			for(var i in e.data) {
				var room:Room = e.data[i];
				
				this.dataProvider.addItem({
					"ID": room.roomID,
					"Name": room.name,
					"Max": room.maxUsers,
					"Creator": room.owner.username,
					"Current": room.currentUsers,
					"Private": room.isPrivate
				});
			}
			
			this.sortItemsOn("ID");
		}
		
		private function onRoomAdd(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			this.dataProvider.addItem({
				"ID": room.roomID,
				"Name": room.name,
				"Max": room.maxUsers,
				"Creator": room.owner.username,
				"Current": room.currentUsers,
				"Private": room.isPrivate
			});
		}
		
		private function onRoomDelete(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			for(var i:int; i < dataProvider.length; i++) {
				var object:Object = dataProvider.getItemAt(i);
				
				if(object["ID"] == room.roomID) {
					dataProvider.removeItem(object);
					break;
				}
			}
		}
		
		private function onRoomCountUpdate(e:SmartLobbyEvent):void {
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