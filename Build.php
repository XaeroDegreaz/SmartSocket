<?php
require_once("./CORE/ETC/__autoload.php");

$xml = simplexml_load_file("BuildConfig.xml");
$xml->SMARTSOCKET_BUILD = (int)$xml->SMARTSOCKET_BUILD+1;

$new_xml = $xml->asXML();

$file = fopen("BuildConfig.xml", "w");
fwrite($file, $new_xml);
fclose($file);

$file = fopen("./DIST/BUILD", "w");
fwrite($file, $xml->SMARTSOCKET_BUILD);
fclose($file);

define("SMARTSOCKET_BUILD", (int)$xml->SMARTSOCKET_BUILD);
define("SMARTSOCKET_VERSION", (string)$xml->SMARTSOCKET_VERSION);

/* creating the output file (SmartSocket.exe) */
$exe = fopen("./DIST/SmartSocket.exe", "w");
$core = fopen("./DIST/libsmartsocket.dll", "w");

/* 1) writing a stub (phpe.exe) */
$size = filesize("./PHP/php.exe");
$fr = fopen("./PHP/php.exe", "r");
fwrite($exe, fread($fr, $size), $size);
$startpos = ftell($exe);

/* 2) writing bytecodes */
bcompiler_write_header($exe);
bcompiler_write_header($core);

bcompiler_write_constant($core, "SMARTSOCKET_BUILD");
bcompiler_write_constant($core, "SMARTSOCKET_VERSION");
bcompiler_write_class($core, "Template");

//# Write all of the main CORE files to the executable.
bcompiler_write_class($core, "Loader");
bcompiler_write_class($core, "Handler");
bcompiler_write_class($core, "Server");

//# Write ETC classes to exe
bcompiler_write_class($core, "Logger");
bcompiler_write_class($core, "ExtensionManager");
//bcompiler_write_function($core, "UpdateManager"); //Why doesn't this work?


bcompiler_write_function($core, "__autoload");
bcompiler_write_file($core, "./CORE/ETC/UpdateManager/UpdateManager.php");
bcompiler_write_file($core, "./CORE/ETC/misc.php");

bcompiler_write_footer($core);

/* 3) writing EXE footer */
bcompiler_write_exe_footer($exe, $startpos);

/* closing the output file */
fclose($core);
fclose($exe);

copy("./DIST/libsmartsocket.dll", "../stable/libsmartsocket.dll");
echo("SmartSocket built.\n");

?>
