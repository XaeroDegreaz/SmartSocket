/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API JSON ServerCall class..

The Initial Developer of the Original Code is
Jerome Doby www.smartsocket.net.
Portions created by the Initial Developer are Copyright (C) 2009-2010
the Initial Developer. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of
either of the GNU General Public License Version 2 or later (the "GPL")
or
the terms of any one of the MPL, the GPL or the LGPL.
*/
package net.smartsocket.protocols.json{
	import com.adobe.serialization.json.JSONDecoder;

	dynamic public class RemoteCall	{
		public var method:String;
		public var directTo:Object;
		
		public function RemoteCall(method:String, directTo:String = "") {
			this.method = method;
			this.directTo = directTo;
		}
		
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