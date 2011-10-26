package net.smartsocket.smartlobby.smartcomponents {
	import fl.controls.TextArea;
	
	import flash.events.*;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.*;
	
	public class ChatBox extends TextArea {
		
		private var _smartLobbyInstance:SmartLobbyClient;
		public var isAutoScrollToBottomEnabled = true;
		public var isRoomJoinMessagesEnabled = true;
		public var isUserJoinMessagesEnabled = true;
		
		public function ChatBox(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			this.enabled = false;
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
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onMessageRoom, onMessageRoom);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomJoin, onRoomJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserJoin, onUserJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserKicked, onUserKicked);
			addEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		private function onEnterFrame(e:Event):void {
			if(isAutoScrollToBottomEnabled) {
				verticalScrollPosition = maxVerticalScrollPosition;
			}
		}
		
		private function onLogin(e:SmartLobbyEvent):void {
			this.enabled = true;
		}
		
		private function onMessageRoom(e:SmartLobbyEvent):void {
			var call:RemoteCall = e.data;			
			appendText(call.sender.username+": "+call.message+"\n");
		}
		
		private function onRoomJoin(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			if(isRoomJoinMessagesEnabled) {
				appendText("** You have joined "+room.name+" **\n");
			}
		}
		
		private function onUserJoin(e:SmartLobbyEvent):void {
			var user:User = e.data;
			
			if( isUserJoinMessagesEnabled && (user.username != SmartLobbyClient.me.username) ) {
				appendText("** "+user.username+" has joined the room **\n");
			}
		}
		
		private function onUserKicked(e:SmartLobbyEvent):void {
			var user:User = e.data.user;
			var reason:String = e.data.reason;
			
			if(SmartLobbyClient.me.username == user.username) {
				//# Show some alert box that they had been kicked.
				//# Server handles the rest...
			}
			
			appendText("** "+user.username+" was kicked ["+reason+"] **\n");			
		}
		
	}
}