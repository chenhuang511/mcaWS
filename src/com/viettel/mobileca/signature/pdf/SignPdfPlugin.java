package com.viettel.mobileca.signature.pdf;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author chungnv14
 */
public class SignPdfPlugin extends SignFilePlugin {

    private String tmpFile;
    private Date signDate;
    private byte[] hash;
    private Certificate[] chain;
    private final Logger logger = Logger.getLogger(SignPdfPlugin.class);

    @Override
    public String createHash(String filePath, Certificate[] chain) throws Exception {
//        org.apache.xml.security.Init.init();
        PDFServerClientSignature pdfSig = new PDFServerClientSignature();
        File tempFile = File.createTempFile("temp", ".pdf");
        Date signDate = new Date();
        List<byte[]> lstHash = pdfSig.createHash(filePath, tempFile.getAbsolutePath(), chain, "Nnt Ky", "Viet Nam", signDate);
        logger.info("Path Temp:" + tempFile.getAbsolutePath());
        this.tmpFile = tempFile.getAbsolutePath();
        this.signDate = signDate;
        this.hash = lstHash.get(1);
        this.chain = chain;
        return new String(Base64.encode(encodeData(lstHash.get(0), "SHA1")));
    }

    @Override
    public void insertSignature(String extSig, String destFile) throws Exception {
        PDFServerClientSignature pdfSig = new PDFServerClientSignature();
        pdfSig.insertSignature(tmpFile, destFile, "nntSignField", hash, Base64.decode(extSig), chain, signDate);
        (new File(tmpFile)).delete();
    }

    private byte[] encodeData(byte[] orginalData, String algorithm) throws Exception {
        return MessageDigest.getInstance(algorithm).digest(orginalData);
    }
}
