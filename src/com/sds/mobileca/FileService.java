package com.sds.mobileca;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class FileService {
	
	public static final String TYPE_PDF = "1";
	public static final String TYPE_XLSX = "2";
	public static final String TYPE_DOCX = "3";
	public static final String TYPE_XML = "4";

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String fileType = getExt(fileDetail.getFileName());
		if(fileType == null)
			return Response.status(200).entity("Unsupported type").build();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssS");
		Date date = new Date();
		String fileSaved = dateFormat.format(date) + "." + fileType;
		String uploadedFileLocation = System.getProperty("catalina.base") + "/webapps/MobileCA-WS/etc/"
				+ Variables.FOLDER_FILE_RECEIVED + "/" + fileSaved;

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		return Response.status(200).entity(fileSaved).build();

	}

	// parse to get ext of file
	private String getExt(String fileName) {
		String[] s = fileName.split("\\.");
		String tmp = s[s.length - 1];
		tmp = tmp.toLowerCase();
		if(tmp.equals("pdf")) {
			return TYPE_PDF;
		} else if(tmp.equals("xlsx")) {
			return TYPE_XLSX;
		} else if(tmp.equals("docx")) {
			return TYPE_DOCX;
		} else if(tmp.equals("xlsx")) {
			return TYPE_XML;
		} else {
			return null;
		}
	}

	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
