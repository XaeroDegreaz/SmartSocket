/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby CreateRoomPopup class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.smartcomponents {
	import fl.controls.NumericStepper;
	
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.filters.DropShadowFilter;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.*;
	
	public class CreateRoomPopup extends MovieClip {
		
		private var _smartLobbyInstance:SmartLobbyClient;
		
		public function CreateRoomPopup(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			if(smartLobbyInstance) {
				this.smartLobbyInstance = smartLobbyInstance;
			}
			
			//# Some beautification
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
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onCreateRoomError, onCreateRoomError);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onCreateRoom, onCreateRoom);
			
			//# Component events
			this["btnCreate"].addEventListener(MouseEvent.CLICK, onBtnCreateClick);
			this["btnCancel"].addEventListener(MouseEvent.CLICK, onBtnCancelClick);
		}
		
		private function onCreateRoomError(e:SmartLobbyEvent):void {
			
		}
		
		private function onCreateRoom(e:SmartLobbyEvent):void {
			//# For some reason, without this try block here, this throws an error
			//# about some null reference in regards to removeChild -- yet it still closes
			//# will research a little deeper at another time.
			try{ 
				this.parent.removeChild(this);
			}catch(e:Error) {
				
			}
				
		}
		
		private function onBtnCreateClick(e:MouseEvent):void {
			_smartLobbyInstance.createRoom(this["txtName"].text, this["txtPassword"].text, NumericStepper(this["spMaxUsers"]).value);
		}
		
		private function onBtnCancelClick(e:MouseEvent):void {
			this.parent.removeChild(this);
		}
		
	}
}