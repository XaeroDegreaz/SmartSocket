<?php
Class Example implements Template{
	protected $s;
	
	public function Example() {
		Logger::log(__CLASS__, "Extension loaded.");
		
		//# Let's try and locate a configuration file. This is useful for making your own config for your extension.
		if(@file_exists("Extensions/Example/Config.xml")) {
			$this->Config = simplexml_load_file("Extensions/Example/Config.xml");
		}else {
			Logger::log(__CLASS__, "No extension config.xml file detected. Skipping...");
		}		
		
		//# Let's create a new server instance.
		$this->s = new Server($this);
		
		//# Let's start the show.
		$this->s->Start();

	}

	public function onConnect($socket) {
		Logger::log(__CLASS__, "The extension has received a connection.");
		
		//# Let's just send them some simple handshake data to test and see if they are able to receive.
		$this->s->Send($socket, "<handshake status='1' time='".time()."' />");
	}
	
	public function onReceive($socket, $raw_data) {

		Logger::log(__CLASS__, "Data Received: $socket, ".$raw_data);
		
		//# We can send it back to the client like this
		$this->s->Send($socket, $raw_data);
		
		//# Let's write the received data fo a file for loging purposes.
		$file = fopen("ProtocolLog.txt", "a");
		fwrite($file, $raw_data);

	}
	
	public function onDisconnect($socket) {
			Logger::log(__CLASS__, "User departed...");
			
			//# If you want some other disconnect logic here, you can create it.
	}
}
?>