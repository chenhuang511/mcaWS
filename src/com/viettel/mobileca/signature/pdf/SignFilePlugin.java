/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.mobileca.signature.pdf;

import java.io.Serializable;
import java.security.cert.Certificate;

/**
 *
 * @author chungnv14
 */
public abstract class SignFilePlugin implements Serializable {

    abstract public String createHash(String filePath, Certificate[] chain) throws Exception;

    abstract public void insertSignature(String extSig, String destFile) throws Exception;
}
