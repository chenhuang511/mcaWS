/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.mobileca.signature.ooxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import be.fedict.eid.applet.service.signer.TemporaryDataStorage;

/**
 * In memory temporary data store, for signing process.
 * @author quanghx2@viettel.com.vn
 * @since 25-10-2010
 * @version 1.0
 */
class OoxmlTemporaryDataStorage implements TemporaryDataStorage {
    /**
     * Out put stream of signed file
     */
    private ByteArrayOutputStream outputStream;
    /**
     * Mapping
     */
    private Map<String, Serializable> attributes;

    public OoxmlTemporaryDataStorage() {
        this.outputStream = new ByteArrayOutputStream();
        this.attributes = new HashMap<String, Serializable>();
    }

    @Override
    public InputStream getTempInputStream() {
        byte[] data = this.outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        return inputStream;
    }

    @Override
    public OutputStream getTempOutputStream() {
        return this.outputStream;
    }

    @Override
    public Serializable getAttribute(String attributeName) {
        return this.attributes.get(attributeName);
    }

    @Override
    public void setAttribute(String attributeName, Serializable attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }
}