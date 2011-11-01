/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby Main SmartLobby example class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package {	
	import com.adobe.serialization.json.*;
	
	import fl.controls.CheckBox;
	import fl.controls.TextArea;
	import fl.controls.TextInput;
	
	import flash.display.DisplayObject;
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.ui.Keyboard;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.RemoteCall;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.SmartLobbyEvent;
	import net.smartsocket.smartlobby.smartcomponents.*;
	
	public class Main extends MovieClip	{
		
		var ss:SmartLobbyClient = new SmartLobbyClient();
		public static var instance:Main;
		
		public function Main() {
			super();
			instance = this;
			ChatBox.setInputTextField(txtMessage.textField);
			//# These listeners are purely for the example components being used on the stage.
			//# You will need to cete your own components and logic for these items if you
			//# need more complex functionality -- they will not be incorporated in the
			//# base smartcomponents package (That's reserved soley for ChatBox, RoomList, and UserList)
			btnSend.addEventListener(MouseEvent.CLICK, onBtnSendClick);
			btnCreate.addEventListener(MouseEvent.CLICK, onBtnCreateClick);
			btnLock.addEventListener(MouseEvent.CLICK, onBtnLockClick);
			txtMessage.addEventListener(KeyboardEvent.KEY_DOWN, onTxtMessageKeyDown);
			cbDisableScroll.addEventListener(MouseEvent.CLICK, onCbAutoScrollClick);
			
			//# All SmartLobby SmartComponents require a valid reference to a SmartLobbyClient object instance.
			RoomList(dgRoomList).smartLobbyInstance = ss;
			UserList(dgUserList).smartLobbyInstance = ss;
			ChatBox(txtChatBox).smartLobbyInstance = ss;
			LoginPopup(mcLoginPopup).smartLobbyInstance = ss;
			
			//# Extras
			TextInput(txtMessage).condenseWhite;
			
			//# Off we go
			SmartSocketClient.addDataListener("main", this);
			ss.connect("jdoby.dyndns.org", 8888);			
		}
		
		//# Example components event handlers
		//# *********************************
		
		private function onBtnCreateClick(e:MouseEvent):void {
			var create:CreateRoomPopup = new CreateRoomPopup(ss);
			addChild(create);
		}
		
		private function onBtnLockClick(e:MouseEvent):void {
			ss.toggleRoomLock();
		}
		
		//# This one is taking a MouseEvent, but since we aren't using that event for anything
		//# let's mark the param as event so we can piggy-back this with another handler
		
		private function onBtnSendClick(e:Event):void {
			//# Surely you'll want more security handling in here, however html is disabled (by default ChatBox uses plain text),
			//# and JSON parsing on the server/client is great about preventing injection attacks as they are (no credit to me, there!).
			//# Basically, it's not possible for a user to inject their own JSON, since everything sent to the server is purely evaluated
			//# as a string.
			if(txtMessage.text == "") {
				return;
			}
			
			//# Check to see if this should be routed to private message
			var arr:Array = txtMessage.text.split(" ", 3);
			if(arr[0] == "/w" || arr[0] == "/W") {
				ss.sendPrivateMessage(arr[1], arr[2]);
				txtMessage.text = ""
				return;
			}
			
			ss.sendRoomMessage(txtMessage.text);
			txtMessage.text = ""
		}
		
		private function onTxtMessageKeyDown(e:KeyboardEvent) {
			if(e.keyCode == Keyboard.ENTER) {
				onBtnSendClick(e);
			}
		}
		
		private function onCbAutoScrollClick(e:MouseEvent) {
			ChatBox(txtChatBox).isAutoScrollToBottomEnabled = (cbDisableScroll.selected) ? false : true;
		}
		
	}	
	
}