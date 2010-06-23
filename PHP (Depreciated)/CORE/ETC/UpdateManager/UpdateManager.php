<?php
function UpdateManager($continue){
	if($continue == "false") {
		Logger::log(__FUNCTION__ , "Skipping update check. Change SMARTSOCKET_AUTOUPDATE to true on Config.xml");
		return false;
	} else {
		Logger::log(__FUNCTION__ , "Checking for updates...");

		if($latest = @(int)file_get_contents("http://smartsocket.googlecode.com/svn/trunk/DIST/BUILD")) {
			//# Compare build number
			if(SMARTSOCKET_BUILD < $latest) {
				Logger::log(__FUNCTION__ , "Update found...");

				if($file = @file_get_contents("http://smartsocket.googlecode.com/svn/trunk/DIST/libsmartsocket.dll", FILE_BINARY)) {
					Logger::log(__FUNCTION__ , "Update retrieved...");
					$update = fopen("libsmartsocket.dll", "wb");
					fwrite($update, $file);
					fclose($update);
					Logger::log(__FUNCTION__ , "Update applied.");
					Logger::log(__FUNCTION__ , "To see the changes, check out http://www.smartsocket.net");
					Logger::log(__FUNCTION__ , "You must now restart SmartSocket.", true);
				}else {
					Logger::log(__FUNCTION__ , "Could not reach the latest stable build file.");
				}

			}elseif (SMARTSOCKET_BUILD > $latest) {
				Logger::log(__FUNCTION__ , "Your build (".SMARTSOCKET_BUILD.") is newer than the public release ($latest).");

			}else {
				Logger::log(__FUNCTION__ , "You already have the latest build.");

			}
		}else {
			Logger::log(__FUNCTION__ , "Could not reach the latest stable build report.");
		}
	}
}
?>