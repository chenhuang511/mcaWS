package com.sds.mobileca;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileAction {
	
	public static String fileEncode(String filePath) {
		FileInputStream fileInputStream = null;
		File file = new File(filePath);
		byte[] bFile = new byte[(int) file.length()];
		String b64Str = null;
		try {
			// convert file into array of bytes
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bFile);
			fileInputStream.close();
			b64Str = Base64Utils.base64Encode(bFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b64Str;
	}

	public static String createFilePathByTime(String fileStatus, String fileType) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();
		return System.getProperty("catalina.base") + "/webapps/MobileCA-WS/etc/" + fileStatus + "/"
				+ dateFormat.format(date) + "." + fileType;
	}

	public static String saveFile(String b64File, String fileType) {

		String filePath = createFilePathByTime(Variables.FOLDER_FILE_RECEIVED, fileType);
		byte[] b64Decoded = Base64Utils.base64Decode(b64File);
		File file = new File(filePath);
		FileOutputStream fop;
		try {
			fop = new FileOutputStream(file);
			fop.write(b64Decoded);
			fop.flush();
			fop.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filePath;
	}

	public static void deleteFile(String filePath) {
		try {

			File file = new File(filePath);

			if (file.delete()) {
				System.out.println(file.getName() + " is deleted!");
			} else {
				System.out.println("Delete operation is failed.");
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

}
