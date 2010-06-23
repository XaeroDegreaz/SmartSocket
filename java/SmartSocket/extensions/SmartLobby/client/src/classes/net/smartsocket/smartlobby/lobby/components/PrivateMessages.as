/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby private message component class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.lobby.components
{
	import flash.display.MovieClip;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.ui.Keyboard;
	
	import net.smartsocket.smartlobby.tools.*;
	import net.smartsocket.smartlobby.SmartLobby;

	public class PrivateMessages extends MovieClip
	{
		public var messageContainer:Array = new Array();
		public var messageTabs:MovieClip = new MovieClip();
		public var currentTarget = null;
		public var currentTargetUserId = null;
		
		public function PrivateMessages()
		{
			super();
			tab.label.text = "Private Chats";
			out_txt.addEventListener(KeyboardEvent.KEY_UP, send);
			hideBtn.addEventListener(MouseEvent.MOUSE_UP, hidePM);
			
		}
		
		public function stopDragListener(e:MouseEvent) {
			stopDrag();
		}
		
		public function startDragListener(e:MouseEvent) {
			this.startDrag();
		}
		
		public function newMessage(o:Object) {
			
			if(messageContainer[o.Sender] == undefined) {
				var j:Number = 0;
				for(var i:String in messageContainer) {
					j++;
				}
				
				messageContainer[o.Sender] = [o.Sender+": "+o.Message];
				
				if(currentTarget == o.Sender || currentTarget == null) {
					currentTarget = o.Sender;
					currentTargetUserId = o.uid;
					
					in_txt.text = "";
					in_txt.htmlText += o.Sender+": "+o.Message;
				}else {
					//# User it in another user's tab
					//$ make it blink or something.
				}
				
				var chatTab:PrivateChatTab = new PrivateChatTab(o.Sender);
				
				chatTab.label_txt.text = o.Sender;
				chatTab.uid = o.uid;
				chatTab.addEventListener(MouseEvent.MOUSE_UP, getMessages);				
				
				chatTab.x = j * chatTab.width;
				
				messageTabs.addChild(chatTab);
				
				tab_bar.source = messageTabs;
				tab_bar.update();
			}else {
				messageContainer[o.Sender].push(o.Sender+": "+o.Message);
				
				if(currentTarget == o.Sender || currentTarget == null) {					
					in_txt.htmlText += o.Sender+": "+o.Message;
					
				}else {
					//# User it in another user's tab
					//$ make it blink or something.
					
					var targetTab = messageTabs.getChildByName(o.Sender);
					targetTab.blink("start");
					
				}
			}
			
		}
		
		public function getMessages	(sender:MouseEvent) {
			in_txt.text = "";
			currentTarget = sender.target.text;			
			currentTargetUserId = sender.target.parent.uid;
			
			var targetTab = messageTabs.getChildByName(sender.target.text);
			
			if(targetTab.blinking) {
				targetTab.blink("stop");
			}
			
			for(var i:Number = 0; i < messageContainer[sender.target.text].length; i++) {
				var msg:String = messageContainer[sender.target.text][i];
				in_txt.htmlText += msg;
				scroll_bar.update();
			}
		}
		
		public function startPM(o:Object) {
			currentTarget = o.Target;
			currentTargetUserId = o.uid;
			
			
			if(messageContainer[o.Target] == undefined) {
				var j:Number = 0;
				for(var i:String in messageContainer) {
					j++;
				}
				
				messageContainer[o.Target] = [];
				
				in_txt.text = "";
				
				var chatTab:PrivateChatTab = new PrivateChatTab(o.Target);
				chatTab.label_txt.text = o.Target;
				chatTab.uid = o.uid;
				trace("Starting pm with: "+chatTab.uid);
				chatTab.addEventListener(MouseEvent.MOUSE_UP, getMessages);				
				
				chatTab.x = j * chatTab.width;
				
				messageTabs.addChild(chatTab);
				
				tab_bar.source = messageTabs;
				tab_bar.update();
			}else {
				in_txt.text = "";
				for(j = 0; j < messageContainer[currentTarget].length; j++) {
					var msg:String = messageContainer[currentTarget][j];
					in_txt.htmlText += msg;
					scroll_bar.update();
				}
			}
		}
		
		public function send(e:KeyboardEvent) {
			if(e.keyCode == Keyboard.ENTER) {
				if (out_txt.text != "") {
					messageContainer[currentTarget].push(SmartLobby.my.Username+": "+out_txt.text);
					in_txt.htmlText += SmartLobby.my.Username+": "+out_txt.text;
					SmartLobby.customListeners["server"].sendPrivate(currentTargetUserId, out_txt.text);
					out_txt.text = "";
				}
			}
		}
		
		public function hidePM(e:MouseEvent):void {
			this.visible = false;
		}
	}
}