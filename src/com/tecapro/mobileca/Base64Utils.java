package com.tecapro.mobileca;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Provides utilities for Base64 encode/decode of binary data.
 */
public class Base64Utils {

    private static byte[] mBase64EncMap, mBase64DecMap;

    /**
     * Class initializer. Initializes the Base64 alphabet (specified in RFC-2045).
     */
    static {
        byte[] base64Map = {
            (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F',
            (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
            (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R',
            (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X',
            (byte) 'Y', (byte) 'Z',
            (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
            (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
            (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
            (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
            (byte) 'y', (byte) 'z',
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/'};
        mBase64EncMap = base64Map;
        mBase64DecMap = new byte[128];
        for (int i = 0; i < mBase64EncMap.length; i++) {
            mBase64DecMap[mBase64EncMap[i]] = (byte) i;
        }
    }

    /**
     * This class isn't meant to be instantiated.
     */
    public Base64Utils() {
    }

    /**
     * Encodes the given byte[] using the Base64-encoding,
     * as specified in RFC-2045 (Section 6.8).
     *
     * @param aData the data to be encoded
     * @return the Base64-encoded <var>aData</var>
     * @exception IllegalArgumentException if NULL or empty array is passed
     */
    public static String base64Encode(byte[] aData) {
        if ((aData == null) || (aData.length == 0)) {
            throw new IllegalArgumentException("Can not encode NULL or empty byte array.");
        }

        byte encodedBuf[] = new byte[((aData.length + 2) / 3) * 4];

        // 3-byte to 4-byte conversion
        int srcIndex, destIndex;
        for (srcIndex = 0, destIndex = 0; srcIndex < aData.length - 2; srcIndex += 3) {
            encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex] >>> 2) & 077];
            encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex + 1] >>> 4) & 017
                    | (aData[srcIndex] << 4) & 077];
            encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex + 2] >>> 6) & 003
                    | (aData[srcIndex + 1] << 2) & 077];
            encodedBuf[destIndex++] = mBase64EncMap[aData[srcIndex + 2] & 077];
        }

        // Convert the last 1 or 2 bytes
        if (srcIndex < aData.length) {
            encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex] >>> 2) & 077];
            if (srcIndex < aData.length - 1) {
                encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex + 1] >>> 4) & 017
                        | (aData[srcIndex] << 4) & 077];
                encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex + 1] << 2) & 077];
            } else {
                encodedBuf[destIndex++] = mBase64EncMap[(aData[srcIndex] << 4) & 077];
            }
        }

        // Add padding to the end of encoded data
        while (destIndex < encodedBuf.length) {
            encodedBuf[destIndex] = (byte) '=';
            destIndex++;
        }

        String result = new String(encodedBuf);
        return result;
    }

    /**
     * Decodes the given Base64-encoded data,
     * as specified in RFC-2045 (Section 6.8).
     *
     * @param aData the Base64-encoded aData.
     * @return the decoded <var>aData</var>.
     * @exception IllegalArgumentException if NULL or empty data is passed
     */
    public static byte[] base64Decode(String aData) {
        if ((aData == null) || (aData.length() == 0)) {
            throw new IllegalArgumentException("Can not decode NULL or empty string.");
        }

        byte[] data = aData.getBytes();

        // Skip padding from the end of encoded data
        int tail = data.length;
        while (data[tail - 1] == '=') {
            tail--;
        }

        byte decodedBuf[] = new byte[tail - data.length / 4];

        // ASCII-printable to 0-63 conversion
        for (int i = 0; i < data.length; i++) {
            data[i] = mBase64DecMap[data[i]];
        }

        // 4-byte to 3-byte conversion
        int srcIndex, destIndex;
        for (srcIndex = 0, destIndex = 0; destIndex < decodedBuf.length - 2;
                srcIndex += 4, destIndex += 3) {
            decodedBuf[destIndex] = (byte) (((data[srcIndex] << 2) & 255)
                    | ((data[srcIndex + 1] >>> 4) & 003));
            decodedBuf[destIndex + 1] = (byte) (((data[srcIndex + 1] << 4) & 255)
                    | ((data[srcIndex + 2] >>> 2) & 017));
            decodedBuf[destIndex + 2] = (byte) (((data[srcIndex + 2] << 6) & 255)
                    | (data[srcIndex + 3] & 077));
        }

        // Handle last 1 or 2 bytes
        if (destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte) (((data[srcIndex] << 2) & 255)
                    | ((data[srcIndex + 1] >>> 4) & 003));
        }
        if (++destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte) (((data[srcIndex + 1] << 4) & 255)
                    | ((data[srcIndex + 2] >>> 2) & 017));
        }

        return decodedBuf;
    }

    public static byte[] toByteArray(String string) {
        byte[] bytes = new byte[string.length()];
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }
    //====================CONVERT DATE====================//
    /*
     *  ho tro them format 'Q/yyyy'
     * @author 		toandd
     * @param		date - ngay can lay format,
     * 			sDateFormat - format mark
     * @return      	String - theo format truyen vao
     * @since			07/09/2011
     */

    public static String formatDate(Date date, String sDateFormat) {
        String sOutput = "";
        if (date != null) {
            if (sDateFormat.equalsIgnoreCase("Q/yyyy")) {
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                int m = c.get(Calendar.MONTH);

                if (m <= Calendar.MARCH) {
                    sOutput = "01/" + c.get(Calendar.YEAR);
                } else if (m <= Calendar.JUNE) {
                    sOutput = "02/" + c.get(Calendar.YEAR);
                } else if (m <= Calendar.SEPTEMBER) {
                    sOutput = "03/" + c.get(Calendar.YEAR);
                } else {
                    sOutput = "04/" + c.get(Calendar.YEAR);
                }
            } else {
                sOutput = (new SimpleDateFormat(sDateFormat)).format(date);
            }
        }
        return sOutput;
    }

    protected static String getDateFormat() {
        return "dd/MM/yyyy";
    }

    public static String formatDate(Calendar date) {
        if (date != null) {
            return formatDate(date.getTime(), getDateFormat());
        } else {
            return "";
        }
    }

    public static String formatDate(Calendar date, String sDateFormat) {
        return formatDate(date.getTime(), sDateFormat);
    }

    public static String formatDate(Date date) {
        return formatDate(date, getDateFormat());
    }
    //////////////////////////////////////////////////////////////
    //=========TACH CHUOI ==============//

    public static String tachChuoi(String s, String key) {
        int index = s.indexOf(key);
        int indexP = s.indexOf(",");
        index = index + 3;
        StringBuffer sb = new StringBuffer();
        char[] arr = s.toCharArray();
        for (int i = index; i < arr.length; i++) {
            sb.append(arr[i]);
            if (arr[i] == ',') {
                sb.deleteCharAt(sb.length() - 1);
                break;
            }
        }
        String re = sb.toString();
        return re;
    }

    public String tachChuoi2(String s) {
        String str = "";
        if (s != null && !s.equals("")) {
            int index = s.indexOf("CN=");
            int indexP = s.indexOf(",");
            str = s.substring(index + 3, indexP);
        }
        return str;
    }
    /////////////////////////////////////////////////////////

    /*************CONVERT DATA*******************/
    /*
     * @author:toandd
     * @since: 16/09/2011
     * @des: get byte theo int
     */
    private static byte[] GenerateBigRandomNumber(int len) {
        byte[] challenge = new byte[len];
        java.util.Random randomGenerator = new java.util.Random();
        randomGenerator.nextBytes(challenge);
        return challenge;
    }
    // Sinh Challenge Message 64 byte thanh dang 123,43,76,11,...

    public String genChallenge() {
        byte[] challenge = GenerateBigRandomNumber(64);
        return ArrayByteFormatString(challenge);
    }

    /**
     * hÃ m nÃ y chuyá»ƒn 1 chuá»—i hexa(biá»ƒu diá»…n 1 khá»‘i byte) thÃ nh máº£ng byte[] .
     * @param str : Chuá»—i hexa biá»ƒu diá»…n khá»‘i byte
     * @return : tráº£ vá»� khá»‘i byte[] Ä‘Æ°á»£c biá»ƒu diá»…n bá»Ÿi chuá»—i hexa Str
     */
    public static byte[] stringFormatByteArray(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Chuyá»ƒn khá»‘i byte thÃ nh chuá»—i hexa biá»ƒu diá»…n khá»‘i byte[] nÃ y
     * @param arrayByte : Khá»‘i byte[] cáº§n biá»ƒu diá»…n
     * @return : Chuá»—i hexa biá»ƒu diá»…n khá»‘i byte Ä‘áº§u vÃ o
     */
    public static String ArrayByteFormatString(byte[] arrayByte) {
        String HEXES = "0123456789ABCDEF";
        StringBuilder hex = new StringBuilder(arrayByte.length * 2);
        for (final byte b : arrayByte) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    /**
     * Kiá»ƒm tra chuá»—i Ä‘áº§u vÃ o cÃ³ pháº£i Ä‘á»‹nh dáº¡ng Base64 hay khÃ´ng
     * @param base64String
     * @return : biáº¿n boolean
     */
    public static boolean IsBase64(String base64String) {
        if (base64String.replace(" ", "").length() % 4 != 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String convertDateToString(java.util.Date d, String format) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        return convertDateToString(c, format);
    }

    public static String convertDateToString(Calendar d, String format) {
        String dd = Integer.toString(d.get(5));
        String mm = Integer.toString(d.get(2) + 1);
        String yyyy = Integer.toString(d.get(1));
        String hh = Integer.toString(d.get(11));
        String mi = Integer.toString(d.get(12));
        String ss = Integer.toString(d.get(13));
        String ms = Integer.toString(d.get(14));

        if (dd.length() == 1) {
            dd = "0" + dd;
        }
        if (mm.length() == 1) {
            mm = "0" + mm;
        }
        if (hh.length() == 1) {
            hh = "0" + hh;
        }
        if (mi.length() == 1) {
            mi = "0" + mi;
        }
        if (ss.length() == 1) {
            ss = "0" + ss;
        }
        if (ms.length() == 1) {
            ms = "0" + ms;
        }
        if ("DD".equalsIgnoreCase(format)) {
            return dd;
        }
        if ("MM".equalsIgnoreCase(format)) {
            return mm;
        }
        if ("YYYY".equalsIgnoreCase(format)) {
            return yyyy;
        }
        if ("MM/YYYY".equals(format)) {
            return mm + "/" + yyyy;
        }
        if ("DD/MM/YYYY".equals(format)) {
            return dd + "/" + mm + "/" + yyyy;
        }
        if ("DD/MM/YYYY HH:MI:SS".equals(format)) {
            return dd + "/" + mm + "/" + yyyy + " " + hh + ":" + mi + ":" + ss;
        }
        if ("DDMMYYYYHH24MISS".equals(format)) {
            return dd + mm + yyyy + hh + mi + ss;
        }
        if ("DDMMYYYYHH24MISSMS".equals(format)) {
            return dd + mm + yyyy + hh + mi + ss + ms;
        }

        return null;
    }
}
