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
	import com.adobe.serialization.json.JSONDecoder;
	import com.dynamicflash.util.Base64;
	
	import flash.events.*;
	import flash.events.EventDispatcher;
	import flash.net.Socket;
	import flash.utils.ByteArray;
	
	import net.smartsocket.smartlobby.lobby.Lobby;
	
	public class SmartSocketClient extends Socket{
		
		
		
		//############### CONNECTION CONTROL
		public function onConnect(e:Event):void{trace("onConnect method currently does nothing. "+e);}
		public function onDisconnect(e:Event):void{trace("onDisconnect method currently does nothing. "+e);}
		public function onError(e:Event):void{trace("onError method currently does nothing. "+e);}
				
		public static var customListeners:Array = new Array();
		
		public static var useZlib:Boolean = false;
		
		public function SmartSocketClient() {
			addEventListener(ProgressEvent.SOCKET_DATA, this.onJSON);
			addEventListener(Event.CONNECT, this.onConnect);
			addEventListener(Event.CLOSE, this.onDisconnect);
			addEventListener(IOErrorEvent.IO_ERROR, this.onError);
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
			
			trace("SmartSocketClient => Received "+incoming.replace("\r",""));
			
			var arr:Array = incoming.split("\r");
			arr.pop();
			
			for(var i:Number = 0; i < arr.length; i++) {
				var data:String;
				
				data = arr[i];
				
				trace("SmartSocketClient => Processing "+data);
				
				var decoder:JSONDecoder = new JSONDecoder(data);
				var json:Array = decoder.getValue();				
				var method:String = json[0];				
				var params:* = json[1];
				
				
				for(var j:String in customListeners) {
					
					if(customListeners[j].hasOwnProperty(method)) {
						try {
							trace("SmartSocketClient => Trying "+method+" on "+customListeners[j]);
							customListeners[j][method](params);
						}catch(e:Error) {
							trace("SmartSocketClient => "+method+" has errors: "+e);
						}finally {
							
						}
						break;
					}
					
				}
				
			}
		}
		
		
		public function send(data:Object):Boolean {
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
					this.writeBytes(byteArray);
				}else {
					this.writeUTFBytes( json+"\r");
				}
				
				this.flush();				
				return true;
			}catch (e:Error) {
				trace("SmartSocketClient => Send error ("+json+"):"+e);
				return false;
			}
			return false;
		}
		//##############
	}
}