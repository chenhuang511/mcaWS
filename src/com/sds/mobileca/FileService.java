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

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String fileType = getExt(fileDetail.getFileName());
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
		return s[s.length - 1];
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
