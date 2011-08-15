package net.smartsocket.demos.whiteboard {
	import flash.display.*;
	import flash.display.MovieClip;
	import flash.events.*;
	import flash.net.*;
	
	import net.smartsocket.SmartSocketClient;
	import net.smartsocket.protocols.json.ServerCall;
	
	public class Canvas extends MovieClip {
		
		public static var playerCanvases:Array = new Array();
		public static var playerCursors:Array = new Array();
		public static var playerIsDrawing:Array = new Array();
		private static var _instance:Canvas;
		
		public function Canvas()
		{
			super();
			_instance = this;
			this.mouseEnabled = false;
			this.mouseChildren = false;
			SmartSocketClient.customListeners.push(this);
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown);
			addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			addEventListener(MouseEvent.CLICK, onMouseUp);
		}
		
		public static function get instance():Canvas {
			return _instance;
		}
		
		private function onMouseDown(e:MouseEvent):void {
			var call:ServerCall = new ServerCall("mouseDown");
			call.put("x", this.mouseX);
			call.put("y", this.mouseY);
			
			WhiteboardClient.instance.send(call);
		}
		
		private function onMouseUp(e:MouseEvent):void {
			var call:ServerCall = new ServerCall("mouseUp");
			call.put("x", this.mouseX);
			call.put("y", this.mouseY);
			
			WhiteboardClient.instance.send(call);
		}
		
		private function onMouseMove(e:MouseEvent):void {
			var call:ServerCall = new ServerCall("mouseMove");
			call.put("x", this.mouseX);
			call.put("y", this.mouseY);
			
			WhiteboardClient.instance.send(call, true);
		}
		
		//# These are all methods that are being called *directly* from the server.
		public function onUserJoin(o:Object):void {
			if(o.username == WhiteboardClient.myUsername) {
				return;
			}
			
			var c:MovieClip = playerCanvases[o.username] = new MovieClip();
			var p:PlayerCursor = playerCursors[o.username] = new PlayerCursor();
			playerIsDrawing[o.username] = false;
			
			addChild(c);
			addChild(p);
		}
		
		public function onUserLeave(o:Object):void {
			removeChild(playerCanvases[o.username]);
			removeChild(playerCursors[o.username]);
			
			playerIsDrawing[o.username] = null;			
			playerCanvases[o.username] = null;
			playerCursors[o.username] = null;
		}
		
		public function onPlayerCanvases(o:Object) {
			for (var i:String in o) {
				if( i != "m" ) {
					var playerObject:Object = o[i];
					//trace
					if(playerObject.username == WhiteboardClient.myUsername) {
						continue;
					}
					
					//# Create blank canvas for them, and move cursor to last known position
					var c:MovieClip = playerCanvases[playerObject.username] = new MovieClip();
					var p:PlayerCursor = playerCursors[playerObject.username] = new PlayerCursor();
					trace("Already playing: "+playerObject.username+" > "+[playerObject.mouseX, playerObject.mouseY]);
					p.x = playerObject.mouseX;
					p.y = playerObject.mouseY;
					
					//# TODO add routine for drawing their previous canvas (must create draw function first)
					
					addChild(c);
					addChild(p);					
				}
				
			}
			
		}
		
		public function onMove(o:Object):void {
			trace("?!");
			playerCursors[o.username].x = o.x;
			playerCursors[o.username].y = o.y;
		}
		
		public function onDraw(o:Object):void {
			
			
			with(MovieClip(playerCanvases[o.username]).graphics) {
				if(!playerIsDrawing[o.username]) {
					playerIsDrawing[o.username] = true
					moveTo(o.x, o.y);
				}
				
				lineStyle(1, 0x000000);
				lineTo(o.x, o.y);
			}
			
		}
		
		public function onRelease(o:Object):void {
			playerIsDrawing[o.username] = false;
		}
		
	}
}