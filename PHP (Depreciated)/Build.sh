#!/bin/sh

wine ./PHP/php.exe -q Build.php
wine ./3RDPARTY/7z.exe a -tzip SmartSocket.zip -r -x!*.bzr -x!*.zip ./DIST/*.*