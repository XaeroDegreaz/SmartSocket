/*
Version: MPL 1.1/LGPL 2.1/GPL 2.0

The contents of this file are subject to the Mozilla Public License Version 
1.1 (the "License"); you may not use this file except in compliance with
the License.

The Original Code is the SmartSocket ActionScript 3 API alert class..

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
	
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.text.TextField;
	
	public class Alert extends MovieClip {
		
		var baseW;
		var baseH;
		public var ok;
		public var cpane_mc;
		private var padding:Number = 10;
		
		public function Alert(type:String, value:*, title:String=null, draggable:Boolean=false)	{
			//ok = this["ok"];				
			var src:*;
			switch(type) {
				
				case "TextField":
				src = createTextField(0,0,380,20);
				src.autoSize = "left";
				src.multiline = true;
				src.wordWrap = true;
				src.htmlText = value;
				TextField(src).textColor = 0xFFFFFF;
				src.selectable = false;
				
				break;
				
				case "MovieClip":
				src = value;
				break;
				
				default:
				break;
			}
			
			this["tab"].label.text = title;
			this["cpane_mc"].source = src;
			
			this["cpane_mc"].content.x += padding;
			this["cpane_mc"].content.y += padding;

			this.x = 760 / 2;
			this.y = 760 / 2;
			
			addEventListener(Event.ADDED_TO_STAGE, animate_in);
			
		}
		
		
		private function createTextField(x:Number, y:Number, width:Number, height:Number):TextField {
			var r:TextField = new TextField();
			r.x = x;
			r.y = y;
			r.width = width;
			r.height = height;
			return r;
		}
	
		private function animate_in(e:Event):void {
			TweenLite.from(this, 1, {alpha: 0, scaleY: 0, ease: Elastic.easeInOut, onUpdate: revalidateButtons, onComplete:animate_in_buttons});
			
		}
	
		public function animate_out():void {
			trace("Trying to use an ease out animation!");
			TweenLite.to(this, 1, {alpha: 0, scaleX: 0, scaleY: 0, ease: Elastic.easeInOut, onComplete: removeMe});
		}
		
		private function removeMe() {
			try {
				parent.removeChild(this);
				trace("Alert removed");
			}catch(e) {
				trace("Alert not removed "+e);
			}
		}
		
		
		
		private function animate_in_buttons():void {
			TweenLite.from(ok, 1, {alpha: 0, scaleY: 0, ease: Elastic.easeInOut});
			ok.visible = true;
		}
		
		private function revalidateButtons() {
			ok.visible = false;
		}
	}	
}