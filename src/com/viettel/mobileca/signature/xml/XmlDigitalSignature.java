/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.mobileca.signature.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignatureProperties;
import javax.xml.crypto.dsig.SignatureProperty;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.IOUtils;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

    //HoangTD added 14/05/2016
    public byte[] createDigestSDS(String src, String tempFile, Certificate[] chain) throws Exception {

        DocumentBuilderFactory dbFactory
                = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        Document doc = dbFactory.newDocumentBuilder().parse(src);
        return createDigestSDS(doc, tempFile, (X509Certificate)chain[0]);
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

    //HoangTD added 14/05/2016
    public static String PackageObjectIdentifier = "idPackageObject";
    public static String SignatureTimeIdentifier = "idSignatureTime";
    public static String SignatureTimeElementName = "SignatureTime";
    public static String SignatureTimeFormatElementName = "Format";
    public static String SignatureTimeValueElementName = "Value";
    public static String DefaultSignatureId = "sigid";
    public static String SignaturePropertiesId = "proid";

    private byte[] createDigestSDS(Document doc, String tempFile, X509Certificate cert) throws Exception {
        String tagname = "CKYDTU_DVI";
        String xlsContent = "<xsl:stylesheet version=\"1.0\"\r\n"
                + "xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n"
                + "<xsl:output method=\"xml\" omit-xml-declaration=\"yes\"/>\r\n"
                + "<xsl:strip-space elements=\"*\"/>\r\n" + "<xsl:template match=\"@*|node()\">\r\n" + "<xsl:copy>\r\n"
                + "<xsl:apply-templates select=\"@*|node()\"/>\r\n" + "</xsl:copy>\r\n" + "</xsl:template>\r\n"
                + "</xsl:stylesheet>";
        StreamSource xlsStreamSource = new StreamSource(new StringReader(xlsContent));
        Transformer trans = TransformerFactory.newInstance().newTransformer(xlsStreamSource);
        DOMResult result = new DOMResult();

        trans.transform(new DOMSource(doc), result);
        doc = (Document) result.getNode();

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

        // Thuc hien kieu ky XML Enveloped
        ArrayList refLs = new ArrayList();
        Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA1, null),
                Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null,
                null);
        // Reference refIdPackageObject = fac.newReference("#"
        // + PackageObjectIdentifier, fac.newDigestMethod(DigestMethod.SHA1,
        // null), null,
        // "http://www.w3.org/2000/09/xmldsig#Object", null);
        refLs.add(ref);
        // refLs.add(refIdPackageObject);
        SignedInfo signedInfo = fac.newSignedInfo(
                fac.newCanonicalizationMethod("http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
                        (C14NMethodParameterSpec) null),
                fac.newSignatureMethod(SignatureMethod.RSA_SHA1, null), refLs, DefaultSignatureId);

        KeyInfoFactory keyInfoFactory = fac.getKeyInfoFactory();
        KeyValue keyValue = keyInfoFactory.newKeyValue(cert.getPublicKey());
        List ls = new ArrayList();
        // ls.add(certificateInfo.getSubject());
        ls.add(cert);
        X509Data x509d = keyInfoFactory.newX509Data(ls);
        List<XMLStructure> keyInfoContents = new ArrayList<XMLStructure>();
        keyInfoContents.add(keyValue);
        keyInfoContents.add(x509d);
        SignatureProperties pros = createSignatureProperties(fac, DefaultSignatureId, doc);
        List<XMLStructure> idPackageObjectContent = new ArrayList<XMLStructure>();

        idPackageObjectContent.add(pros);

        // create idpackageobject xml object
        XMLObject idPackageObject = fac.newXMLObject(idPackageObjectContent, PackageObjectIdentifier, null, null);

        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoContents);
        // XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo);
        XMLSignature signature = fac.newXMLSignature(signedInfo, keyInfo, Collections.singletonList(idPackageObject),
                DefaultSignatureId, null);
        Node root = doc.getElementsByTagName(tagname).item(0);
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        KeyPair kp = kpg.generateKeyPair();
        DOMSignContext dsc = new DOMSignContext(kp.getPrivate(), root);
        signature.sign(dsc);
        // byte[] hash =
        // DigestUtils.sha1(signature.getSignedInfo().getCanonicalizedData());
        // byte[] extSignature =
        // sign(IOUtils.toByteArray(signature.getSignedInfo().getCanonicalizedData()),
        // pk);
        // String signatureXML = new BASE64Encoder().encode(extSignature);
        // Node signatureValue =
        // doc.getElementsByTagName("SignatureValue").item(0);
        // signatureValue.setTextContent(signatureXML);
        byte[] digest = IOUtils.toByteArray(signature.getSignedInfo().getCanonicalizedData());
        StreamResult res = new StreamResult(new FileOutputStream(tempFile));
        trans.transform(new DOMSource(doc), res);
        return digest;

    }

    public static final String CONTENT_TYPES = "http://schemas.openxmlformats.org/package/2006/content-types";
    public static final String CORE_PROPERTIES = "http://schemas.openxmlformats.org/package/2006/metadata/core-properties";
    public static final String DIGITAL_SIGNATURE = "http://schemas.openxmlformats.org/package/2006/digital-signature";
    public static final String RELATIONSHIPS = "http://schemas.openxmlformats.org/package/2006/relationships";
    public static final String MARKUP_COMPATIBILITY = "http://schemas.openxmlformats.org/markup-compatibility/2006";

    private static SignatureProperties createSignatureProperties(XMLSignatureFactory fac, String pSignatureId,
            org.w3c.dom.Document pSignatureDoc) {

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Date signingTime = now.getTime();

        String signatureDateTimeFormatString = "yyyy-MM-dd'T'HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(signatureDateTimeFormatString);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        String dateFriendly = sdf.format(signingTime);

        // set as date format as MS office produces it ?
        // TODO : needs and cries for better handling
        dateFriendly = dateFriendly + "Z";
        signatureDateTimeFormatString = "YYYY-MM-DDThh:mm:ssTZD";

        // signature time
        Element signDateTimeElement = pSignatureDoc.createElementNS(DIGITAL_SIGNATURE, SignatureTimeElementName);
        signDateTimeElement.setPrefix("mdssi");
        // explicitly add namespace so it is not omitted during c18n
        signDateTimeElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", DIGITAL_SIGNATURE);
        // format node
        Element signDateTimeFormat = pSignatureDoc.createElementNS(DIGITAL_SIGNATURE, SignatureTimeFormatElementName);
        signDateTimeFormat.setPrefix("mdssi");
        // explicitly add namespace so it is not omitted during c18n
        signDateTimeFormat.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", DIGITAL_SIGNATURE);
        signDateTimeFormat.appendChild(pSignatureDoc.createTextNode(signatureDateTimeFormatString));

        // value node
        Element signDateTimeValue = pSignatureDoc.createElementNS(DIGITAL_SIGNATURE, SignatureTimeValueElementName);
        signDateTimeValue.setPrefix("mdssi");
        // explicitly add namespace so it is not omitted during c18n
        signDateTimeValue.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:mdssi", DIGITAL_SIGNATURE);
        signDateTimeValue.appendChild(pSignatureDoc.createTextNode(dateFriendly));

        signDateTimeElement.appendChild(signDateTimeFormat);
        signDateTimeElement.appendChild(signDateTimeValue);

        List<DOMStructure> signaturePropertiesElems = new ArrayList<DOMStructure>();
        signaturePropertiesElems.add(new DOMStructure(signDateTimeElement));
        SignatureProperty signatureProperty = fac.newSignatureProperty(signaturePropertiesElems, "#" + pSignatureId,
                SignatureTimeIdentifier);

        SignatureProperties signatureProperties = fac
                .newSignatureProperties(Collections.singletonList(signatureProperty), SignaturePropertiesId);
        return signatureProperties;
    }
}
