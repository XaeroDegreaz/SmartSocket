package net.smartsocket.demos.whiteboard {
	import flash.display.*;
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.net.*;
	
	public class LoginClip extends MovieClip {
		
		public function LoginClip()
		{
			super();
			
			btnJoin.addEventListener(MouseEvent.CLICK, onClick);
		}
		
		private function onClick(e:MouseEvent):void {
			Main.whiteboardClient = new WhiteboardClient(txtHost.text, int(txtPort.text));
		}
		
	}
}