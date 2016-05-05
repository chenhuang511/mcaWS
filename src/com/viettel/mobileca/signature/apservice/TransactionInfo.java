
package com.viettel.mobileca.signature.apservice;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for transactionInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="additionData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="apId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="apURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="certId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="certInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="certList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="certSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataDisplay" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dataSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="errorDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="forcePIN" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="mac" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msgMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msisdn" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mssFormat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="msspId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgErrCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgErrDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orgRequestId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="processCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reqDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="requestId" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="resDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="runImm" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="sigType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="signature" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeout" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transactionInfo", propOrder = {
    "additionData",
    "apId",
    "apURL",
    "certId",
    "certInfo",
    "certList",
    "certSerial",
    "dataDisplay",
    "dataSign",
    "errorCode",
    "errorDesc",
    "forcePIN",
    "mac",
    "msgMode",
    "msisdn",
    "mssFormat",
    "msspId",
    "orgErrCode",
    "orgErrDesc",
    "orgRequestId",
    "processCode",
    "reqDate",
    "requestId",
    "resDate",
    "runImm",
    "sigType",
    "signature",
    "timeout"
})
public class TransactionInfo {

    protected String additionData;
    protected String apId;
    protected String apURL;
    protected Integer certId;
    protected String certInfo;
    @XmlElement(nillable = true)
    protected List<String> certList;
    protected String certSerial;
    protected String dataDisplay;
    protected String dataSign;
    protected String errorCode;
    protected String errorDesc;
    protected Integer forcePIN;
    protected String mac;
    protected String msgMode;
    protected String msisdn;
    protected String mssFormat;
    protected String msspId;
    protected String orgErrCode;
    protected String orgErrDesc;
    protected Long orgRequestId;
    protected String processCode;
    protected String reqDate;
    protected Long requestId;
    protected String resDate;
    protected Integer runImm;
    protected Integer sigType;
    protected String signature;
    protected Long timeout;

    /**
     * Gets the value of the additionData property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditionData() {
        return additionData;
    }

    /**
     * Sets the value of the additionData property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditionData(String value) {
        this.additionData = value;
    }

    /**
     * Gets the value of the apId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApId() {
        return apId;
    }

    /**
     * Sets the value of the apId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApId(String value) {
        this.apId = value;
    }

    /**
     * Gets the value of the apURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApURL() {
        return apURL;
    }

    /**
     * Sets the value of the apURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApURL(String value) {
        this.apURL = value;
    }

    /**
     * Gets the value of the certId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCertId() {
        return certId;
    }

    /**
     * Sets the value of the certId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCertId(Integer value) {
        this.certId = value;
    }

    /**
     * Gets the value of the certInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertInfo() {
        return certInfo;
    }

    /**
     * Sets the value of the certInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertInfo(String value) {
        this.certInfo = value;
    }

    /**
     * Gets the value of the certList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the certList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCertList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getCertList() {
        if (certList == null) {
            certList = new ArrayList<String>();
        }
        return this.certList;
    }

    /**
     * Gets the value of the certSerial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCertSerial() {
        return certSerial;
    }

    /**
     * Sets the value of the certSerial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCertSerial(String value) {
        this.certSerial = value;
    }

    /**
     * Gets the value of the dataDisplay property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataDisplay() {
        return dataDisplay;
    }

    /**
     * Sets the value of the dataDisplay property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataDisplay(String value) {
        this.dataDisplay = value;
    }

    /**
     * Gets the value of the dataSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataSign() {
        return dataSign;
    }

    /**
     * Sets the value of the dataSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataSign(String value) {
        this.dataSign = value;
    }

    /**
     * Gets the value of the errorCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorCode(String value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorDesc() {
        return errorDesc;
    }

    /**
     * Sets the value of the errorDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorDesc(String value) {
        this.errorDesc = value;
    }

    /**
     * Gets the value of the forcePIN property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getForcePIN() {
        return forcePIN;
    }

    /**
     * Sets the value of the forcePIN property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setForcePIN(Integer value) {
        this.forcePIN = value;
    }

    /**
     * Gets the value of the mac property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMac() {
        return mac;
    }

    /**
     * Sets the value of the mac property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMac(String value) {
        this.mac = value;
    }

    /**
     * Gets the value of the msgMode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsgMode() {
        return msgMode;
    }

    /**
     * Sets the value of the msgMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsgMode(String value) {
        this.msgMode = value;
    }

    /**
     * Gets the value of the msisdn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsisdn(String value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the mssFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMssFormat() {
        return mssFormat;
    }

    /**
     * Sets the value of the mssFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMssFormat(String value) {
        this.mssFormat = value;
    }

    /**
     * Gets the value of the msspId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMsspId() {
        return msspId;
    }

    /**
     * Sets the value of the msspId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMsspId(String value) {
        this.msspId = value;
    }

    /**
     * Gets the value of the orgErrCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgErrCode() {
        return orgErrCode;
    }

    /**
     * Sets the value of the orgErrCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgErrCode(String value) {
        this.orgErrCode = value;
    }

    /**
     * Gets the value of the orgErrDesc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgErrDesc() {
        return orgErrDesc;
    }

    /**
     * Sets the value of the orgErrDesc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgErrDesc(String value) {
        this.orgErrDesc = value;
    }

    /**
     * Gets the value of the orgRequestId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getOrgRequestId() {
        return orgRequestId;
    }

    /**
     * Sets the value of the orgRequestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setOrgRequestId(Long value) {
        this.orgRequestId = value;
    }

    /**
     * Gets the value of the processCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessCode() {
        return processCode;
    }

    /**
     * Sets the value of the processCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessCode(String value) {
        this.processCode = value;
    }

    /**
     * Gets the value of the reqDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReqDate() {
        return reqDate;
    }

    /**
     * Sets the value of the reqDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReqDate(String value) {
        this.reqDate = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRequestId(Long value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the resDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResDate() {
        return resDate;
    }

    /**
     * Sets the value of the resDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResDate(String value) {
        this.resDate = value;
    }

    /**
     * Gets the value of the runImm property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRunImm() {
        return runImm;
    }

    /**
     * Sets the value of the runImm property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRunImm(Integer value) {
        this.runImm = value;
    }

    /**
     * Gets the value of the sigType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSigType() {
        return sigType;
    }

    /**
     * Sets the value of the sigType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSigType(Integer value) {
        this.sigType = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSignature(String value) {
        this.signature = value;
    }

    /**
     * Gets the value of the timeout property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getTimeout() {
        return timeout;
    }

    /**
     * Sets the value of the timeout property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setTimeout(Long value) {
        this.timeout = value;
    }

}
