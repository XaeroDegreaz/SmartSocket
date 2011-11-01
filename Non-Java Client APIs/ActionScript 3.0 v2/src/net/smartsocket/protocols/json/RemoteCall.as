/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API JSON RemoteCall class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2011
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.protocols.json{
	import com.adobe.serialization.json.JSONDecoder;
	
	/**
	 * The RemoteCall class is realy the backbone of SmartSocket. It allows us to easily send serialized objects back
	 * and forth between the client and server in a predictable protocol. This is a dynamic class, so you are able to add
	 * your own properties to this class before serialization. 
	 * @author XaeroDegreaz
	 * 
	 */	
	dynamic public class RemoteCall	{
		/**
		 * This represents the target method on the server extension that you want to call. 
		 */		
		public var method:String;
		/**
		 * This represents the dataListener on SmartSocketClient to refer all contents of a response to this call. 
		 */		
		public var directTo:Object;
		
		/**
		 * Construct a new RemoteCall 
		 * @param method The server extension method to execute
		 * @param directTo When a response from the server is received, the SmartSocket client will call the return call on this dataListener
		 * 
		 */		
		public function RemoteCall(method:String, directTo:String = "") {
			this.method = method;
			this.directTo = directTo;
		}
		
		/**
		 * Construct a remote call from a JSON formatted string. 
		 * @param data The JSON string of data to construct from.
		 * @return The new RemoteCall object
		 * 
		 */		
		public static function constructFrom(data:String):RemoteCall {			
			var decoder:JSONDecoder = new JSONDecoder(data);
			var json:Object = decoder.getValue();			
			var call:RemoteCall = new RemoteCall(json["method"]);
			
			for (var i in json) {
				try {
					call[i] = json[i];
				}catch(e:Error) {
					trace("Error populating RemoteCall: "+e);
				}
			}
			
			return call;
		}
	}
}