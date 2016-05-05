/**
 *
 * @(#)XMLOfficeSignature Feb 24, 2015 2:30:20 PM Copyright 2014 Viettel ICT.
 * All rights reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package com.viettel.mobileca.signature.ooxml;

import be.fedict.eid.applet.service.signer.ooxml.OOXMLProvider;
import be.fedict.eid.applet.service.spi.DigestInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 *
 * @author minhnn10
 */
public class XMLOfficeSignature {

    private static OoxmlSignatureService service;
    private static DigestInfo digestInfo;
    private static boolean initialed = false;

    public static void initial() {
        if (!initialed) {
            OOXMLProvider.install();
            initialed = true;
        }
    }

    public static byte[] hash(List<X509Certificate> chain, String filePath) throws Exception {
        File file = new File(filePath);
        URL fileURL = file.toURI().toURL();
        service = new OoxmlSignatureService(fileURL, null);
        digestInfo = service.preSign(null, chain);
        return digestInfo.digestValue;

    }

    public static boolean insertSignature(byte[] signature, String destFile, List<X509Certificate> chain) {
        try {
            File outFile = new File(destFile);
            FileOutputStream os = new FileOutputStream(outFile);
            service.postSign(signature, chain);
            byte[] signedOOXMLData = service.getSignedOfficeOpenXMLDocumentData();
            os.write(signedOOXMLData);
            os.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
