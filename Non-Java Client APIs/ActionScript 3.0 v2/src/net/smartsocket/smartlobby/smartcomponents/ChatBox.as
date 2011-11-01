/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby SmartComponents ChatBox class.

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby.smartcomponents {
	import fl.controls.TextArea;
	
	import flash.events.*;
	import flash.text.TextField;
	
	import net.smartsocket.*;
	import net.smartsocket.protocols.json.*;
	import net.smartsocket.smartlobby.*;
	import net.smartsocket.smartlobby.events.*;
	
	/**
	 * This component serves to listen for SmartLobbyEvent regarding messaging
	 * and room announcements. This class can be further extended to tweak / override
	 * those event listeners more to your liking, or to add additional functionality.<br/><br/>
	 * 
	 * Listens for events: SmartLobbyEvent.onLogin, SmartLobbyEvent.onMessageRoom, SmartLobbyEvent.onMessagePrivate, SmartLobbyEvent.onRoomJoin, 
	 * SmartLobbyEvent.onUserJoin, SmartLobbyEvent.onUserKicked
	 * @author XaeroDegreaz
	 * 
	 */	
	public class ChatBox extends TextArea {
		
		protected var _smartLobbyInstance:SmartLobbyClient;
		private static var _inputTextField:TextField;
		/**
		 * Determines whether or not automatically scrolling of the scroll bar to
		 * the bottom is enabled. If true, scrolling is auto. If false, the bar will
		 * not scroll until true.
		 */		
		public var isAutoScrollToBottomEnabled = true;
		/**
		 * Determined whether or not the user will get an alert in chat when
		 * they join a new room.
		 */		
		public var isRoomJoinMessagesEnabled = true;
		/**
		 * Determines whether or not user join messages are displayed in the chat.
		 */		
		public var isUserJoinMessagesEnabled = true;
		
		/**
		 * Contruct a new ChatBox instance. The ChatBox class sub-classes a TextArea.
		 * If you want to dedicate a TextField for use with this class, call the 
		 * ChatBox.setInputTextField(textField) method.
		 * @param smartLobbyInstance A reference to the SmartLobbyClient instance.
		 * 
		 */		
		public function ChatBox(smartLobbyInstance:SmartLobbyClient = null) {
			super();
			this.enabled = false;
			if(smartLobbyInstance) {
				this.smartLobbyInstance = smartLobbyInstance;
			}
		}		
		
		/**
		 * Set the dedicated textfield to be used for typing in the chat. 
		 * @param tf
		 * 
		 */		
		public static function setInputTextField(tf:TextField):void {
			_inputTextField = tf;
		}
		
		/**
		 * Return the TextField object controlling this ChatBox's sending functionality.
		 * @return The TextField object that is being used to send messages, and is bound to this ChatBox.
		 * 
		 */		
		public static function getInputTextField():TextField {
			return _inputTextField;
		}
		
		/**
		 * Return the instance of the SmartLobbyClient object bound to this component. 
		 * @return 
		 * 
		 */		
		public function get smartLobbyInstance():SmartLobbyClient {
			return _smartLobbyInstance;
		}		
		
		/**
		 * Set the instance of the SmartLobbyClient to be used by this component. 
		 * @return 
		 * 
		 */
		public function set smartLobbyInstance(value:SmartLobbyClient):void	{
			_smartLobbyInstance = value;
			
			//# These events listeners will only used for displaying the content of the rooms.
			//# If you need some other added functionality for these events, feel free to use
			//# additional event handlers for the items below.
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onLogin, onLogin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onMessageRoom, onMessageRoom);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onMessagePrivate, onMessagePrivate);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onRoomJoin, onRoomJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserJoin, onUserJoin);
			_smartLobbyInstance.addEventListener(SmartLobbyEvent.onUserKicked, onUserKicked);
			addEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		/**
		 * Enter fram just polls to check if the auto scroll is enabled, and scroll the scrollbar if neccessary. 
		 * @param e Event.ENTER_FRAME
		 * 
		 */		
		protected function onEnterFrame(e:Event):void {
			if(isAutoScrollToBottomEnabled) {
				verticalScrollPosition = maxVerticalScrollPosition;
			}
		}
		
		/**
		 * Dispatched when user logs in. Makes this component enabled. 
		 * @param e SmartLobbyEvent.onLogin
		 * 
		 */		
		protected function onLogin(e:SmartLobbyEvent):void {
			this.enabled = true;
		}
		
		/**
		 * Dispatched when the room receives a message. Shows it in the text box. 
		 * @param e SmartLobbyEvent.onMessageRoom
		 * 
		 */		
		protected function onMessageRoom(e:SmartLobbyEvent):void {
			var call:RemoteCall = e.data;			
			appendText(call.sender.username+": "+call.message+"\n");
		}
		
		/**
		 * Dispatched when either the user sends, or receives a private message. Simple formatting in the chat box. 
		 * @param e SmartLobbyEvent.onMessagePrivate
		 * 
		 */		
		protected function onMessagePrivate(e:SmartLobbyEvent):void {
			var call:RemoteCall = e.data;
			
			appendText("[PRIVATE] ["+call.sender+"]: "+call.message+"\n");
		}
		
		/**
		 * Called when THIS user joins a room. Dispays information about the join in the chat box, if isRoomJoinMessagesEnabled == true (on by default) 
		 * @param e SmartLobbyEvent.onRoomJoin
		 * 
		 */		
		protected function onRoomJoin(e:SmartLobbyEvent):void {
			var room:Room = e.data;
			
			if(isRoomJoinMessagesEnabled) {
				appendText("** You have joined "+room.name+" **\n");
			}
		}
		
		/**
		 * Called when any user joins the room. Adds a join message to this window if isUserJoinMessagesEnabled == true (on by default)
		 * @param e SmartLobbyEvent.onUserJoin
		 * 
		 */		
		protected function onUserJoin(e:SmartLobbyEvent):void {
			var user:User = e.data;
			
			if( isUserJoinMessagesEnabled && (user.username != SmartLobbyClient.me.username) ) {
				appendText("** "+user.username+" has joined the room **\n");
			}
		}
		
		/**
		 * Dispatched when a user is kicked from the room, and displays a message in the chat about the kick. 
		 * @param e SmartLobbyEvent.onUserKicked
		 * 
		 */		
		protected function onUserKicked(e:SmartLobbyEvent):void {
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