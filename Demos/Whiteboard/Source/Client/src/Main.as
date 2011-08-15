package {
	import flash.display.*;
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.net.*;
	
	import net.smartsocket.demos.whiteboard.WhiteboardClient;
	
	public class Main extends MovieClip {
		
		private static var _instance:Main;
		
		public static var whiteboardClient:WhiteboardClient;
		
		public function Main()
		{
			super();
			_instance = this; 
		}
		
		public static function get instance():Main {
			return _instance;
		}
		
	}
}