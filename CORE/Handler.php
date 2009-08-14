<?php
/**
 * The Handler class handles data sent from the client
 * @author Jerome Doby
 * @package Core 
 */
final class Handler {
	//# Used to hold the main server object
	protected $s;
	
	/**
	 * Constructor
	 * @param $s "Server class object."
	 * @return unknown_type
	 */
	public function Handler($s) {
		Logger::log(__CLASS__, "Handler class initialized.");
		
		if($s instanceof Server) {
			$this->s = $s;
		}else {
			Logger::log(__CLASS__, "Construct handler is not an instance of a Server class object.", true);
			exit();
		}

	}
	
	/**
	 * Takes in the data that the client is sending to the server and checks it for worthiness, then routes it to the extension.
	 * @param $socket resource
	 * @param $raw_data string
	 * @param $master array
	 * @return boolean
	 */
	public function onReceive(&$socket, $raw_data, &$master) {
		
		//# Let's check if the useZlib node of the Config.xml is set to true
		if((string)$this->s->Loader->xml->useZlib == "true") {
			//# We are supposed to use zlib here, so let's check if the data is zlib or not.
			if(!$raw_data = gzuncompress($raw_data)) {
				Logger::log(__CLASS__, "Invalid GZip/Zlib received from user. Security risk HIGH, disconnecting.");
				//# Let's close out the client. If you are sure you are using zlib, this could disconnect a hacker.
				$this->onDisconnect($socket, $master);
				
				return false;
			}
		}

		//# We now send this data to the extension's onReceive method along with the resource of client that sent it		
		$this->s->extension->onReceive($socket, $raw_data);
		return true;
	}
	
	/**
	 * This will perform core disconnect functions so as not to bother the user with it.
	 * However, it will fire up the extension's onDisconnect method and if the extension's
	 * author has any additional disconnect logic they would like to perform it will be done.
	 * @param $socket resource
	 * @param $master array
	 * @return boolean
	 */
	public function onDisconnect(&$socket, &$master) {
		//# Let's let the extension have a chance to perform their logic before we unset some crucial client info.
		$this->s->extension->onDisconnect($socket);
		
		//# Now, we go ahead and get the target socket location and kill connection.
		$key_to_del = array_search($socket, $master, TRUE);
		fclose($socket);
			
		unset($master[$key_to_del]);
			
		Logger::log(__CLASS__, "User disconnected.");
		return true;

	}
}
?>