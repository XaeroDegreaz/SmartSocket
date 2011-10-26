package net.smartsocket.smartlobby {
	import net.smartsocket.SmartSocketClient;
	import net.smartsocket.protocols.json.RemoteCall;

	public class Room {
		
		public var userList:Array = new Array();
		public var name:String = "";
		/**
		 * This field is only used during the construction of the Room object
		 * before sending it to the server for creation -- no Room object will
		 * ever hold a password value wne being sent from the server.
		 */		
		public var password:String = "";
		public var maxUsers:int = 10;
		public var currentUsers:int;
		public var roomID:int;
		public var isPrivate:Boolean;
		public var isAcceptingNewJoiners:Boolean;
		public var owner:User;
		public var customData:Object = {};
		
		public function Room(object:Object = null) {
			for (var i in object) {
				try {
					if(i == "owner") {
						owner = new User(object[i]);
					}else {
						this[i] = object[i];
					}
				}catch(e:Error) {
					trace("Room does not have the property ["+i+"]");
				}
			}
		}
		
		public function getUserList():void {
			var call:RemoteCall = new RemoteCall("getUserList");
			call.slDataListener = "room";
			call.roomName = name;
			call.test = "test";
			
			SmartSocketClient.send( call );
		}
		
	}
}