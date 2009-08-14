<?php
/**
 * Simple class that loads some config information from the Config.xml of the main SmartSocket program.
 * It will be more complete and comprehensive later on.
 * @author Jerome Doby
 * @package Core
 */
final class Loader {
	
	public $ip;
	public $port;
	public $xml;
	
	/**
	 * All this does is set up some basic configuration information.
	 * More to come in the future.
	 * @return boolean
	 */
	public function Loader() {
		
		Logger::log(__CLASS__, "Loader class initialized.");
		
		$file = "Config.xml";
		if($this->xml = simplexml_load_file($file)) {
			
			$this->ip = (string)$this->xml->ip;
			Logger::log(__CLASS__, "Server IP: $this->ip.");
		
			$this->port = (integer)$this->xml->port;
			Logger::log(__CLASS__, "Server Port: $this->port.");
			
		}else {
			Logger::log(__CLASS__, "Could not load the configuration file ($file).", true);
		}
				
		return true;		
	}
	
}
?>