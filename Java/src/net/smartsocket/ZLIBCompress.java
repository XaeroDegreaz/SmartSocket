/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.smartsocket;

/**
 *
 * @author XaeroDegreaz
 */
import java.io.IOException;
import java.io.*;
import java.util.zip.*;

public class ZLIBCompress {

    // Throw exceptions to console:
    public static byte[] compress(Object data) throws IOException {
	byte[] input = data.toString().getBytes();

	Deflater compressor = new Deflater();
	compressor.setLevel(Deflater.BEST_COMPRESSION);
	compressor.setInput(input);
	compressor.finish();
	ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);

	// Compress the data
	byte[] buf = new byte[4096];
	while (!compressor.finished()) {
	    int count = compressor.deflate(buf);
	    bos.write(buf, 0, count);
	}
	try {
	    bos.close();
	} catch (IOException e) {
	}

	//save the compressed data into data.db file
	//OutputStream outputStream = new FileOutputStream("data.db");
	//bos.writeTo(outputStream);

	// Get the compressed data
	byte[] compressedData = bos.toByteArray();

	return compressedData;
    }

    public static String decompress(byte[] data) throws Exception {
	String uncompressedData = null;

	// Decompress the bytes
	Inflater decompresser = new Inflater();
	decompresser.setInput(data, 0, data.length);

	byte[] result = new byte[4096];
	int resultLength = decompresser.inflate(result);

	decompresser.end();
	
	uncompressedData = new String(result, 0, resultLength, "UTF-8");

	return uncompressedData;
    }
}
