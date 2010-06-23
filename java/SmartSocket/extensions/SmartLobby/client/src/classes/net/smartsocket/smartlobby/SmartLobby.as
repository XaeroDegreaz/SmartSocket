/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby client class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.smartlobby {
	import com.adobe.serialization.json.JSON;
	import com.adobe.serialization.json.JSONDecoder;
	import com.dynamicflash.util.Base64;
	
	import flash.events.*;
	import flash.events.EventDispatcher;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	
	import net.smartsocket.SmartSocketClient;
	import net.smartsocket.smartlobby.events.*;
	import net.smartsocket.smartlobby.lobby.Lobby;
	import net.smartsocket.smartlobby.tools.*;

	 public class SmartLobby extends SmartSocketClient{
		 
		 
		
		//############### CONNECTION CONTROL
		public override function onConnect(e:Event):void{trace("onConnect method currently does nothing. "+e);}
		public override function onDisconnect(e:Event):void{trace("onDisconnect method currently does nothing. "+e);}
		public override function onError(e:Event):void{trace("onError method currently does nothing. "+e);}
		
		//############### ACCOUNT CONTROL
		public function onLogin(e:Object):void{trace("onLogin method currently does nothing. "+e);}
		public function onInitUserObject(e:Object):void{trace("onInitUserObject method currently does nothing. "+e);}
		
		public static var lobby:Lobby;
		public static var my:Object = [];
		public static var gameRunning:Boolean = false;
		public static var customListeners:Array = new Array();
		
		public function SmartLobby() {
			super();
		}
		
		protected override function onJSON(event:ProgressEvent):void {
			var incoming:String;
			
			if(useZlib) {
				//# Create a byteArray to hold our data
				var byteArray:ByteArray = new ByteArray();
				
				//# Read the ByteArray data sent from the server into byteArray
				readBytes(byteArray);
				
				//# Uncompress the ZLIB string.
				byteArray.uncompress();
				
				//# Read the JSON string that was uncompressed.
				incoming = byteArray.readUTFBytes(byteArray.bytesAvailable);				
			}else {
				incoming = this.readUTFBytes(this.bytesAvailable);
			}
			
			trace("SmartLobby => Received "+incoming.replace("\r",""));
			var arr:Array = incoming.split("\r");
			arr.pop();
			
			for(var i:Number = 0; i < arr.length; i++) {
				var data:String;				
				
				data = arr[i];				
				
				trace("SmartLobby => Processing "+data);
				
				var decoder:JSONDecoder = new JSONDecoder(data);
				var json:Array = decoder.getValue();				
				var method:String = json[0];				
				var params:* = json[1];
				
				try {
					trace("Testing against SmartLobbyEvent class...");
					SmartLobby.lobby.dispatchEvent( new SmartLobbyEvent(SmartLobbyEvent[method], params) );
					//SmartLobby.lobby[method](params);
					trace("SmartLobbyEvent "+method+" has been dispatched.");
				}catch(e:Error) {
					trace(method+" is not a SmartLobbyEvent. Cycling SmartLobby.customListeners: "+e);
					for(var j:String in SmartLobby.customListeners) {
						 
						if(SmartLobby.customListeners[j].hasOwnProperty(method)) {
							try {
								trace("SmartLobby => Trying "+method+" on "+SmartLobby.customListeners[j]);
								SmartLobby.customListeners[j][method](params);
							}catch(e:Error) {
								trace("SmartLobby => "+method+" has errors: "+e);
							}finally {
								
							}
							break;
						}
						
					}
				}finally {
					trace("=============");
				}
			}
		}
		
		//# SmartLobby core functions.
		public function login(details:Object):void {
			var o:Object = ["login", details];				
			send(o);
		}
		
		public function joinLobby():void {
			var o:Object = ["joinLobby",{}];
			send(o);
		}
		
		public function leaveLobby():void {
			var o:Object = ["leaveLobby",{}];
			send(o);
		}
		
		public function joinRoom(room:Number):void {
			var o:Object = ["joinRoom",{
				"_id" : room
			}];
			send(o);
		}
		
		public function getUserList():void {
			var o:Object = ["getUserList",{}];
			send(o);
		}
		
		public function getRoomList():void {
			var o:Object = ["getRoomList",{}];
			send(o);
		}
		
		public function createRoom(details:Object):void {
			var o:Object = ["createRoom", details];			
			send(o);
		}
		
		public function leaveRoom():void {
			var o:Object = ["leaveRoom",{}];			
			send(o);			
		}
		
		public function sendRoom(message:String):void {
			var o:Object = ["sendRoom",{
				"_message" : message
			}];				
			send(o);
		}
		
		public function sendPrivate(target:String, message:String):void {
			var o:Object = ["sendPrivate",{
				"_message" : message,
				"_target" : target
			}];				
			send(o);
		}		
	}
}