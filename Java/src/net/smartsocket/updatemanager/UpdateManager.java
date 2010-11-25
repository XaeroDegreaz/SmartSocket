/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.updatemanager;

import java.io.*;
import java.net.*;
import net.smartsocket.*;

import net.smartsocket.SaveUpdateFile;

/**
 *
 * @author XaeroDegreaz
 */
public class UpdateManager {

    public Boolean updateAvailable = false;

    public UpdateManager(Boolean updateNow) {
        
        if(Loader._constants.get("AUTO_UPDATE").equals(false)) {
            return;
        }

	Logger.log("Updater", "Checking for updates...");
	try {
	    URL remoteChecksumFile = new URL("http://github.com/XaeroDegreaz/SmartSocket/raw/master/Java/Update%20Checksum.md5");
	    URL localChecksumFile = getClass().getResource("/net/smartsocket/resources/Update Checksum.md5");

	    InputStream remoteInput = remoteChecksumFile.openStream();
	    InputStream localInput = localChecksumFile.openStream();

	    BufferedReader remoteReader = new BufferedReader(new InputStreamReader(remoteInput));
	    BufferedReader localReader = new BufferedReader(new InputStreamReader(localInput));

	    String remoteChecksum = remoteReader.readLine().trim();
	    String localChecksum = localReader.readLine().trim();

	    if (remoteChecksum.equals(localChecksum)) {
		System.out.println("Versions are the same; no update needed.");
		Logger.log("Updater", "Your extension is using the latest SmartSocket library.");

	    } else {
		System.out.println("Versions are different; update available.");
		updateAvailable = true;
		beginUpdate();
	    }

	    System.out.println("Local: " + localChecksum + " Remote: " + remoteChecksum);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void beginUpdate() {
	Logger.log("Updater", "New version of SmartSocket is available.");

	try {

	    javax.swing.JOptionPane optionPane = new javax.swing.JOptionPane();
	    int retval = optionPane.showConfirmDialog(
		    Main.getApplication().getMainFrame(),
		    "The SmartSocket library has been updated.\n"
		    + "If you download the updates, they will be available\n"
		    + "the next time you compile your extension.\n"
		    + "Do you want to download them now?",
		    "SmartSocket Updates",
		    optionPane.YES_NO_OPTION);

	    if (retval == optionPane.YES_OPTION) {

		SaveUpdateFile saveDialog = new SaveUpdateFile(Main.getApplication().getMainFrame(), true);
		retval = saveDialog.chooser.showSaveDialog(Main.getApplication().getMainFrame());

		if (retval == saveDialog.chooser.APPROVE_OPTION) {

		    //# Gather information on the file.
		    Logger.log("Updater", "Downloading latest build...");
		    URL url = new URL("http://github.com/XaeroDegreaz/SmartSocket/raw/master/Java/SmartSocket.jar");
		    URLConnection connection = url.openConnection();
		    InputStream raw = connection.getInputStream(), in = new BufferedInputStream(raw);
		    int bytesRead = 0, offset = 0, contentLength = connection.getContentLength();
		    byte[] data = new byte[contentLength];

		    while (offset < contentLength) {
			bytesRead = in.read(data, offset, data.length - offset);
			if (bytesRead == -1) {
			    break;
			}
			offset += bytesRead;
		    }

		    in.close();

		    FileOutputStream out = new FileOutputStream(saveDialog.chooser.getSelectedFile().getAbsolutePath());
		    
		    out.write(data);
		    out.flush();
		    out.close();
		    Logger.log("Updater", "Update complete. Updates will take effect next time you compile your server extension.");
		}
	    }

	} catch (Exception e) {
	    Logger.log("Updater", "There was an error downloading the update. Please try again later.\nAlternatively, you can manually download it from the repository on GitHub.");
	}
    }
}