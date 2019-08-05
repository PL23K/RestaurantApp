package com.pl23k.restaurant.utils;

import org.apache.tomcat.util.security.MD5Encoder;

import java.security.MessageDigest;

/**
 * Created by HelloWorld on 2017/6/28.
 */
public class MD5Util {
    private static MessageDigest md5md = null;

    public static String getMD5(String in) {
        String out = null;
        try {

            if (md5md == null) {
                md5md = MessageDigest.getInstance("MD5");
            }
            if(in != null && in.length()>0){
                out = MD5Encoder.encode(md5md.digest(in.getBytes("utf-8")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public static String getMD5(String in,String charsetName) {
        String out = null;
        try {

            if (md5md == null) {
                md5md = MessageDigest.getInstance("MD5");
            }
            if(in != null && in.length()>0){
                out = MD5Encoder.encode(md5md.digest(in.getBytes(charsetName)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
}
