package com.sds.mobileca;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.multipart.FormDataParam;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.util.encoders.Base64;

import com.viettel.mobileca.signature.ooxml.XMLOfficeSignature;
import com.viettel.mobileca.signature.pdf.SignFilePlugin;
import com.viettel.mobileca.signature.pdf.SignPdfPlugin;
import com.viettel.mobileca.signature.utils.APQueryCertificate;
import com.viettel.mobileca.signature.utils.APSignature;
import com.viettel.mobileca.signature.utils.HashSHA1;
import com.viettel.mobileca.signature.xml.XmlDigitalSignature;

@Path("/sign")
public class SignService {
	String certString = null;
	String vtCertString = null;
	String b64File1 = null;

	@Path("getUserCert/{phoneNumber}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String getUserCert(@PathParam("phoneNumber") String phoneNumber) throws Exception {
		String userCertString = APQueryCertificate.onProcess(phoneNumber);
		return userCertString;
	}

	@Path("/{phoneNumber}/{fileName}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String signFile(@PathParam("phoneNumber") String phoneNumber, @PathParam("fileName") String fileName)
			throws Exception {
		String fileToSign = System.getProperty("catalina.base") + "/webapps/MobileCA-WS/etc/"
				+ Variables.FOLDER_FILE_RECEIVED + "/" + fileName;

		certString = APQueryCertificate.onProcess(phoneNumber);

		vtCertString = "MIIEKDCCAxCgAwIBAgIKYQWvMwAAAAAABzANBgkqhkiG9w0BAQUFADB+MQswCQYDVQQGEwJWTjEzMDEGA1UEChMqTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIENvbW11bmljYXRpb25zMRswGQYDVQQLExJOYXRpb25hbCBDQSBDZW50ZXIxHTAbBgNVBAMTFE1JQyBOYXRpb25hbCBSb290IENBMB4XDTEwMTAyMDA3MTcwMVoXDTE1MTAyMDA3MjcwMVowOjELMAkGA1UEBhMCVk4xFjAUBgNVBAoTDVZpZXR0ZWwgR3JvdXAxEzARBgNVBAMTClZpZXR0ZWwtQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCd2dP6MvFT0bchdUYheo8Hw8JEPRzc/ngSv+JCSlZMQBXV2wA0rTFX97n2bwjOFcQQyS/x0xNXq11fU65bOs+XNiwTpZ9BgGWcPxXxFql2wgNUuDdfue47fMMjGFGWIpfne5+0Oxn323oKmNRSKd5asVuCz/Fv0eOqUduWBBl5jcWgU5iAkIb4+ZgN7iQx6mcwwSZ3/ozI9Oq4iPLNAOc7otDahckDUmfhmutUtMcuKYBUys3Nlz4LPNV9DCifc82uTvzdcXg144pHlFEJZVNbFLIHTbHiw/QhTPLHUVnVde46Jqq336RSTbXEvKEXIMiCg4K1/1tBErY1ZeHO42BNAgMBAAGjgeswgegwCwYDVR0PBAQDAgGGMBIGA1UdEwEB/wQIMAYBAf8CAQAwHQYDVR0OBBYEFIVcBEHcRwZwAhTlzC523nLeRdaFMB8GA1UdIwQYMBaAFM1iceRhvf497LJAYNOBdd06rGvGMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly9wdWJsaWMucm9vdGNhLmdvdi52bi9jcmwvbWljbnJjYS5jcmwwRwYIKwYBBQUHAQEEOzA5MDcGCCsGAQUFBzAChitodHRwOi8vcHVibGljLnJvb3RjYS5nb3Yudm4vY3J0L21pY25yY2EuY3J0MA0GCSqGSIb3DQEBBQUAA4IBAQB2/74l0LDdM4tqc1zOZqvzdYzETSB2IdOtOpStAkrIUYM4VSK8tbbmTPl0Zsowyx9mDmYwmMLuNoju75vwHjYldcUiE2xkMrCbRQpx+F1yeKe0vkWo78Xo9UlUV2LXH739I+x/D5wtHXXNmbx5fRXwztFaJFgRVOLKi5l9+4iis4wmDxI1Jq/K0yirNC/NwQlOI83g+xB/80T13M3hjY7iMA1Y7Gf/uUZztn3S3+AVL7J5W/TVHXC8Tshizvt816Re5GdQ+GMqEFV5q4ttdujbwjiYbMz1QIRlAREmxGBRi61mBmEZVdeAHF/VDT/u7hs41TjbdrWrqNIVjBI07xRX";
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate userCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(certString.getBytes())));
		X509Certificate viettelCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(vtCertString.getBytes())));
		Certificate[] chain = new Certificate[] { userCert, viettelCert };
		String fileExt = getExt(fileName);
		if (fileExt.equals("docx") || fileExt.equals("xlsx")) {
			List<X509Certificate> lstCert = new ArrayList<X509Certificate>();
			for (Certificate cert : chain) {
				X509Certificate xc = (X509Certificate) cert;
				lstCert.add(xc);
			}
			String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT, fileExt);
			XMLOfficeSignature.initial();
			byte[] hash = XMLOfficeSignature.hash(lstCert, fileToSign);
			String signature = APSignature.onProcess(new String(Base64.encode(hash)));
			byte[] signatureValue = Base64.decode(signature);
			XMLOfficeSignature.insertSignature(signatureValue, destinationFilePath, lstCert);
			String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
			return b64DestinationFile;
		} else if (fileExt.equals("pdf")) {
			SignFilePlugin sfp = new SignPdfPlugin();
			String hash = sfp.createHash(fileToSign, chain);
			String sig = APSignature.onProcess(hash);
			String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT,
					Variables.TYPE_PDF);
			sfp.insertSignature(sig, destinationFilePath);
			String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
			// FileAction.deleteFile(filePath);
			return b64DestinationFile;
		} else if (fileExt.equals("xml")) {
			String tempPath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_TEMP, Variables.TYPE_XML);
			String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT,
					Variables.TYPE_XML);
			byte[] hash = XmlDigitalSignature.getInstance().createDigest(fileToSign, tempPath, chain);
			byte[] hashSha1 = HashSHA1.hash(hash);
			String sig = APSignature.onProcess(new String(Base64.encode(hashSha1)));
			XmlDigitalSignature.getInstance().insertSignature(tempPath, destinationFilePath, Base64.decode(sig));
			String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
			return b64DestinationFile;
		} else {
			return "Unsuported file type";
		}

	}

	@Path("signpdf/{phoneNumber}/{b64File}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String signPDF(@PathParam("phoneNumber") String phoneNumber, @PathParam("fileName") String fileName)
			throws Exception {
		String fileToSign = System.getProperty("catalina.base") + "/webapps/MobileCA-WS/etc/"
				+ Variables.FOLDER_FILE_RECEIVED + "/" + fileName;

		certString = APQueryCertificate.onProcess(phoneNumber);

		vtCertString = "MIIEKDCCAxCgAwIBAgIKYQWvMwAAAAAABzANBgkqhkiG9w0BAQUFADB+MQswCQYDVQQGEwJWTjEzMDEGA1UEChMqTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIENvbW11bmljYXRpb25zMRswGQYDVQQLExJOYXRpb25hbCBDQSBDZW50ZXIxHTAbBgNVBAMTFE1JQyBOYXRpb25hbCBSb290IENBMB4XDTEwMTAyMDA3MTcwMVoXDTE1MTAyMDA3MjcwMVowOjELMAkGA1UEBhMCVk4xFjAUBgNVBAoTDVZpZXR0ZWwgR3JvdXAxEzARBgNVBAMTClZpZXR0ZWwtQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCd2dP6MvFT0bchdUYheo8Hw8JEPRzc/ngSv+JCSlZMQBXV2wA0rTFX97n2bwjOFcQQyS/x0xNXq11fU65bOs+XNiwTpZ9BgGWcPxXxFql2wgNUuDdfue47fMMjGFGWIpfne5+0Oxn323oKmNRSKd5asVuCz/Fv0eOqUduWBBl5jcWgU5iAkIb4+ZgN7iQx6mcwwSZ3/ozI9Oq4iPLNAOc7otDahckDUmfhmutUtMcuKYBUys3Nlz4LPNV9DCifc82uTvzdcXg144pHlFEJZVNbFLIHTbHiw/QhTPLHUVnVde46Jqq336RSTbXEvKEXIMiCg4K1/1tBErY1ZeHO42BNAgMBAAGjgeswgegwCwYDVR0PBAQDAgGGMBIGA1UdEwEB/wQIMAYBAf8CAQAwHQYDVR0OBBYEFIVcBEHcRwZwAhTlzC523nLeRdaFMB8GA1UdIwQYMBaAFM1iceRhvf497LJAYNOBdd06rGvGMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly9wdWJsaWMucm9vdGNhLmdvdi52bi9jcmwvbWljbnJjYS5jcmwwRwYIKwYBBQUHAQEEOzA5MDcGCCsGAQUFBzAChitodHRwOi8vcHVibGljLnJvb3RjYS5nb3Yudm4vY3J0L21pY25yY2EuY3J0MA0GCSqGSIb3DQEBBQUAA4IBAQB2/74l0LDdM4tqc1zOZqvzdYzETSB2IdOtOpStAkrIUYM4VSK8tbbmTPl0Zsowyx9mDmYwmMLuNoju75vwHjYldcUiE2xkMrCbRQpx+F1yeKe0vkWo78Xo9UlUV2LXH739I+x/D5wtHXXNmbx5fRXwztFaJFgRVOLKi5l9+4iis4wmDxI1Jq/K0yirNC/NwQlOI83g+xB/80T13M3hjY7iMA1Y7Gf/uUZztn3S3+AVL7J5W/TVHXC8Tshizvt816Re5GdQ+GMqEFV5q4ttdujbwjiYbMz1QIRlAREmxGBRi61mBmEZVdeAHF/VDT/u7hs41TjbdrWrqNIVjBI07xRX";
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate userCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(certString.getBytes())));
		X509Certificate viettelCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(vtCertString.getBytes())));
		Certificate[] chain = new Certificate[] { userCert, viettelCert };
		SignFilePlugin sfp = new SignPdfPlugin();
		String hash = sfp.createHash(fileToSign, chain);
		String sig = APSignature.onProcess(hash);
		String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT,
				Variables.TYPE_PDF);
		sfp.insertSignature(sig, destinationFilePath);
		String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
		// FileAction.deleteFile(filePath);
		return b64DestinationFile;

	}

	@Path("signxml/{phoneNumber}/{b64File}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String signXML(@PathParam("phoneNumber") String phoneNumber, @PathParam("b64File") String b64File)
			throws Exception {
		/*
		 * b64File1 = FileAction.fileEncode(System.getProperty("catalina.base"
		 * )+"/wtpwebapps/MobileCA-WS/etc/config.xml");
		 */
		certString = (String) APQueryCertificate.onProcess(phoneNumber);
		vtCertString = "MIIEKDCCAxCgAwIBAgIKYQWvMwAAAAAABzANBgkqhkiG9w0BAQUFADB+MQswCQYDVQQGEwJWTjEzMDEGA1UEChMqTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIENvbW11bmljYXRpb25zMRswGQYDVQQLExJOYXRpb25hbCBDQSBDZW50ZXIxHTAbBgNVBAMTFE1JQyBOYXRpb25hbCBSb290IENBMB4XDTEwMTAyMDA3MTcwMVoXDTE1MTAyMDA3MjcwMVowOjELMAkGA1UEBhMCVk4xFjAUBgNVBAoTDVZpZXR0ZWwgR3JvdXAxEzARBgNVBAMTClZpZXR0ZWwtQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCd2dP6MvFT0bchdUYheo8Hw8JEPRzc/ngSv+JCSlZMQBXV2wA0rTFX97n2bwjOFcQQyS/x0xNXq11fU65bOs+XNiwTpZ9BgGWcPxXxFql2wgNUuDdfue47fMMjGFGWIpfne5+0Oxn323oKmNRSKd5asVuCz/Fv0eOqUduWBBl5jcWgU5iAkIb4+ZgN7iQx6mcwwSZ3/ozI9Oq4iPLNAOc7otDahckDUmfhmutUtMcuKYBUys3Nlz4LPNV9DCifc82uTvzdcXg144pHlFEJZVNbFLIHTbHiw/QhTPLHUVnVde46Jqq336RSTbXEvKEXIMiCg4K1/1tBErY1ZeHO42BNAgMBAAGjgeswgegwCwYDVR0PBAQDAgGGMBIGA1UdEwEB/wQIMAYBAf8CAQAwHQYDVR0OBBYEFIVcBEHcRwZwAhTlzC523nLeRdaFMB8GA1UdIwQYMBaAFM1iceRhvf497LJAYNOBdd06rGvGMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly9wdWJsaWMucm9vdGNhLmdvdi52bi9jcmwvbWljbnJjYS5jcmwwRwYIKwYBBQUHAQEEOzA5MDcGCCsGAQUFBzAChitodHRwOi8vcHVibGljLnJvb3RjYS5nb3Yudm4vY3J0L21pY25yY2EuY3J0MA0GCSqGSIb3DQEBBQUAA4IBAQB2/74l0LDdM4tqc1zOZqvzdYzETSB2IdOtOpStAkrIUYM4VSK8tbbmTPl0Zsowyx9mDmYwmMLuNoju75vwHjYldcUiE2xkMrCbRQpx+F1yeKe0vkWo78Xo9UlUV2LXH739I+x/D5wtHXXNmbx5fRXwztFaJFgRVOLKi5l9+4iis4wmDxI1Jq/K0yirNC/NwQlOI83g+xB/80T13M3hjY7iMA1Y7Gf/uUZztn3S3+AVL7J5W/TVHXC8Tshizvt816Re5GdQ+GMqEFV5q4ttdujbwjiYbMz1QIRlAREmxGBRi61mBmEZVdeAHF/VDT/u7hs41TjbdrWrqNIVjBI07xRX";
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate userCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(certString.getBytes())));
		X509Certificate viettelCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(vtCertString.getBytes())));
		Certificate[] chain = new Certificate[] { userCert, viettelCert };

		String filePath = FileAction.saveFile(b64File, Variables.TYPE_XML);
		String tempPath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_TEMP, Variables.TYPE_XML);
		String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT, Variables.TYPE_XML);
		byte[] hash = XmlDigitalSignature.getInstance().createDigest(filePath, tempPath, chain);
		byte[] hashSha1 = HashSHA1.hash(hash);
		String sig = APSignature.onProcess(new String(Base64.encode(hashSha1)));
		XmlDigitalSignature.getInstance().insertSignature(tempPath, destinationFilePath, Base64.decode(sig));
		String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
		return b64DestinationFile;
	}

	/**
	 * @param phoneNumber
	 * @param b64File
	 * @param fileType
	 *            dinh dang file cua office
	 * @return
	 * @throws Exception
	 */
	@Path("signoffice/{fileType}/{phoneNumber}/{b64File}")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String signMSOffice(@PathParam("phoneNumber") String phoneNumber, @PathParam("b64File") String b64File,
			@PathParam("fileType") String fileType) throws Exception {
		b64File1 = FileAction.fileEncode(System.getProperty("catalina.base") + "/webapps/MobileCA-WS/etc/demo.docx");
		certString = (String) APQueryCertificate.onProcess(phoneNumber);
		vtCertString = "MIIEKDCCAxCgAwIBAgIKYQWvMwAAAAAABzANBgkqhkiG9w0BAQUFADB+MQswCQYDVQQGEwJWTjEzMDEGA1UEChMqTWluaXN0cnkgb2YgSW5mb3JtYXRpb24gYW5kIENvbW11bmljYXRpb25zMRswGQYDVQQLExJOYXRpb25hbCBDQSBDZW50ZXIxHTAbBgNVBAMTFE1JQyBOYXRpb25hbCBSb290IENBMB4XDTEwMTAyMDA3MTcwMVoXDTE1MTAyMDA3MjcwMVowOjELMAkGA1UEBhMCVk4xFjAUBgNVBAoTDVZpZXR0ZWwgR3JvdXAxEzARBgNVBAMTClZpZXR0ZWwtQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCd2dP6MvFT0bchdUYheo8Hw8JEPRzc/ngSv+JCSlZMQBXV2wA0rTFX97n2bwjOFcQQyS/x0xNXq11fU65bOs+XNiwTpZ9BgGWcPxXxFql2wgNUuDdfue47fMMjGFGWIpfne5+0Oxn323oKmNRSKd5asVuCz/Fv0eOqUduWBBl5jcWgU5iAkIb4+ZgN7iQx6mcwwSZ3/ozI9Oq4iPLNAOc7otDahckDUmfhmutUtMcuKYBUys3Nlz4LPNV9DCifc82uTvzdcXg144pHlFEJZVNbFLIHTbHiw/QhTPLHUVnVde46Jqq336RSTbXEvKEXIMiCg4K1/1tBErY1ZeHO42BNAgMBAAGjgeswgegwCwYDVR0PBAQDAgGGMBIGA1UdEwEB/wQIMAYBAf8CAQAwHQYDVR0OBBYEFIVcBEHcRwZwAhTlzC523nLeRdaFMB8GA1UdIwQYMBaAFM1iceRhvf497LJAYNOBdd06rGvGMDwGA1UdHwQ1MDMwMaAvoC2GK2h0dHA6Ly9wdWJsaWMucm9vdGNhLmdvdi52bi9jcmwvbWljbnJjYS5jcmwwRwYIKwYBBQUHAQEEOzA5MDcGCCsGAQUFBzAChitodHRwOi8vcHVibGljLnJvb3RjYS5nb3Yudm4vY3J0L21pY25yY2EuY3J0MA0GCSqGSIb3DQEBBQUAA4IBAQB2/74l0LDdM4tqc1zOZqvzdYzETSB2IdOtOpStAkrIUYM4VSK8tbbmTPl0Zsowyx9mDmYwmMLuNoju75vwHjYldcUiE2xkMrCbRQpx+F1yeKe0vkWo78Xo9UlUV2LXH739I+x/D5wtHXXNmbx5fRXwztFaJFgRVOLKi5l9+4iis4wmDxI1Jq/K0yirNC/NwQlOI83g+xB/80T13M3hjY7iMA1Y7Gf/uUZztn3S3+AVL7J5W/TVHXC8Tshizvt816Re5GdQ+GMqEFV5q4ttdujbwjiYbMz1QIRlAREmxGBRi61mBmEZVdeAHF/VDT/u7hs41TjbdrWrqNIVjBI07xRX";
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate userCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(certString.getBytes())));
		X509Certificate viettelCert = (X509Certificate) cf
				.generateCertificate(new ByteArrayInputStream(Base64.decode(vtCertString.getBytes())));
		Certificate[] chain = new Certificate[] { userCert, viettelCert };

		List<X509Certificate> lstCert = new ArrayList<X509Certificate>();
		for (Certificate cert : chain) {
			X509Certificate xc = (X509Certificate) cert;
			lstCert.add(xc);
		}
		String filePath = FileAction.saveFile(b64File1, fileType);
		String destinationFilePath = FileAction.createFilePathByTime(Variables.FOLDER_FILE_SENT, fileType);
		XMLOfficeSignature.initial();
		byte[] hash = XMLOfficeSignature.hash(lstCert, filePath);
		String signature = APSignature.onProcess(new String(Base64.encode(hash)));
		byte[] signatureValue = Base64.decode(signature);
		XMLOfficeSignature.insertSignature(signatureValue, destinationFilePath, lstCert);
		String b64DestinationFile = FileAction.fileEncode(destinationFilePath);
		return b64DestinationFile;
	}

	// parse to get ext of file
	private String getExt(String fileName) {
		String[] s = fileName.split("\\.");
		return s[s.length - 1];
	}
}
