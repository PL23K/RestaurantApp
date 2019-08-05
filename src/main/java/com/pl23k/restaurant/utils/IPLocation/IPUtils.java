package com.pl23k.restaurant.utils.IPLocation;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by HelloWorld on 2017/5/14.
 */
public class IPUtils
{
    /**
     * 获取登录用户的IP地址
     * 对手机无效
     * @param request
     * @return
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        if (ip.split(",").length > 1) {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static int ipToInt(String ip){
        int result = 0;
        String[] ipAddressInArray = ip.split("\\.");

        for (int i = 3; i >= 0; i--) {
            int iip = Integer.parseInt(ipAddressInArray[3 - i]);
            result |= iip << (i * 8);
        }
        return result;
    }

    public static  String intToIp(int iip) {
        StringBuilder result = new StringBuilder(15);

        for (int i = 0; i < 4; i++) {
            result.insert(0,Integer.toString(iip & 0xff));
            if (i < 3) {
                result.insert(0,'.');
            }
            iip = iip >> 8;
        }
        return result.toString();
    }

    //ip = 3232235778
    public String longToIp2(long ip) {

        return ((ip >> 24) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + (ip & 0xFF);
    }

    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRealIpV2(HttpServletRequest request) {
        String accessIP = request.getHeader("x-forwarded-for");
        if (null == accessIP)
            return request.getRemoteAddr();
        return accessIP;
    }
}
