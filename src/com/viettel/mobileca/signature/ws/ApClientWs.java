/**
 *
 * @(#)ApClientWs Oct 15, 2014 10:28:12 AM Copyright 2014 Viettel ICT. All
 * rights reserved. VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license
 * terms.
 */
package com.viettel.mobileca.signature.ws;

import com.viettel.mobileca.signature.apservice.TransactionInfo;
import javax.jws.WebService;

/**
 *
 * @author minhnn10
 */
@WebService(serviceName = "ApClientWsImplService", portName = "ApClientWsImplPort", endpointInterface = "com.viettel.apclient.ws.ApClientWs", targetNamespace = "http://ws.apclient.viettel.com/")
public class ApClientWs {

    public TransactionInfo notification(TransactionInfo tranInfo) {
        //TODO implement this method
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
