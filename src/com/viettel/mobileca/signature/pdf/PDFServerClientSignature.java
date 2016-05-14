package com.viettel.mobileca.signature.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.io.RASInputStream;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.io.StreamUtil;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.ByteBuffer;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalBlankSignatureContainer;
import com.itextpdf.text.pdf.security.ExternalSignatureContainer;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ChungNV14
 */
public class PDFServerClientSignature {

    private static final String SIGN_FIELD = "nntSignField";
    private static final String HASH_ALG = "SHA1";
    private static final String CRYPT_ALG = "RSA";

    public void insertSignature(String src, String dest, String fieldName, byte[] hash, byte[] extSignature, Certificate[] chain, Date signDate) throws DocumentException, IOException, GeneralSecurityException {
        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        AcroFields af = reader.getAcroFields();
        PdfDictionary v = af.getSignatureDictionary(fieldName);
        if (v == null) {
            throw new DocumentException("No field");
        }
        if (!af.signatureCoversWholeDocument(fieldName)) {
            throw new DocumentException("Not the last signature");
        }
        PdfArray b = v.getAsArray(PdfName.BYTERANGE);
        long[] gaps = b.asLongArray();
        if (b.size() != 4 || gaps[0] != 0) {
            throw new DocumentException("Single exclusion space supported");
        }
        RandomAccessSource readerSource = reader.getSafeFile().createSourceView();
//        InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
        String hashAlgorithm = HASH_ALG;
        BouncyCastleDigest digest = new BouncyCastleDigest();
        PdfPKCS7 sgn = new PdfPKCS7(null, chain, hashAlgorithm, null, digest, false);
        sgn.setExternalDigest(extSignature, null, CRYPT_ALG);
        Calendar cal = Calendar.getInstance();
        cal.setTime(signDate);
        byte[] signedContent = sgn.getEncodedPKCS7(hash, cal, null, null, null, MakeSignature.CryptoStandard.CMS);
        int spaceAvailable = (int) (gaps[2] - gaps[1]) - 2;
        if ((spaceAvailable & 1) != 0) {
            throw new DocumentException("Gap is not a multiple of 2");
        }
        spaceAvailable /= 2;
        if (spaceAvailable < signedContent.length) {
            throw new DocumentException("Not enough space");
        }
        StreamUtil.CopyBytes(readerSource, 0, gaps[1] + 1, os);
        ByteBuffer bb = new ByteBuffer(spaceAvailable * 2);
        for (byte bi : signedContent) {
            bb.appendHex(bi);
        }
        int remain = (spaceAvailable - signedContent.length) * 2;
        for (int k = 0; k < remain; ++k) {
            bb.append((byte) 48);
        }
        bb.writeTo(os);
        StreamUtil.CopyBytes(readerSource, gaps[2] - 1, gaps[3] + 1, os);
        os.close();
        bb.close();
    }

    public List<byte[]> createHash(String src, String tempFile, Certificate[] chain,
            String reason, String location, Date signDate) throws IOException, Exception {

        emptySignature(src, tempFile, SIGN_FIELD, chain, reason, location);
        return preSign(tempFile, SIGN_FIELD, chain, signDate);
    }

    private List<byte[]> preSign(String src, String fieldName, Certificate[] chain, Date signDate) throws GeneralSecurityException, DocumentException {
        try {
            List<byte[]> result = new ArrayList();
            PdfReader reader = new PdfReader(src);
            AcroFields af = reader.getAcroFields();
            PdfDictionary v = af.getSignatureDictionary(fieldName);
            if (v == null) {
                throw new DocumentException("No field");
            }
            PdfArray b = v.getAsArray(PdfName.BYTERANGE);
            long[] gaps = b.asLongArray();
            if (b.size() != 4 || gaps[0] != 0) {
                throw new DocumentException("Single exclusion space supported");
            }
            RandomAccessSource readerSource = reader.getSafeFile().createSourceView();
            InputStream rg = new RASInputStream(new RandomAccessSourceFactory().createRanged(readerSource, gaps));
            BouncyCastleDigest digest = new BouncyCastleDigest();
            PdfPKCS7 sgn = new PdfPKCS7(null, chain, HASH_ALG, null, digest, false);
            byte[] hash = DigestAlgorithms.digest(rg, digest.getMessageDigest(HASH_ALG));
            Calendar cal = Calendar.getInstance();
            cal.setTime(signDate);
            byte[] sh = sgn.getAuthenticatedAttributeBytes(hash, cal, null, null, MakeSignature.CryptoStandard.CMS);
            result.add(sh);
            result.add(hash);
            return result;

        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    private void emptySignature(String src, String dest, String fieldname, Certificate[] chain, String reason, String location) throws IOException, DocumentException, GeneralSecurityException {
        BouncyCastleProvider providerBC = new BouncyCastleProvider();
        Security.addProvider(providerBC);
        PdfReader reader = new PdfReader(src);
        FileOutputStream os = new FileOutputStream(dest);
        PdfStamper stamper = PdfStamper.createSignature(reader, os, '\0');
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        appearance.setVisibleSignature(new Rectangle(36, 748, 144, 780), 1, fieldname);
        appearance.setCertificate(chain[0]);
        appearance.setReason(reason);
        appearance.setLocation(location);
        ExternalSignatureContainer external = new ExternalBlankSignatureContainer(PdfName.ADOBE_PPKLITE, PdfName.ADBE_PKCS7_DETACHED);
        MakeSignature.signExternalContainer(appearance, external, 8192);
    }

}
