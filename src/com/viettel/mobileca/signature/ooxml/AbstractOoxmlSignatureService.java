/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.mobileca.signature.ooxml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xml.security.utils.Constants;
import com.sun.org.apache.xpath.internal.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import be.fedict.eid.applet.service.signer.AbstractXmlSignatureService;
import be.fedict.eid.applet.service.signer.facets.KeyInfoSignatureFacet;
import be.fedict.eid.applet.service.signer.ooxml.OOXMLURIDereferencer;
import be.fedict.eid.applet.service.signer.time.ConstantLocalClock;

/**
 * Van digital signature for OOXML format
 * @author sonnt38@viettel.com.vn
 * @since 20-11-2010
 * @version 1.0
 */
public abstract class AbstractOoxmlSignatureService extends AbstractXmlSignatureService {
    

    protected AbstractOoxmlSignatureService() {
        ConstantLocalClock clock = new ConstantLocalClock();
        addSignatureFacet(new OoxmlSignatureFacet(this, clock));
        addSignatureFacet(new KeyInfoSignatureFacet(true, false, false));
        setSignatureId("idPackageSignature");
    }
    
    protected AbstractOoxmlSignatureService(Long signDate) {
        ConstantLocalClock clock = new ConstantLocalClock();
        addSignatureFacet(new OoxmlSignatureFacet(this, clock , signDate));
        addSignatureFacet(new KeyInfoSignatureFacet(true, false, false));
        setSignatureId("idPackageSignature");
    }

    @Override
    protected String getSignatureDescription() {
        return "Office OpenXML Document";
    }

    @Override
    public String getFilesDigestAlgorithm() {
        return null;
    }

    @Override
    protected final URIDereferencer getURIDereferencer() {
        URL ooxmlUrl = getOfficeOpenXMLDocumentURL();
        return new OOXMLURIDereferencer(ooxmlUrl);
    }

    @Override
    protected String getCanonicalizationMethod() {
        return CanonicalizationMethod.INCLUSIVE;
    }

    private class OOXMLSignedDocumentOutputStream extends ByteArrayOutputStream {

        @Override
        public void close() throws IOException {
            
            super.close();
            try {
                outputSignedOfficeOpenXMLDocument(this.toByteArray());
            } catch (Exception e) {
                throw new IOException("generic error: " + e.getMessage(), e);
            }
        }
    }

    /**
     * The output stream to which to write the signed Office OpenXML file.
     *
     * @return
     */
    abstract protected OutputStream getSignedOfficeOpenXMLDocumentOutputStream();
    /**
     * get the comment of signer
     *
     * @return
     */
    abstract protected String getSignComment();
    /**
     * Gives back the URL of the OOXML to be signed.
     *
     * @return
     */
    abstract protected URL getOfficeOpenXMLDocumentURL();

    private void outputSignedOfficeOpenXMLDocument(byte[] signatureData)
            throws IOException, ParserConfigurationException, SAXException,
                    TransformerException {
        
        OutputStream signedOOXMLOutputStream = getSignedOfficeOpenXMLDocumentOutputStream();
        if (null == signedOOXMLOutputStream) {
                throw new NullPointerException("signedOOXMLOutputStream is null");
        }

        String signatureZipEntryName = "_xmlsignatures/sig-"
                        + UUID.randomUUID().toString() + ".xml";
        
        
        /*
         * Copy the original OOXML content to the signed OOXML package. During
         * copying some files need to changed.
         */
        ZipOutputStream zipOutputStream = copyOOXMLContent(
                        signatureZipEntryName, signedOOXMLOutputStream);

        /*
         * Add the OOXML XML signature file to the OOXML package.
         */
        ZipEntry zipEntry = new ZipEntry(signatureZipEntryName);
        zipOutputStream.putNextEntry(zipEntry);
        IOUtils.write(signatureData, zipOutputStream);
        zipOutputStream.close();
    }

