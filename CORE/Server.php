<?php
/**
 * The server class is responsible for loading all of the other CORE classes.
 * It is also takes care of a lot of data binding and automation of tasks.
 * @package Core
 *
 */
final class Server {
	public $extension;
	public $Loader;
	public $Handler;
	
	public $master = array();
	/**
	 * Must be called by an extension.
	 *
	 * @param string $extension "String name of the extension to load."
	 * @return Server
	 */
	public function Server($extension) {
		
		Logger::log(__CLASS__, "Server class initialized.");
		$this->extension = $extension;
		
		$this->Loader = new Loader($this);
		$this->Handler = new Handler($this);
				
	}
	
	/**
	 * Start the server itself, and send data to the Handler class.
	 *
	 * @param string $protocol "Will later incorperate UDP."
	 */
	public function Start($protocol='tcp') {
				
		$socket = stream_socket_server("$protocol://".$this->Loader->ip.":".$this->Loader->port, $errno, $errstr);
		
		if (!$socket) {
			die("$errstr ($errno)\n");
		} else {
			Logger::log(__CLASS__, "Server created, listening for incoming connections...");
			$this->master[] = $socket;
			$read = $this->master;
			
			while (true) {
				$read = $this->master;
				$mod_fd = stream_select($read, $_w = NULL, $_e = NULL, 5);
				
				if ($mod_fd === FALSE) {
					Logger::log(__CLASS__, "Stream binding failed, exiting server.");
					break;
				}
				
				for ($i = 0; $i < $mod_fd; ++$i) {
					
					if ($read[$i] === $socket) {
						//# A connection has been established...
						
						$conn = stream_socket_accept($socket);
					
						$this->master[] = $conn;
						Logger::log(__CLASS__, "New connection.");
						$this->extension->onConnect($conn);
					} else {
						$sock_data = fread($read[$i], 1024);
						//var_dump($sock_data);
						if (strlen($sock_data) === 0) { // connection closed
														
							$this->Handler->onDisconnect($read[$i], $this->master);
							
						} else if ($sock_data === FALSE) {
							Logger::log(__CLASS__, "Something bad happened while a user was connecting. Purging connection.");
							$key_to_del = array_search($read[$i], $this->master, TRUE);
							unset(self::$master[$key_to_del]);
						} else {
							$this->Handler->onReceive($read[$i], $sock_data, $this->master);
							
						}
					}
				}
			}
		}
	}
	
	/**
	 * Send the buffer to an array of clients or just a single client.
	 * @param $client_list mixed "List of sockets."
	 * @param $data string "The data packet to be sent to the client.";
	 * @return boolean
	 */
	public function Send($client_list, $data) {
		
		//# If the main configuration file is set to use zlib compression, we do it here.
		if((string)$this->Loader->xml->useZlib == "true") {
			$data = gzcompress($data);
		}
		
		//# Goes through the array one by one and sends them each the packet
		if(is_array($client_list)) {
			foreach($client_list as $client) {
				Logger::log(__CLASS__, "Sending ($data) to ($client_list)");
				fwrite($client, $data) or
				Logger::log(__CLASS__, "There was an error sending ($data) to ($client_list)");
			}
			
		}else {
			//# We are sending to only one client.
			Logger::log(__CLASS__, "Sending ($data) to ($client_list)");
			fwrite($client_list, $data) or
			Logger::log(__CLASS__, "There was an error sending ($data) to ($client_list)");
		}
		return true;
		
	}
	
}
?>