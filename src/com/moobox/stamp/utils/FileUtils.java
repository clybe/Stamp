package com.moobox.stamp.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Environment;

public class FileUtils {
	
	static final String SDPath = "/stamp"; // Pic Cache dir
	static final String PicName = "temp.png"; 

	public static String InputStreamToByte(InputStream is) throws IOException {
		ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
		byte[] buffer = new byte[2048];
		int len;
		while ((len = is.read(buffer)) > 0) {
			bytestream.write(buffer, 0, len);
		}
		byte imgdata[] = bytestream.toByteArray();
		bytestream.close();
		return new String(imgdata);
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
	
	public static File getCacheFolder() {
		File file = new File(Environment.getExternalStorageDirectory()
				+ SDPath);
		if (!file.exists()) {
			file.mkdir();
		}
		return file;
	}

	public static String getAPKPath() {
		return (Environment.getExternalStorageDirectory() + SDPath + "/" + PicName);
	}

	public static File getPicFile() throws IOException {
		File folder = getCacheFolder();
		File apkFile = new File(folder, PicName);
		apkFile.createNewFile();
		return apkFile;
	}

}
