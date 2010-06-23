/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API SmartLobby private chat component class..

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
	import com.greensock.TweenLite;
	import com.greensock.easing.*;
	import com.greensock.events.TweenEvent;
	
	import flash.display.MovieClip;
	
	import net.smartsocket.smartlobby.tools.*;

	public class PrivateChatTab extends MovieClip
	{
		public var uid;
		public var blinkTween:TweenLite;
		public var blinking:Boolean = false;
		public function PrivateChatTab(myName:String)
		{
			this.name = myName;
			this.cacheAsBitmap = true;
			super();
		}
		
		public function blink(state:String):void {
			
			switch(state) {
				
				case "start":
				blinking = true;
				blinkTween = new TweenLite(this, .5, {y: this.y+5, ease: Strong.easeOut, onComplete: startYoyo});
				break;
				
				case "stop":
				blinking = false;
				this.y = 0;
				blinkTween.complete();
				break;
				
				
			}
		}
		
		public function startYoyo(e:TweenLite) {
			blinkTween.reverse();
		}
		
	}
}