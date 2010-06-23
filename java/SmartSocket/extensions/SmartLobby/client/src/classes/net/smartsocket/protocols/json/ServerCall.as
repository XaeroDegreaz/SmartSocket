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
package net.smartsocket.protocols.json
{
	dynamic public class ServerCall extends Array
	{
		private var properties:Object = {};
		
		public function ServerCall(method:String)
		{
			push(method);
			push(properties);
			
		}
		
		public function put(key:Object, value:Object):void {
			properties[key] = value;
		}
	}
}