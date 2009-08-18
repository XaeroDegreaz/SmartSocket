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
			
			foreach($this->xml->constants->children() as $constant) {
				echo($constant);
				define($constant->getName(), $constant);
			}
			
		}else {
			Logger::log(__CLASS__, "Could not load the configuration file ($file).", true);
		}
				
		return true;		
	}
	
}
?>