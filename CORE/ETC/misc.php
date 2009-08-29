<?php

function Start() {
	//# Load the Config.xml file.
	$smartsocket_config = new Loader(); 
	echo("*********************************************
*                                           *
* SmartSocket: Extensible PHP Socket Server *
* http://www.SmartSocket.net                *
* @Author: Jerome Doby                      *
* @Email: Jerome@SmartSocket.net            *
*                                           *
*********************************************
v".SMARTSOCKET_VERSION." Build ".SMARTSOCKET_BUILD."
---
\n\n");
	
	//# We check for auto updates here. I will add more to this later on.
	UpdateManager(SMARTSOCKET_AUTOUPDATE);
	//# Now we launch the extension manager so that we can load a desired extension and begin the server process.
	$em = new ExtensionManager();
}

function safe($value){
	
	if ( get_magic_quotes_gpc() ){
		$value = stripslashes($value);
	}

	$value = mysql_real_escape_string($value);
	return $value;
}

?>