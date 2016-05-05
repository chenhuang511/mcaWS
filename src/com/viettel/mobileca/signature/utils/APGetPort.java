/**
 *
 * @(#)APGetPort Feb 24, 2015 2:51:33 PM Copyright 2014 Viettel ICT. All rights
 * reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.mobileca.signature.utils;

import com.viettel.mobileca.signature.apservice.APWs;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 *
 * @author minhnn10
 */
public class APGetPort {

    public static APWs getPort(String urlWs) throws Exception {
        URL url = new URL(urlWs);
        QName qName = new QName("http://ws.apservice.viettel.com/", "APWsImplService");
        javax.xml.ws.Service ser = javax.xml.ws.Service.create(url, qName);
        return ser.getPort(APWs.class);
    }
}
