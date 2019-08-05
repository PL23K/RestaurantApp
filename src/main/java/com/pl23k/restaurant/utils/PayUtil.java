package com.pl23k.restaurant.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.HttpKit;
import com.pl23k.restaurant.controller.IndexController;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by HelloWorld on 2019-05-12.
 */
public class PayUtil {

    /**
     * 微信支付签名
     * @param characterEncoding
     * @param parameters
     * @return
     */
    public static String wechatPayCreateSign(String characterEncoding,SortedMap<String,Object> parameters,String key){
        String sign = "";
        try{
            StringBuffer sb = new StringBuffer();
            Set es = parameters.entrySet();
            Iterator it = es.iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String k = (String)entry.getKey();
                Object v = entry.getValue();
                if(null != v && !"".equals(v)
                        && !"sign".equals(k) && !"key".equals(k)) {
                    sb.append(k + "=" + v + "&");
                }
            }
            sb.append("key=" + key);
            sign = MD5Util.getMD5(sb.toString(), characterEncoding).toUpperCase();
        }catch (Exception e){
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 微信签名SHA1
     * @param parameters
     * @return
     */
    public static String wechatPayCreateSignSHA1(SortedMap<String,Object> parameters){
        String sign = "";
        try{
            StringBuffer sb = new StringBuffer();
            Set es = parameters.entrySet();
            Iterator it = es.iterator();
            while(it.hasNext()) {
                Map.Entry entry = (Map.Entry)it.next();
                String k = (String)entry.getKey();
                Object v = entry.getValue();
                if(null != v && !"".equals(v)
                        && !"sign".equals(k) && !"key".equals(k)) {
                    sb.append(k + "=" + v + "&");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            //System.out.println(sb.toString());
            sign = DigestUtils.sha1Hex(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 将Map转换为XML格式的字符串
     *
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }

    /**
     * 将SortedMap<Object,Object> 集合转化成 xml格式
     * @param parameters
     * @return
     */
    public static String getRequestXml(SortedMap<String,Object> parameters){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            //if ("attach".equalsIgnoreCase(k)||"body".equalsIgnoreCase(k)||"sign".equalsIgnoreCase(k)) {
                sb.append("<"+k+">"+"<![CDATA["+v+"]]></"+k+">");
            //}else {
                //sb.append("<"+k+">"+v+"</"+k+">");
            //}
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    /**
     * 验证签名
     * @param map
     * @return
     */
    public static Boolean verifySign(Map<String,String> map,String key){
        Boolean ret = false;
        try{
            SortedMap<String,Object> sortedMap = new TreeMap<>(map);
            String sign = wechatPayCreateSign("utf-8",sortedMap,key);
            if(sign.equals(map.get("sign"))){
                ret = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 获取jssdk配置
     * @return
     */
    public static Map<String,Object> getJssdkConfig(String url, String wxAppId, String wxAppSecret, HttpSession httpSession){
        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String accessToken = null;
        Long accessTokenTime = null;
        try{
            accessToken = (String)httpSession.getAttribute(IndexController.WX_ACCESS_TOKEN);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(accessToken!=null){
            try{
                accessTokenTime = (Long)httpSession.getAttribute(IndexController.WX_ACCESS_TOKEN_TIME);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(accessTokenTime == null){
                accessToken = null;
            }else{
                if((System.currentTimeMillis()/1000)  > accessTokenTime){//超时了
                    accessToken = null;
                }
            }
        }
        //判断accessToken
        if(accessToken == null){
            //重新获取
            String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wxAppId+"&secret="+wxAppSecret;
            JSONObject result = JSON.parseObject(HttpKit.get(accessTokenUrl));
            if(result != null){
                accessToken = result.getString("access_token");
                httpSession.setAttribute(IndexController.WX_ACCESS_TOKEN,accessToken);
                httpSession.setAttribute(IndexController.WX_ACCESS_TOKEN_TIME,(System.currentTimeMillis()/1000)+7000);
            }
        }
        String jsapiTicket = null;
        Long jsapiTicketTime = null;
        try{
            jsapiTicket = (String)httpSession.getAttribute(IndexController.WX_JSAPI_TICKET);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(jsapiTicket!=null){
            try{
                jsapiTicketTime = (Long)httpSession.getAttribute(IndexController.WX_JSAPI_TICKET_TIME);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(jsapiTicketTime == null){
                jsapiTicket = null;
            }else{
                if((System.currentTimeMillis()/1000)  > jsapiTicketTime){//超时了
                    jsapiTicket = null;
                }
            }
        }
        //判断accessToken
        if(jsapiTicket == null){
            //重新获取
            String jsapiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token="+accessToken;
            JSONObject result = JSON.parseObject(HttpKit.get(jsapiTicketUrl));
            if(result != null){
                jsapiTicket = result.getString("ticket");
                httpSession.setAttribute(IndexController.WX_JSAPI_TICKET,jsapiTicket);
                httpSession.setAttribute(IndexController.WX_JSAPI_TICKET_TIME,(System.currentTimeMillis()/1000)+7000);
            }
        }
        SortedMap<String,Object> map = new TreeMap<>();
        map.put("timestamp",Integer.valueOf(timestamp));
        map.put("noncestr",Utils.createRandom(false,32));
        map.put("jsapi_ticket",jsapiTicket);
        map.put("url",url);
        map.put("signature",PayUtil.wechatPayCreateSignSHA1(map));
        map.put("appId",wxAppId);

        return map;
    }

    /**
     * 发送模板消息
     * @param openId
     * @param templateId
     * @param wxAppId
     * @param wxAppSecret
     * @param data
     * @param httpSession
     * @return
     */
    public static Boolean sendMessage(String openId,String templateId,String wxAppId,String wxAppSecret, JSONObject data,HttpSession httpSession){
        Boolean ret = false;
        try{
            String timestamp = String.valueOf(System.currentTimeMillis()/1000);
            String accessToken = null;
            Long accessTokenTime = null;
            try{
                accessToken = (String)httpSession.getAttribute(IndexController.WX_ACCESS_TOKEN);
            }catch (Exception e){
                e.printStackTrace();
            }
            if(accessToken!=null){
                try{
                    accessTokenTime = (Long)httpSession.getAttribute(IndexController.WX_ACCESS_TOKEN_TIME);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(accessTokenTime == null){
                    accessToken = null;
                }else{
                    if((System.currentTimeMillis()/1000)  > accessTokenTime){//超时了
                        accessToken = null;
                    }
                }
            }
            //判断accessToken
            if(accessToken == null){
                //重新获取
                String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+wxAppId+"&secret="+wxAppSecret;
                JSONObject result = JSON.parseObject(HttpKit.get(accessTokenUrl));
                if(result != null){
                    accessToken = result.getString("access_token");
                    httpSession.setAttribute(IndexController.WX_ACCESS_TOKEN,accessToken);
                    httpSession.setAttribute(IndexController.WX_ACCESS_TOKEN_TIME,(System.currentTimeMillis()/1000)+7000);
                }
            }
            JSONObject post = new JSONObject();
            post.put("touser",openId);
            post.put("template_id",templateId);
            post.put("appid",wxAppId);
            post.put("data",data);
            String postUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="+accessToken;
            System.out.println("消息推送："+post.toJSONString());
            String result = HttpKit.post(postUrl,post.toJSONString());
            JSONObject res = JSON.parseObject(result);
            if("ok".equals(res.getString("errmsg"))){
                ret = true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
