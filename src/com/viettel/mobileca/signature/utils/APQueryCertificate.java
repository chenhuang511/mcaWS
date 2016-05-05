/**
 *
 * @(#)APQueryCertificate Feb 24, 2015 2:45:33 PM Copyright 2014 Viettel ICT.
 * All rights reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to
 * license terms.
 */
package com.viettel.mobileca.signature.utils;

import com.viettel.mobileca.signature.apservice.APWs;
import com.viettel.mobileca.signature.apservice.TransactionInfo;

import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.bouncycastle.util.encoders.Base64;

/**
 *
 * @author minhnn10
 */
public class APQueryCertificate {
	
	public static String onProcess(String phoneNumber) throws Exception {

		APWs apPort = APGetPort.getPort(Constants.URL_WS);
		TransactionInfo tInfo = new TransactionInfo();
		tInfo.setApId(Constants.AP_ID);
		tInfo.setProcessCode(Constants.CODE_CERT_QUERY);
		DateFormat df = new SimpleDateFormat("yyMMddHHmmss");
		tInfo.setRequestId(Long.valueOf(df.format(new Date())));
		tInfo.setMsspId("Viettel");
		df = new SimpleDateFormat("yyyyMMddHHmmss");
		tInfo.setReqDate(df.format(new Date()));
		/*tInfo.setMsisdn(Constants.MSISDN);*/
		tInfo.setMsisdn(phoneNumber);
		tInfo.setMsgMode("SYNC");
		PrivateKey key = MacUtils.getPrivateKeyFromString(Constants.PRIVATE_KEY);
		String dataEncrypt = MacUtils.getDataEncrypt(tInfo);
		String mac = MacUtils.creMsgSig(dataEncrypt, key);
		tInfo.setMac(mac);
		TransactionInfo resTran;
		resTran = apPort.certificateQuery(tInfo);
		return resTran.getCertList().get(0);
		
		
	}
}
