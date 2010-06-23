/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby chat component class..

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
	import flash.events.*;
	import flash.ui.Keyboard;
	
	import net.smartsocket.smartlobby.tools.*;
	import net.smartsocket.smartlobby.SmartLobby;
	
	public class Chat extends MovieClip
	{
		
		public function Chat()
		{
			tab.label.text = "Chat";
			in_txt.text = "Welcome to Tactics of War!";
			
			out_txt.addEventListener(KeyboardEvent.KEY_DOWN, handle_keydown);
		}
		
		private function handle_keydown(e:KeyboardEvent) {
			
			if(e.keyCode == Keyboard.ENTER) {
				
				if(out_txt.text != "") {
					SmartLobby.customListeners["server"].sendRoom(out_txt.text);
					out_txt.text = "";
				}
			}
			
		}

	}
}