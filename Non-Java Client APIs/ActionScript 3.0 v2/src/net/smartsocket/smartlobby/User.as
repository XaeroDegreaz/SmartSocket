package net.smartsocket.smartlobby {
	public class User {
		
		public var username:String;
		public var userID:int;
		public var friendsList;
		public var room:Room;
		
		public function User(object:Object) {			
			for (var i in object) {
				try {
					this[i] = object[i];
				}catch(e:Error) {
					trace("User does not have the property ["+i+"]");
				}
			}			
		}
	}
}