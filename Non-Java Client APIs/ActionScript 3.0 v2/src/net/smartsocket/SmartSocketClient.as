/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API client class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket {
	import com.adobe.serialization.json.JSON;
	
	import flash.events.*;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	
	import net.smartsocket.protocols.json.RemoteCall;
	
	public class SmartSocketClient extends Socket {		
		
		//############### CONNECTION CONTROL
		public function onConnect(e:Event):void{
			trace("onConnect method currently does nothing. "+e);
		}
		public function onDisconnect(e:Event):void{trace("onDisconnect method currently does nothing. "+e);}
		public function onError(e:Event):void{trace("onError method currently does nothing. "+e);}
		
		public static var useZlib:Boolean = false;
		
		//# These are items that have registered themselves as listeners
		private static var _listeners:Array = new Array();
		private static var _instance:SmartSocketClient;
		
		public function SmartSocketClient() {
			_instance = this;
			
			addEventListener(ProgressEvent.SOCKET_DATA, this.onJSON);
			addEventListener(Event.CONNECT, this.onConnect);
			addEventListener(Event.CLOSE, this.onDisconnect);
			addEventListener(IOErrorEvent.IO_ERROR, this.onError);
		}
		/**
		 * This method allows objects outside the main SmartSocketClient instance to process data sent from the server.
		 * @param uniqueId The unique name given to the requested listener.
		 * @param object The object in question -- in most cases you will use the "this" keyword.
		 * 
		 */		
		public static function addDataListener(uniqueId:String, object:Object):void {
			_listeners[uniqueId] = object;
		}
		
		/**
		 * Remove a certain listener from the listener array.
		 * @param uniqueId The uniqueId initially given with the addListener command.
		 * 
		 */		
		public static function removeDataListener(uniqueId:String):void {
			_listeners[uniqueId] = null;
		}
		
		protected function onJSON(event:ProgressEvent):void {			
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
			
			trace("SmartSocketClient <= Received "+incoming.replace("\r\n",""));
			
			var arr:Array = incoming.split("\r\n");
			arr.pop();
			
			for(var i:Number = 0; i < arr.length; i++) {
				var data:String = arr[i];
				
				trace("SmartSocketClient ** Processing "+data);
				
				var remoteCall:RemoteCall = RemoteCall.constructFrom(data);
				
				if(remoteCall.directTo && remoteCall.directTo != "") {
					//# Direct this call to target object
					try {
						trace("SmartSocketClient ** Calling ["+remoteCall.method+"] on requested listener ["+remoteCall.directTo+"]");
						_listeners[remoteCall.directTo][remoteCall.method](remoteCall);
					}catch(e:Error) {
						trace("SmartSocketClient !! Call to ["+remoteCall.directTo+" / "+remoteCall.method+"] threw an error: "+e);
						trace("*****************");
						trace(e.getStackTrace());
						trace("*****************");
					}					
				}else {
					try {
						trace("SmartSocketClient ** No listener specified; calling ["+remoteCall.method+"] on "+this);
						_instance[remoteCall.method](remoteCall);
					}catch(e:Error) {
						trace("SmartSocketClient !! No listener specified; call to ["+remoteCall.method+" / "+this+"] threw an error: "+e);
						trace("*****************");
						trace(e.getStackTrace());
						trace("*****************");
					}
				}				
			}
		}
		
		
		public static function send(data:RemoteCall):Boolean {
			//# Encode our client call to JSON
			var json:String = JSON.encode(data);
			
			try {
				trace("SmartSocketClient => Sending "+json);
				
				if(useZlib) {
					//# Create a byteArray to hold the data
					var byteArray:ByteArray = new ByteArray();					
					//# Write the JSON String to the byte array
					byteArray.writeUTFBytes(json);					
					//# Compress the byteArray
					byteArray.compress();
					
					trace("Original Length: "+json.length+" Compressed Length + MetaData: "+byteArray.length);	
					
					_instance.writeBytes(byteArray);
				}else {
					_instance.writeUTFBytes( json+"\r");
				}
				
				_instance.flush();			
				return true;
			}catch (e:Error) {
				trace("SmartSocketClient !! Send error ("+json+"):"+e);
				return false;
			}
			return false;
		}
		//##############
	}
}