    private ZipOutputStream copyOOXMLContent(String signatureZipEntryName,
                    OutputStream signedOOXMLOutputStream) throws IOException,
                    ParserConfigurationException, SAXException,
                    TransformerException {
        ZipOutputStream zipOutputStream = new ZipOutputStream(
                        signedOOXMLOutputStream);
        ZipInputStream zipInputStream = new ZipInputStream(this
                        .getOfficeOpenXMLDocumentURL().openStream());
        ZipEntry zipEntry;
        boolean hasOriginSigsRels = false;
        while (null != (zipEntry = zipInputStream.getNextEntry())) {
            
            ZipEntry newZipEntry = new ZipEntry(zipEntry.getName());
            zipOutputStream.putNextEntry(newZipEntry);
            if ("[Content_Types].xml".equals(zipEntry.getName())) {
                Document contentTypesDocument = loadDocumentNoClose(zipInputStream);
                Element typesElement = contentTypesDocument
                                .getDocumentElement();

                /*
                 * We need to add an Override element.
                 */
                Element overrideElement = contentTypesDocument
                                .createElementNS(
                                                "http://schemas.openxmlformats.org/package/2006/content-types",
                                                "Override");
                overrideElement.setAttribute("PartName", "/"
                                + signatureZipEntryName);
                overrideElement
                    .setAttribute("ContentType",
                                    "application/vnd.openxmlformats-package.digital-signature-xmlsignature+xml");
                typesElement.appendChild(overrideElement);

                Element nsElement = contentTypesDocument.createElement("ns");
                nsElement
                    .setAttributeNS(Constants.NamespaceSpecNS, "xmlns:tns",
                                    "http://schemas.openxmlformats.org/package/2006/content-types");
                NodeList nodeList = XPathAPI.selectNodeList(
                                contentTypesDocument,
                                "/tns:Types/tns:Default[@Extension='sigs']", nsElement);
                if (0 == nodeList.getLength()) {
                        /*
                         * Add Default element for 'sigs' extension.
                         */
                        Element defaultElement = contentTypesDocument
                                        .createElementNS(
                                                        "http://schemas.openxmlformats.org/package/2006/content-types",
                                                        "Default");
                        defaultElement.setAttribute("Extension", "sigs");
                        defaultElement
                            .setAttribute("ContentType",
                                            "application/vnd.openxmlformats-package.digital-signature-origin");
                        typesElement.appendChild(defaultElement);
                }

                writeDocumentNoClosing(contentTypesDocument, zipOutputStream,
                                    false);
            } else if ("_rels/.rels".equals(zipEntry.getName())) {
                    Document relsDocument = loadDocumentNoClose(zipInputStream);

                    Element nsElement = relsDocument.createElement("ns");
                    nsElement
                                    .setAttributeNS(Constants.NamespaceSpecNS, "xmlns:tns",
                                                    "http://schemas.openxmlformats.org/package/2006/relationships");
                    NodeList nodeList = XPathAPI
                            .selectNodeList(
                                            relsDocument,
                                            "/tns:Relationships/tns:Relationship[@Target='_xmlsignatures/origin.sigs']",
                                            nsElement);
                    if (0 == nodeList.getLength()) {
                            Element relationshipElement = relsDocument
                                            .createElementNS(
                                                            "http://schemas.openxmlformats.org/package/2006/relationships",
                                                            "Relationship");
                            relationshipElement.setAttribute("Id", "rel-id-"
                                            + UUID.randomUUID().toString());
                            relationshipElement
                                    .setAttribute(
                                                    "Type",
                                                    "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/origin");
                            relationshipElement.setAttribute("Target",
                                            "_xmlsignatures/origin.sigs");

                            relsDocument.getDocumentElement().appendChild(
                                            relationshipElement);
                    }

                    writeDocumentNoClosing(relsDocument, zipOutputStream, false);
            } else if ("_xmlsignatures/_rels/origin.sigs.rels".equals(zipEntry
                            .getName())) {
                    hasOriginSigsRels = true;
                    Document originSignRelsDocument = loadDocumentNoClose(zipInputStream);

                    Element relationshipElement = originSignRelsDocument
                                    .createElementNS(
                                                    "http://schemas.openxmlformats.org"
                                                    + "/package/2006/relationships",
                                                    "Relationship");
                    String relationshipId = "rel-" + UUID.randomUUID().toString();
                    relationshipElement.setAttribute("Id", relationshipId);
                    relationshipElement
                            .setAttribute(
                                            "Type",
                                            "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature");
                    String target = FilenameUtils.getName(signatureZipEntryName);
                    
                    relationshipElement.setAttribute("Target", target);
                    originSignRelsDocument.getDocumentElement().appendChild(
                                    relationshipElement);

                    writeDocumentNoClosing(originSignRelsDocument, zipOutputStream,
                                    false);
            } else {
                    IOUtils.copy(zipInputStream, zipOutputStream);
            }
        }

        if (!hasOriginSigsRels) {
                /*
                 * Add signature relationships document.
                 */
                addOriginSigsRels(signatureZipEntryName, zipOutputStream);
                addOriginSigs(zipOutputStream);
        }

        /*
         * Return.
         */
        zipInputStream.close();
        return zipOutputStream;
    }

    private void addOriginSigs(ZipOutputStream zipOutputStream)
                    throws IOException {
        zipOutputStream
            .putNextEntry(new ZipEntry("_xmlsignatures/origin.sigs"));
    }

    private void addOriginSigsRels(String signatureZipEntryName,
                    ZipOutputStream zipOutputStream)
                    throws ParserConfigurationException, IOException,
                    TransformerException {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                            .newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory
                            .newDocumentBuilder();
            Document originSignRelsDocument = documentBuilder.newDocument();

            Element relationshipsElement = originSignRelsDocument.createElementNS(
                            "http://schemas.openxmlformats.org/package/2006/relationships",
                            "Relationships");
            relationshipsElement.setAttributeNS(Constants.NamespaceSpecNS, "xmlns",
                            "http://schemas.openxmlformats.org/package/2006/relationships");
            originSignRelsDocument.appendChild(relationshipsElement);

            Element relationshipElement = originSignRelsDocument.createElementNS(
                            "http://schemas.openxmlformats.org/package/2006/relationships",
                            "Relationship");
            String relationshipId = "rel-" + UUID.randomUUID().toString();
            relationshipElement.setAttribute("Id", relationshipId);
            relationshipElement
                    .setAttribute(
                                    "Type",
                                    "http://schemas.openxmlformats.org/package/2006/relationships/digital-signature/signature");
            String target = FilenameUtils.getName(signatureZipEntryName);
            
            relationshipElement.setAttribute("Target", target);
            relationshipsElement.appendChild(relationshipElement);

            zipOutputStream.putNextEntry(new ZipEntry(
                            "_xmlsignatures/_rels/origin.sigs.rels"));
            writeDocumentNoClosing(originSignRelsDocument, zipOutputStream, false);
    }

    @Override
    protected OutputStream getSignedDocumentOutputStream() {
            
            /*
             * Create each time a new object; we want an empty output stream to
             * start with.
             */
            OutputStream signedDocumentOutputStream = new OOXMLSignedDocumentOutputStream();
            return signedDocumentOutputStream;
    }
}
