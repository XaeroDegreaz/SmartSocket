<?php
function __autoload($class_name) {
	
	//# First, let's check and see if the file is a core server file:
	if(file_exists("CORE/$class_name.php")) {
		require_once "CORE/$class_name.php";
		return true;
	}
	
	//# Now, let's check to see if the file is an extension:
	if(file_exists("DIST/Extensions/$class_name/$class_name.php")) {
		require_once "DIST/Extensions/$class_name/$class_name.php";
		return true;
	}
	
	//# Now, we check the Etc dir to see if the file exists there:
	if(file_exists("CORE/ETC/$class_name/$class_name.php")) {
		require_once "CORE/ETC/$class_name/$class_name.php";
		return true;
	}
	
	//# THIS IS FOR THE BUILD VERSION
	//# Now, let's check to see if the file is an extension:
	if(file_exists("Extensions/$class_name/$class_name.php")) {
		require_once "Extensions/$class_name/$class_name.php";
		return true;
	}
	
	//# Now, we check the Etc dir to see if the file exists there:
	if(file_exists("Etc/$class_name/$class_name.php")) {
		require_once "Etc/$class_name/$class_name.php";
		return true;
	}
	
	Logger::log("__autoload", "The class file for $class_name could not be found!\n", true);
}
?>