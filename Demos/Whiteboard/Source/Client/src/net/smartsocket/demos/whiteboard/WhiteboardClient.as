package net.smartsocket.demos.whiteboard {
	import flash.display.*;
	import flash.events.*;
	import flash.net.*;
	
	import net.smartsocket.SmartSocketClient;
	import net.smartsocket.protocols.json.ServerCall;
	
	public class WhiteboardClient extends SmartSocketClient {
		
		public static var myCanvas:MovieClip;
		public static var myCursor:PlayerCursor;
		public static var myUsername:String;
		private static var _instance:WhiteboardClient;
		
		public function WhiteboardClient(host:String, port:int)
		{
			super();
			_instance = this;
			customListeners.push(this);
			this.connect(host, port);
		}
		
		public static function get instance():WhiteboardClient {
			return _instance;
		}
		
		public override function onConnect(e:Event):void {
			var call:ServerCall = new ServerCall("login");
			call.put("username", Math.random().toString());
			send(call, true);
		}
		
		public function onLogin(o:Object):void {
			
			Main.instance.removeChild( Main.instance.loginClip_mc );
			Canvas.instance.mouseEnabled = true;
			
			trace("Logged in as: "+o.username);
			myUsername = o.username;
			myCanvas = Canvas.playerCanvases[o.username] = new MovieClip();
			myCursor = Canvas.playerCursors[o.username] = new PlayerCursor();
			
			Canvas.instance.addChild(myCanvas);
			Canvas.instance.addChild(myCursor);
		}
		
		
		
	}
}