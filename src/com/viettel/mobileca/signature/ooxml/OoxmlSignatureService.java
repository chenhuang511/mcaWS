/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.mobileca.signature.ooxml;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Van signing service. Wrapper around input and output data in signing/verifying process
 * @author quanghx2@viettel.com.vn
 * @since 21-10-2010
 * @version 1.0
 */
class OoxmlSignatureService extends AbstractOoxmlSignatureService {

    /**
     * File url
     */
    private final URL ooxmlUrl;
    /**
     * data storage
     */
    private final OoxmlTemporaryDataStorage temporaryDataStorage;
    /**
     * signed output stream
     */
    private final ByteArrayOutputStream signedOOXMLOutputStream;
    /**
     * signing comment
     */
    private final String comment;

    public OoxmlSignatureService(URL ooxmlUrl, String comment) {
        this.temporaryDataStorage = new OoxmlTemporaryDataStorage();
        this.signedOOXMLOutputStream = new ByteArrayOutputStream();
        this.ooxmlUrl = ooxmlUrl;
        this.comment = comment;
    }
    
    public OoxmlSignatureService(URL ooxmlUrl, String comment, Long signDate) {
        super(signDate);
        this.temporaryDataStorage = new OoxmlTemporaryDataStorage();
        this.signedOOXMLOutputStream = new ByteArrayOutputStream();
        this.ooxmlUrl = ooxmlUrl;
        this.comment = comment;
    }

    @Override
    protected URL getOfficeOpenXMLDocumentURL() {
        return this.ooxmlUrl;
    }

    @Override
    protected OutputStream getSignedOfficeOpenXMLDocumentOutputStream() {
        return this.signedOOXMLOutputStream;
    }

    public byte[] getSignedOfficeOpenXMLDocumentData() {
        return this.signedOOXMLOutputStream.toByteArray();
    }

    @Override
    protected OoxmlTemporaryDataStorage getTemporaryDataStorage() {
        return this.temporaryDataStorage;
    }

    @Override
    public String getSignComment() {
        return this.comment;
    }
}
