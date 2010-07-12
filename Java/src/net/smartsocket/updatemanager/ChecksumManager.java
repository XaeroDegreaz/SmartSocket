/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket.updatemanager;

import java.security.*;

import java.io.*;

public class ChecksumManager {

    public static byte[] createChecksum(String filename) throws
	    Exception {
	InputStream fis = new FileInputStream(filename);

	byte[] buffer = new byte[1024];
	MessageDigest complete = MessageDigest.getInstance("MD5");
	int numRead;
	do {
	    numRead = fis.read(buffer);
	    if (numRead > 0) {
		complete.update(buffer, 0, numRead);
	    }
	} while (numRead != -1);
	fis.close();
	return complete.digest();
    }

    // see this How-to for a faster way to convert
    // a byte array to a HEX string
    public static String getMD5Checksum(String filename) throws Exception {
	byte[] b = createChecksum(filename);
	String result = "";
	for (int i = 0; i < b.length; i++) {
	    result +=
		    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
	}
	return result;
    }

    public static void main(String args[]) {
	try {
	    System.out.println("Build Checksum: "+ createChecksumFile( getMD5Checksum("SmartSocket.jar") ) );
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static String createChecksumFile(String checksum) {
	try {
	    BufferedWriter writer = new BufferedWriter( new FileWriter("Update Checksum.md5") );
	    writer.write(checksum);
	    writer.close();
	} catch (Exception e) {
	}

	return checksum;
    }
}
