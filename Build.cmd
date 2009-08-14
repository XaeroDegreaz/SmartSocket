@echo off
@title SmartSocket

"%cd%/PHP/php.exe" -q Build.php
rem 7z a -tzip SmartSocket.zip  -r -x!*.bzr -x!*.zip ./DIST/*.*
