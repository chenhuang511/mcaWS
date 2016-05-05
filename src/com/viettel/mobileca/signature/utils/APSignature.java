/**
 *
 * @(#)APSignature Feb 24, 2015 2:45:42 PM Copyright 2014 Viettel ICT. All
 * rights reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package com.viettel.mobileca.signature.utils;

import com.viettel.mobileca.signature.apservice.APWs;
import com.viettel.mobileca.signature.apservice.TransactionInfo;
import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author minhnn10
 */
public class APSignature {

    public static String onProcess(String hash) throws Exception {
        APWs apPort = APGetPort.getPort(Constants.URL_WS);
        TransactionInfo tInfo = new TransactionInfo();
        tInfo.setApId(Constants.AP_ID);
        tInfo.setProcessCode(Constants.CODE_SIGNATURE);
        DateFormat df;
        df = new SimpleDateFormat("yyMMddHHmmss");
        tInfo.setRequestId(Long.valueOf(df.format(new Date())));
        tInfo.setMsspId("Viettel");
        df = new SimpleDateFormat("yyyyMMddHHmmss");
        tInfo.setReqDate(df.format(new Date()));
        tInfo.setMsisdn(Constants.MSISDN);
        tInfo.setDataSign(hash);
        tInfo.setDataDisplay("ky van ban");
        tInfo.setMsgMode("SYNC");
        tInfo.setSigType(0);
        PrivateKey key = MacUtils.getPrivateKeyFromString(Constants.PRIVATE_KEY);
        String dataEncrypt = MacUtils.getDataEncrypt(tInfo);
        String mac = MacUtils.creMsgSig(dataEncrypt, key);
        tInfo.setMac(mac);
        TransactionInfo resTran = null;
        resTran = apPort.signature(tInfo);
        return resTran.getSignature();
    }
}
