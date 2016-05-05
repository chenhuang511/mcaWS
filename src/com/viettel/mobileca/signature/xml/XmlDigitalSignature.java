/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.mobileca.signature.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.IOUtils;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author chungnv14
 */
public class XmlDigitalSignature {

    private XmlDigitalSignature() {
    }

    public static XmlDigitalSignature getInstance() {
        if (xmlSign == null) {
            xmlSign = new XmlDigitalSignature();
        }
        return xmlSign;
    }

    public byte[] createDigest(String src, String tempFile, Certificate[] chain) throws Exception {

        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder().parse(src);
        return createDigest(doc, tempFile, chain);
    }

    public byte[] createDigest(byte[] dataFile, String tempFile, Certificate[] chain) throws Exception {

        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder().parse(new ByteArrayInputStream(dataFile));
        return createDigest(doc, tempFile, chain);
    }

    private byte[] createDigest(Document doc, String tempFile, Certificate[] chain) throws Exception {

        // prepare signature factory
        String providerName = System.getProperty(
                "jsr105Provider",
                "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        final XMLSignatureFactory sigFactory
                = XMLSignatureFactory.getInstance(
                        "DOM",
                        (Provider) Class.forName(providerName).newInstance());

        Node sigParent = doc.getDocumentElement();
        String referenceURI = ""; // Empty string means whole document
        List transforms = Collections.singletonList(
                sigFactory.newTransform(
                        Transform.ENVELOPED,
                        (TransformParameterSpec) null));
        // Create a Reference to the enveloped document
        Reference ref = sigFactory.newReference(referenceURI,
                sigFactory.newDigestMethod(
                        DigestMethod.SHA1, null),
                transforms, null, null);
        // Create the SignedInfo
        SignedInfo signedInfo = sigFactory.newSignedInfo(
                sigFactory.newCanonicalizationMethod(
                        CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null),
                sigFactory.newSignatureMethod(
                        SignatureMethod.RSA_SHA1,
                        null),
                Collections.singletonList(ref));

        // Create the SignedInfo.
        KeyInfoFactory keyInfoFactory = sigFactory.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.addAll(Arrays.asList(chain));

        X509Data xd = keyInfoFactory.newX509Data(x509Content);
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));
        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair kp = kpg.generateKeyPair();
        DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), sigParent);

        // Create the XMLSignature (but don't sign it yet)
        XMLSignature signature = sigFactory.newXMLSignature(signedInfo, keyInfo);
//        // Marshal, generate (and sign) the enveloped signature
        signature.sign(dsc);
//        byte[] hash = DigestUtils.sha1(signature.getSignedInfo().getCanonicalizedData());
//            byte[] extSignature = sign(IOUtils.toByteArray(signature.getSignedInfo().getCanonicalizedData()), pk);
//            String signatureXML = new BASE64Encoder().encode(extSignature);
//            Node signatureValue = doc.getElementsByTagName("SignatureValue").item(0);
//            signatureValue.setTextContent(signatureXML);
        byte[] digest = IOUtils.toByteArray(signature.getSignedInfo().getCanonicalizedData());
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        StreamResult res = new StreamResult(new FileOutputStream(tempFile));
        trans.transform(new DOMSource(doc), res);
        return digest;

    }

    public void insertSignature(String tempFile, String dest, byte[] extSignature) throws Exception {
        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder().parse(tempFile);
        String signatureXML = Base64.encode(extSignature);
        Node signatureValue = doc.getElementsByTagName("SignatureValue").item(0);
        signatureValue.setTextContent(signatureXML);
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        FileOutputStream os = new FileOutputStream(dest);
        StreamResult res = new StreamResult(os);
        trans.transform(new DOMSource(doc), res);
        os.close();
        (new File(tempFile)).delete();
    }
    private static XmlDigitalSignature xmlSign;

}
