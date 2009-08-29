<?php
/**
 * Simple class that loads some config information from the Config.xml of the main SmartSocket program.
 * It will be more complete and comprehensive later on.
 * @author Jerome Doby
 * @package Core
 */
final class Loader {
	
	/**
	 *Loop through the main configuration file and create constants used in the server.
	 * @return boolean
	 */
	public function Loader() {
		
		Logger::log(__CLASS__, "Loading configuration data from Config.xml...");
		
		$file = "Config.xml";
		if($this->xml = simplexml_load_file($file)) {
			
			foreach($this->xml->constants->children() as $constant) {
				
				define($constant->getName(), $constant);
			}
			
		}else {
			Logger::log(__CLASS__, "Could not load the configuration file ($file).", true);
		}
				
		return true;		
	}
	
}
?>