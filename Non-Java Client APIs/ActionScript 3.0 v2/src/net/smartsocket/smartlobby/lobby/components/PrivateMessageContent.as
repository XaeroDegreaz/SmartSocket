package net.smartsocket.smartlobby.lobby.components {
	
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.text.TextField;
	import flash.ui.Keyboard;
	
	import net.jerome.events.*;
	import net.jerome.ui.*;
	import net.smartsocket.SmartLobbyClient;
	
	public class PrivateMessageContent extends MovieClip {
		public var in_txt:TextField;
		public var out_txt:TextField;
		public var _data:Object;
		
		public function PrivateMessageContent(o:Object) {
			super();
			_data = o;
			in_txt.htmlText = "";
			addEventListener(TabbedScrollPaneEvent.ADDED, onTabAdded);
			out_txt.addEventListener(KeyboardEvent.KEY_UP, sendMessage);
		}
		
		public function addMessage(o:Object):void {
			in_txt.appendText(o.Sender+": "+o.Message+"\n");
			scrollBar_mc.update();
			scrollBar_mc.scrollPosition = scrollBar_mc.maxScrollPosition;
		}
		
		private function sendMessage(e:KeyboardEvent):void {
			if(e.keyCode == Keyboard.ENTER) {
				if (out_txt.text != "") {
					in_txt.appendText(SmartLobbyClient.my.Username+": "+out_txt.text+"\n");
					SmartLobbyClient.customListeners["server"]
						.sendPrivate(_data.uid, out_txt.text);
					out_txt.text = "";
				}
			}
		}
		
		private function onTabAdded(e:TabbedScrollPaneEvent):void {
			trace("A new private message content has been created.");
		}
		
	}
}