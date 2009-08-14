<?php
/**
 * Basic console logging class. More to come in the future.
 * @author Jerome Doby
 * @package Etc
 *
 */
final class Logger {
	/**
	 * Basic printing to screen method. It can surely be improved upon, but not on the top of my priorities.
	 * @param $class string "String name of the class or function that should be noted in the console"
	 * @param $message "Desired message"
	 * @param $die boolean "Causes the server to halt. Good for if you want to kill the server and leave your own error message before php dies or exits on a fatal note."
	 * @return unknown_type
	 */
	static public function log($class, $message, $die=false) {
		//# Create an infinite loop after printing the message. This halts the server.
		if($die) {
			print("# $class (FATAL)\t: $message\n");
			while(true){};
			exit();
		}
		
		//# Get rid of line breaks in the message so they don't mess up our log output. Instead make them visible.
		$message = eregi_replace("\r", "\\r", eregi_replace("\n", "\\n", eregi_replace("\r\n", "\\r\\n", $message)));
		print("# $class\t: ".trim($message)."\n");
	}
}
?>