package net.smartsocket {
	import flash.events.*;
	import flash.utils.Timer;
	
	import net.smartsocket.protocols.json.ServerCall;
	
	
	public class TransferQueue {
		
		private static var outgoing:Vector.<ServerCall> = new Vector.<ServerCall>();
		private static var timer:Timer;
		private var caller:SmartSocketClient;
		
		public function TransferQueue($caller:SmartSocketClient, delay:int)	{
			caller = $caller;			
			timer = new Timer(delay);
			timer.addEventListener(TimerEvent.TIMER, processQueue);
			timer.start();
		}
		
		public function get length():int {
			return outgoing.length;
		}
		
		public function add(call:ServerCall):void {
			outgoing.push(call);
		}
		
		private function processQueue(e:TimerEvent):void {
			if(outgoing.length != 0) {
				var serverCall:ServerCall = outgoing.shift();
				trace("TransferQueue => Processing "+serverCall[0]);
				caller.send(serverCall, true);
			}
		}
		
	}
}