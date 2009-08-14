<?php
/**
 * This template ensures that the main methods required for use by SmartSocket are present and proper within an extension.
 * @author Jerome Doby
 * @package Core
 */
interface Template {	
	public function onConnect($socket);
	public function onReceive($socket, $raw_data);
	public function onDisconnect($socket);	
}
?>