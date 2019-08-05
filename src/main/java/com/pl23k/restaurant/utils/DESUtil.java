package com.pl23k.restaurant.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 加密解密工具类。
 * <p>
 * 采用对称加密算法
 * </P>
 */
public class DESUtil {

    private static final String KEY_STR = "9b5e5022d8850c31a6326fe75134bc583ad2e-2ec3-5fec-bddd-c276fcdda2ed";
    /**
     * 加密字符串。
     * @param str 待加密的字符串
     * @return 加密后的字符串
     */
    public static String encryStr(String str) {
        try {

            DESKeySpec objDesKeySpec = new DESKeySpec(KEY_STR.getBytes("UTF-8"));
            SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey deskey = objKeyFactory.generateSecret(objDesKeySpec);

            Cipher c1 = Cipher.getInstance("DES");
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] cipherByte = c1.doFinal(str.getBytes());
            BASE64Encoder en = new BASE64Encoder();
            String s = en.encode(cipherByte);
            str = s;
        } catch (Exception e) {
            e.printStackTrace();
            str = "";
        }
        return str;
    }

    /**
     * 解密字符串。
     * @param str 待解密的字符串
     * @return 解密后的字符串
     */
    public static String decryStr(String str) {
        try {
            DESKeySpec objDesKeySpec = new DESKeySpec(KEY_STR.getBytes("UTF-8"));
            SecretKeyFactory objKeyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey deskey = objKeyFactory.generateSecret(objDesKeySpec);
            BASE64Decoder de = new BASE64Decoder();
            byte[] cipherByte = de.decodeBuffer(str);
            Cipher c1 = Cipher.getInstance("DES");
            c1.init(Cipher.DECRYPT_MODE, deskey);
            byte[] clearByte = c1.doFinal(cipherByte);
            String s = new String(clearByte);
            str = s;
        } catch (Exception e) {
            e.printStackTrace();
            str = "";
        }
        return str;
    }


    public static void main(String[] args) {
        System.out.println("加密" + DESUtil.encryStr("12#$%67xy01sef5"));
        System.out.println("解密" + DESUtil.decryStr("ByBhEATY53A="));
    }

}
