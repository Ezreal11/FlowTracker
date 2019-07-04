package com.zzw.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtil {
    public static boolean isDifferent(Object oldObj, Object newObj) {
        String oldStr = (oldObj == null) ? "" : oldObj.toString();
        String newStr = (newObj == null) ? "" : newObj.toString();
        return oldStr.compareTo(newStr) != 0;
    }

    public static String toString(Object obj) {
        return (obj == null) ? null : obj.toString();
    }

    public static String md5Encode(String s) {
        if (s == null || s.length() <= 0) {
            return s;
        }

        byte[] secretBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(s.getBytes());
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (secretBytes == null) {
            return s;
        }

        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }
}
