package com.moobox.stamp.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

	public static byte[] InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			bytestream.write(buffer, 0, len);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return imgdata;
	}

	public static String InputStreamToString(InputStream inputStream) {
		StringBuffer buffer = new StringBuffer();
		try {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream));
			String line = null;
			do {
				line = bufferedReader.readLine();
				if (line != null) {
					buffer.append(line.trim());
				}
			} while (line != null);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return buffer.toString();
	}
}
