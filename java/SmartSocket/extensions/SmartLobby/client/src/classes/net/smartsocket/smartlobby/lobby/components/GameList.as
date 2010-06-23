/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby game list component class..

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
	import fl.data.DataProvider;
	
	import flash.display.MovieClip;
	import flash.events.MouseEvent;
	
	import net.smartsocket.smartlobby.tools.*;
	import net.smartsocket.smartlobby.SmartLobby;
	
	public class GameList extends MovieClip
	{
		public var listType:MovieClip = null;
		
		public function GameList()
		{
			tab.label.text = "Game Rooms";
		}
		
		public function switchTo(type:String) {
			if(listType != null) {
				removeChild(listType);
			}
			
			switch(type) {
				case "gl":
				listType = new gl();
				listType.x = 0;
				listType.y = 0;
				
								
				listType._list.columns = ["ID","Name","Map","Current","Max","Creator","Status","Private"];
				listType._list.dataProvider = new DataProvider(new Array());
				
				listType.addEventListener(MouseEvent.DOUBLE_CLICK, joinRoom);
				
				break;
				
				case "tl":
				listType = new tl();
				listType.x = 10;
				listType.y = 10;
				
				var teamListColumns:Array = ["Username", "Status"];
				
				listType.unassigned.columns = teamListColumns;				
				listType.red.columns = teamListColumns;
				listType.blue.columns = teamListColumns;
				
				listType.unassigned.getColumnAt(0).width = 95;
				listType.red.getColumnAt(0).width = 95;
				listType.blue.getColumnAt(0).width = 95;
				
				listType.unassigned.dataProvider = new DataProvider(new Array());
				listType.red.dataProvider = new DataProvider(new Array());
				listType.blue.dataProvider = new DataProvider(new Array());
				
				if(SmartLobby.my.createdRoom != SmartLobby.my.room) {
					//# Disable these unless they are the creator of the room
					listType.startBtn.visible = false;
					listType.optionsBtn.visible = false;
					listType.kickBtn.visible = false;
				}
				
				//# Disable the ready btn until they are on red or blue team
				listType.readyBtn.visible = false;
				
				listType.redBtn.addEventListener(MouseEvent.MOUSE_UP, joinRed);
				listType.blueBtn.addEventListener(MouseEvent.MOUSE_UP, joinBlue);
				listType.readyBtn.addEventListener(MouseEvent.MOUSE_UP, ready);
				listType.leaveBtn.addEventListener(MouseEvent.MOUSE_UP, leave);
				listType.optionsBtn.addEventListener(MouseEvent.MOUSE_UP, options);
				listType.startBtn.addEventListener(MouseEvent.MOUSE_UP, start);
				listType.kickBtn.addEventListener(MouseEvent.MOUSE_UP, kick);
				
				break;
			}			
			addChild(listType);		
		}
		
		public function joinRoom(e:MouseEvent) {
			SmartLobby.customListeners["server"].joinRoom(Number(listType._list.selectedItem.ID));
		}
		public function joinRed(e:MouseEvent) {
			SmartLobby.customListeners["server"].joinTeam("red");
			SmartLobby.my.team = "red";
			listType.readyBtn.visible = true;
			
		}
		public function joinBlue(e:MouseEvent) {
			SmartLobby.customListeners["server"].joinTeam("blue");
			SmartLobby.my.team = "blue";
			listType.readyBtn.visible = true;
		}
		public function ready(e:MouseEvent) {
			SmartLobby.customListeners["server"].ready();
		}
		public function leave(e:MouseEvent) {
			var o:Object = ["leaveGameRoom",{}]
			SmartLobby.customListeners["server"].send(o);
			SmartLobby.customListeners["server"].leaveRoom();
			SmartLobby.my.team = null;
		}
		public function options(e:MouseEvent) {
			//Globals.server.gameOptions();
		}
		public function start(e:MouseEvent) {
			var o:Object = ["startGame",{}];
			SmartLobby.customListeners["server"].send(o);
		}
		public function kick(e:MouseEvent) {
			//Globals.server.kick();
		}

	}
}