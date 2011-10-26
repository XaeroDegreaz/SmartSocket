/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby private message component class..

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
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.ui.Keyboard;
	
	import net.jerome.ui.*;
	import net.smartsocket.SmartLobbyClient;
	import net.smartsocket.smartlobby.tools.*;

	public class PrivateMessages extends MovieClip
	{
		public var tabbedScrollPane:TabbedScrollPane;
		
		public function PrivateMessages()
		{
			super();
			tab.label.text = "PM";
			hideBtn.addEventListener(MouseEvent.MOUSE_UP, hidePM);
			
			tabbedScrollPane = new TabbedScrollPane(480, 380, 0, 0, "off", "off");
			addChild(tabbedScrollPane);
			
		}
		
		public function stopDragListener(e:MouseEvent) {
			stopDrag();
		}
		
		public function startDragListener(e:MouseEvent) {
			this.startDrag();
		}
		
		public function receiveMessage(o:Object) {
			if(!visible) {
				visible = true;
			}
			
			var senderTab:TabbedScrollPaneTab = tabbedScrollPane.getTabByLabel(o.Sender);
			
			if(!senderTab) {
				tabbedScrollPane.addTab({
					label: o.Sender,
					content: new PrivateMessageContent(o)
				})
			}
			var content:PrivateMessageContent = senderTab._content as PrivateMessageContent;
			content.addMessage(o);			
		}
		
		
		public function startNewMessage(o:Object) {
			for(var i in o) {
				trace(i+" "+o[i])
			}
			if(!visible) {
				visible = true;
			}
			trace("starting new pm");
			var targetTab:TabbedScrollPaneTab = tabbedScrollPane.getTabByLabel(o.Target);
			
			if(!targetTab) {
				tabbedScrollPane.addTab({
					label: o.Target,
					content: new PrivateMessageContent(o),
					active: true
				})
			}else {
				targetTab.setActive(null, true);
			}
			
		}
		
		
		public function hidePM(e:MouseEvent):void {
			this.visible = false;
		}
	}
}