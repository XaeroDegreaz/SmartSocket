/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.smartsocket.updatemanager;
import java.io.*;
import java.net.*;

/**
 *
 * @author XaeroDegreaz
 */
public class UpdateManager {
    public Boolean updateAvailable = false;
    
    public UpdateManager(Boolean updateNow) {
	URL remoteChecksum = null;
	BufferedReader reader = null;
	
	try {
	    remoteChecksum = new URL("http://github.com/XaeroDegreaz/SmartSocket/raw/master/Java/Update%20Checksum.md5");
	    InputStream input = remoteChecksum.openStream();
	    reader = new BufferedReader( new InputStreamReader(input) );

	    String checksum = reader.readLine().trim();

	    if( ChecksumManager.getMD5Checksum("SmartSocket.jar").equals(checksum) ) {
		System.out.println("Versions are different.");
	    }else {
		System.out.println("Versions are different.");
	    }

	    System.out.println("Local: "+ChecksumManager.getMD5Checksum("SmartSocket.jar")+" Remote: "+checksum);

	}catch(Exception e) {
	    e.printStackTrace();
	}

	if(updateNow) {
	    //# Get updates
	}else {
	    //# Tell about updates.
	}
    }

    public static void main(String[] args) {
	new UpdateManager(true);
    }

}
