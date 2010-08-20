package net.smartsocket.utils {
	
	import flash.utils.getQualifiedClassName;
	
	public class ConsoleLog {
		
		public static const DEBUG:String = "DEBUG";
		public static const INFO:String = "INFO";
		public static const WARNING:String = "WARNING";
		public static const CRITICAL:String = "CRITICAL";
		
		public static var level:String = DEBUG;
		
		public function ConsoleLog() {
			//# Unused constructor
		}
		
		public static function log(message:*, caller:*, logLevel:String = "DEBUG"):void {
			
			var array:Array = getQualifiedClassName(caller).split("::");
			var className:String;
			
			if(array.length == 2) {
				className = array[1];
			}else {
				className = array[0];
			}
			
			trace("["+level+ "] "+className+" => "+message);			
		}
		
	}
}