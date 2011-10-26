/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby userlist component class..

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
	import flash.events.MouseEvent;
	import net.smartsocket.smartlobby.tools.*;
	import net.smartsocket.SmartLobbyClient;
	
	public class UserList extends MovieClip
	{
		public function UserList()
		{
			tab.label.text = "User List";
			this.addEventListener(MouseEvent.DOUBLE_CLICK, startPM);
		}
		
		public function startPM(e:MouseEvent) {
			
			SmartLobbyClient.lobby.pm.visible = true;
			var o:Object = {Target:_list.selectedItem.label, uid:_list.selectedItem.data};
			SmartLobbyClient.lobby.pm.startNewMessage(o);
		}

	}
}