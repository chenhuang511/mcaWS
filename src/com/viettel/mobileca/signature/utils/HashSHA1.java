/**
 *
 * @(#)HashSHA1 Mar 11, 2015 11:29:33 AM Copyright 2014 Viettel ICT. All rights
 * reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.mobileca.signature.utils;

import java.security.MessageDigest;

/**
 *
 * @author minhnn10
 */
public class HashSHA1 {

    public static byte[] hash(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance(SHA);
        md.update(data);
        return md.digest();
    }
    private static final String SHA = "SHA1";
}
