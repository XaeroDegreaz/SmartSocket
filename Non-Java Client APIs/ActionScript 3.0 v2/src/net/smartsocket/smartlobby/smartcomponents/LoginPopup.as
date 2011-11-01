/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby LoginPopup class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.smartcomponents {
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.filters.DropShadowFilter;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.*;
	
	public class LoginPopup extends MovieClip {
		
		private var _smartLobbyInstance:SmartLobbyClient;
		
		public function LoginPopup(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			//SmartSocketClient.addDataListener("login", this);
			if(smartLobbyInstance) {
				this.smartLobbyInstance = smartLobbyInstance;
			}
			
			//# Some beutification
			this.filters = [new DropShadowFilter(4, 45, 0, .5, 4, 4, 1, 3, false, false, false)];
		}
		
		public function get smartLobbyInstance():SmartLobbyClient {
			return _smartLobbyInstance;
		}
		
		public function set smartLobbyInstance(value:SmartLobbyClient):void	{
			_smartLobbyInstance = value;
			
			//# These events listeners will only used for displaying the content of the rooms.
			//# If you need some other added functionality for these events, feel free to use
			//# additional event handlers for the items below.
			//# Will be dispatched from SmartSocketClient
			_smartLobbyInstance.addEventListener(Event.CONNECT, onConnect);
			_smartLobbyInstance.addEventListener(IOErrorEvent.IO_ERROR, onError);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLogin, onLogin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLoginError, onLoginError);
			
			//# Component events
			btnLogin.addEventListener(MouseEvent.CLICK, onBtnLoginClick);
		}
		
		private function onConnect(e:Event):void {
			txtStatus.text = "The server is online; please login.";
		}
		
		private function onError(e:IOErrorEvent):void {
			txtStatus.text = "Connection to the server could not be established.";
		}
		
		private function onBtnLoginClick(e:MouseEvent):void {
			if(txtUsername.text == "") {
				return;
			}
			
			_smartLobbyInstance.login(txtUsername.text, txtPassword.text);
		}
		
		private function onLogin(e:SmartLobbyEvent):void {
			var call:RemoteCall = e.data;
			
			SmartLobbyClient.me = new User(call);
			_smartLobbyInstance.getRoomList();
			_smartLobbyInstance.joinRoom(Number.NaN,"Room 2");
			
			this.parent.removeChild(this);
		}
		
		
		private function onLoginError(e:SmartLobbyEvent):void {
			txtStatus.text = e.data.error;
		}
		
		
		
	}
}