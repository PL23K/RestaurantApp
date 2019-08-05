package com.pl23k.restaurant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.pl23k.restaurant.common.SystemConfig;
import com.pl23k.restaurant.constants.Common;
import com.pl23k.restaurant.constants.JsonCodeType;
import com.pl23k.restaurant.interceptor.GlobalInterceptor;
import com.pl23k.restaurant.interceptor.UserAuthInterceptor;
import com.pl23k.restaurant.model.*;
import com.pl23k.restaurant.utils.DESUtil;
import com.pl23k.restaurant.utils.DateUtil;
import com.pl23k.restaurant.utils.IPLocation.IPUtils;
import com.pl23k.restaurant.utils.PayUtil;
import com.pl23k.restaurant.utils.Utils;
import org.apache.log4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by PL23K on 2018-12-13.
 */
@Before(UserAuthInterceptor.class)
public class IndexController extends Controller {
    private static Logger logger = Logger.getLogger(IndexController.class);

    public static final String SESSION_WECHAT = "SESSION_WECHAT";
    public static final String SESSION_USER_NAME = "SESSION_USER_NAME";
    public static final String SESSION_USER_MEMBER_ID = "SESSION_USER_MEMBER_ID";
    public static final String SESSION_LOGIN_ERROR_TIMES = "SESSION_LOGIN_ERROR_TIMES";

    public static final String COOKIE_USER_NMAE = "COOKIE_USER_NAME";   //COOKIE username
    public static final String COOKIE_PHONE = "COOKIE_PHONE";   //COOKIE phone

    public static final String COOKIE_CUR_ROLE = "COOKIE_CUR_ROLE";   //

    public static final String WX_ACCESS_TOKEN = "WX_ACCESS_TOKEN";   //
    public static final String WX_ACCESS_TOKEN_TIME = "WX_ACCESS_TOKEN_TIME";   //
    public static final String WX_JSAPI_TICKET = "WX_JSAPI_TICKET";   //
    public static final String WX_JSAPI_TICKET_TIME = "WX_JSAPI_TICKET_TIME";   //

    public static final String WX_LOGIN_OPENID = "WX_LOGIN_OPENID"; //因为登录的时候要多次用到openid和accessToken，所以保存起来会好一些
    public static final String WX_LOGIN_ACCESS_TOKEN = "WX_LOGIN_ACCESS_TOKEN";

    /**
     * 首页
     */
    @Clear
    @Before(GlobalInterceptor.class)
    public void index(){
        try{
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            /*
            User user = User.getRecordByMemberId("A190000000");
            setSessionAttr(IndexController.SESSION_USER_NAME, user.getUsername());
            setSessionAttr(IndexController.SESSION_USER_MEMBER_ID,user.getMemberId());
            setSessionAttr(IndexController.SESSION_LOGIN_ERROR_TIMES,0);
            //cookie记录用户名
            setCookie(IndexController.COOKIE_USER_NMAE,user.getUsername(),999999999);
            */

            Worker worker = Worker.getRecordByUsername(getSessionAttr(SESSION_USER_NAME));
            if(worker!=null && "worker".equals(getCookie(COOKIE_CUR_ROLE))){
                redirect("/worker"+SystemConfig.sExt);
                return;
            }

            if(user!=null){
                Map info = new HashMap();
                BigDecimal returnMoney = RechargeOrder.getWaitMoneyByMemberId(user.getMemberId());
                if(returnMoney == null){
                    returnMoney = BigDecimal.ZERO;
                }
                info.put("user",user);
                info.put("returnMoney",returnMoney);
                setAttr("info",info);
                render("Index.fmk");
            }else{
                redirect("/login"+SystemConfig.sExt);
            }
        }catch (Exception e){
            e.printStackTrace();
            redirect("/login"+SystemConfig.sExt);
        }
    }

    /**
     * 获取JSSDK配置
     */
    public void getJssdkConfig(){
        Ret ret = new Ret();
        ret.set("code",JsonCodeType.CODE_FAILED)
                .set("message","操作失败");
        try{
            String url = getPara("url","/");
            Config config = Config.getConfig();
            //准备签名
            Map<String,Object> map = PayUtil.getJssdkConfig(url,config.getWxAppId(),config.getWxAppSecret(),getSession());

            ret.set("code",JsonCodeType.CODE_SUCCESS)
                    .set("message","签名成功")
                    .set("data",map);

        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(ret);
    }

    @Clear
    @Before(GlobalInterceptor.class)
    public void login(){
        setAttr("content","微信登录失败，请重试");
        render("ErrorPage.fmk");
    }

    @Clear
    @Before(GlobalInterceptor.class)
    public void wxLogin() {
        try {
            //1、判断是否授权
            String code = getPara("code");
            String state = getPara("state");
            String openId = "";
            String accessToken = "";
            Config config = Config.getConfig();
            if(StrKit.isBlank(code)){
                setAttr("content","微信登录错误：无效的code");
                render("ErrorPage.fmk");
                return ;
            }
            //2、获取openid
            String accessTokenResult = HttpKit.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ config.getWxAppId()+"&secret="+config.getWxAppSecret()+"&code="+code+"&grant_type=authorization_code");
            if(StrKit.isBlank(accessTokenResult)){
                setAttr("content","微信登录错误：获取accessToken失败");
                render("ErrorPage.fmk");
                return ;
            }
            JSONObject accessTokenJson = JSON.parseObject(accessTokenResult);
            if(accessTokenJson == null){
                setAttr("content","微信登录错误：accessToken内容错误："+accessTokenResult);
                render("ErrorPage.fmk");
                return ;
            }
            if(StrKit.isBlank(accessTokenJson.getString("openid")) || StrKit.isBlank(accessTokenJson.getString("access_token"))){
                setAttr("content","微信登录错误：openid或者access_token为空");
                render("ErrorPage.fmk");
                return ;
            }
            openId = accessTokenJson.getString("openid");
            accessToken = accessTokenJson.getString("access_token");
            //保存
            //setSessionAttr(WX_LOGIN_OPENID,openId);
            //setSessionAttr(WX_LOGIN_ACCESS_TOKEN,accessToken);
            //3、判断用户是否已存在
            WechatUser wechatUser = WechatUser.getWechatUserByOpenId(openId);
            if(wechatUser != null){
                User user = User.getRecordByMemberId(wechatUser.getMemberId());
                if(StrKit.isBlank(wechatUser.getMemberId()) || user == null){
                    setAttr("content","微信登录错误：用户不存在，请稍后再试");
                    render("ErrorPage.fmk");
                    return ;
                }else{
                    //3.1 存在则登录  ->跳转首页，结束
                    //添加Session
                    setSessionAttr(IndexController.SESSION_WECHAT, wechatUser.getOpenId());
                    setSessionAttr(IndexController.SESSION_USER_NAME, user.getUsername());
                    setSessionAttr(IndexController.SESSION_USER_MEMBER_ID,user.getMemberId());
                    setSessionAttr(IndexController.SESSION_LOGIN_ERROR_TIMES,0);
                    //cookie记录用户名
                    setCookie(IndexController.COOKIE_USER_NMAE,user.getUsername(),999999999);

                    //记录登录IP
                    //String ip = IPUtils.getIp(getRequest());
                    //LoginLog.addLoginLog(user.getIdentifier(),ip);
                    redirect("/");
                    return ;
                }
            }
            //4获取用户信息
            redirect("https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ config.getWxAppId()
                    +"&redirect_uri="+config.getWxUserInfoCall()
                    +"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect");
        } catch (Exception e) {
            setAttr("content","微信登录错误："+e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     * 请求授权
     */
    @Clear
    @Before(GlobalInterceptor.class)
    public void wxUserInfo(){
        //snsapi_userinfo
        try {
            //1、判断是否授权
            String code = getPara("code");
            String state = getPara("state");
            String openId = "";//getSessionAttr(WX_LOGIN_OPENID);
            String accessToken = "";//getSessionAttr(WX_LOGIN_ACCESS_TOKEN);
            Config config = Config.getConfig();
            if(StrKit.isBlank(code)){
                setAttr("content","获取信息失败：无效的code");
                render("ErrorPage.fmk");
                return ;
            }
            //2、获取openid
            //if(StrKit.isBlank(openId) || StrKit.isBlank(accessToken)){
                String accessTokenResult = HttpKit.get("https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ config.getWxAppId()+"&secret="+config.getWxAppSecret()+"&code="+code+"&grant_type=authorization_code");
                if(StrKit.isBlank(accessTokenResult)){
                    setAttr("content","获取信息失败：获取accessToken失败");
                    render("ErrorPage.fmk");
                    return ;
                }
                JSONObject accessTokenJson = JSON.parseObject(accessTokenResult);
                if(accessTokenJson == null){
                    setAttr("content","获取信息失败：accessToken内容错误："+accessTokenResult);
                    render("ErrorPage.fmk");
                    return ;
                }
                if(StrKit.isBlank(accessTokenJson.getString("openid")) || StrKit.isBlank(accessTokenJson.getString("access_token"))){
                    setAttr("content","获取信息失败：无效的openid或access_token");
                    render("ErrorPage.fmk");
                    return ;
                }
                openId = accessTokenJson.getString("openid");
                accessToken = accessTokenJson.getString("access_token");
            //}
            //4、获取用户信息
            String userInfoResult = HttpKit.get("https://api.weixin.qq.com/sns/userinfo?access_token="+accessToken+"&openid="+openId+"&lang=zh_CN");
            if(StrKit.isBlank(userInfoResult)){
                setAttr("content","获取信息失败：获取userInfoResult失败");
                render("ErrorPage.fmk");
                return ;
            }
            JSONObject userInfoObject = JSON.parseObject(userInfoResult);
            if(userInfoObject==null || !openId.equals(userInfoObject.getString("openid"))){
                logger.error("获取信息失败："+userInfoResult);
                setAttr("content","获取信息失败：获取用户信息失败");
                render("ErrorPage.fmk");
                return ;
            }
            WechatUser wu = WechatUser.getWechatUserByOpenId(openId);
            if(wu!=null){
                setAttr("content","获取信息失败：用户已创建，请重新登录");
                render("ErrorPage.fmk");
                return ;
            }
            WechatUser newWechatUser = new WechatUser();
            newWechatUser.setNickName(userInfoObject.getString("nickname"));
            newWechatUser.setAccessToken(accessToken);
            newWechatUser.setAccessTokenTime(new Date());
            newWechatUser.setAddTime(new Date());
            newWechatUser.setOpenId(openId);
            newWechatUser.setSex("1".equals(userInfoObject.getString("sex")));
            newWechatUser.setAvatarUrl(userInfoObject.getString("headimgurl")==null?"":userInfoObject.getString("headimgurl"));
            newWechatUser.setUnionId(userInfoObject.getString("unionid")==null?"":userInfoObject.getString("unionid"));
            newWechatUser.setAddress(userInfoObject.getString("province")+" "+userInfoObject.getString("city")+" "+userInfoObject.getString("country"));
            newWechatUser.setPrivilege(userInfoObject.getString("privilege")==null? "":userInfoObject.getString("privilege"));
            if(!newWechatUser.save()){
                setAttr("content","获取信息失败：保存微信用户失败");
                render("ErrorPage.fmk");
            }else{
                setSessionAttr(IndexController.SESSION_WECHAT, newWechatUser.getOpenId());
                //新建用户
                User user = new User();
                user.setMemberId(User.getNewIdentifier())
                        .setUsername(user.getMemberId().substring(1))
                        .setPassword(DESUtil.encryStr("888888"))
                        .setRealName(newWechatUser.getNickName())
                        .setSex(newWechatUser.getSex())
                        .setBirthday(new Date())
                        .setAddress(newWechatUser.getAddress())
                        .setPhone("")
                        .setEmail("")
                        .setLevel(1)
                        .setAvatar(newWechatUser.getAvatarUrl())
                        .setIsActive(true)
                        .setCoin(BigDecimal.ZERO)
                        .setMoney(BigDecimal.ZERO)
                        .setWechatId(newWechatUser.getId())
                        .setAddTime(new Date());
                if(user.save()){
                    //登录
                    newWechatUser.setMemberId(user.getMemberId())
                            .update();
                    //添加Session
                    setSessionAttr(IndexController.SESSION_WECHAT, newWechatUser.getOpenId());
                    setSessionAttr(IndexController.SESSION_USER_NAME, user.getUsername());
                    setSessionAttr(IndexController.SESSION_USER_MEMBER_ID,user.getMemberId());
                    setSessionAttr(IndexController.SESSION_LOGIN_ERROR_TIMES,0);
                    //cookie记录用户名
                    setCookie(IndexController.COOKIE_USER_NMAE,user.getUsername(),999999999);

                    logger.info("Login success.  User:"+newWechatUser.getNickName());
                    //记录登录IP
                    //String ip = IPUtils.getIp(getRequest());
                    //LoginLog.addLoginLog(user.getIdentifier(),ip);
                    redirect("/");
                }else{
                    //出错了
                    newWechatUser.delete();
                    setAttr("content","获取信息失败：保存用户失败");
                    render("ErrorPage.fmk");
                }
            }
        } catch (Exception e) {
            setAttr("content","获取信息失败："+e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     * 我的二维码
     */
    public void qrCode(){
        try{
            String domain = getRequest().getScheme()+"://"+getRequest().getServerName();
            if(80 != getRequest().getServerPort()){
                domain += ":"+getRequest().getServerPort();
            }
            renderQrCode(domain+"/register/"+getSessionAttr(IndexController.SESSION_USER_NAME)+SystemConfig.sExt+"#User"+getSessionAttr(IndexController.SESSION_USER_NAME),300,300);
        }catch (Exception e){
            e.printStackTrace();
            renderError(500);
        }
    }

    /**
     * 充值页面
     */
    public void recharge(){
        try{
            String inviteCode = getPara(0,"");
            Map<String,Object> map = new HashMap<>();
            //1、获取店铺列表
            List<Shop> shops = Shop.getAllShop(true);
            map.put("shopList",shops);
            RechargeOrder rechargeOrder = RechargeOrder.getLastShopRechargeByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            if(rechargeOrder!=null){
                map.put("lastShopId",rechargeOrder.getShopId());
            }else{
                map.put("lastShopId",0);
            }
            //2、获取充值金额列表
            List<Map<String,Object>> rechargeList = Promotion.getPromotion2List();
            map.put("rechargeList",rechargeList);
            //3、获取历史充值
            List<RechargeOrder> rechargeOrders = RechargeOrder.getRechargesByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            map.put("rechargeOrderList",rechargeOrders);
            map.put("inviteCode",inviteCode);
            setAttr("info",map);
            render("Recharge.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 消费列表
     */
    public void consumeList(){
        try{
            Integer pageNumber = getParaToInt(0,1);
            Map<String,Object> info = new HashMap<>();
            Page<Consume> consumes = Consume.searchRecordByPage(pageNumber,1,getSessionAttr(SESSION_USER_NAME),"","");
            info.put("consumeList",consumes);
            info.put("todayConsume",Consume.getTodayConsumeByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID)));
            info.put("totalConsume",Consume.getTotalConsumeByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID)));
            setAttr("info",info);
            render("ConsumeList.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 创建充值订单
     */
    public void doRecharge(){
        Ret ret = new Ret();
        ret.set("code", JsonCodeType.CODE_SUCCESS)
                .set("message","操作成功");
        try{
            Integer shopId = getParaToInt("shopId",0);
            Integer rechargeId = getParaToInt("rechargeId",0);
            String inviteCode = getPara("inviteCode","");
            Worker worker = null;
            if(StrKit.notBlank(inviteCode)){
                worker = Worker.getRecordByUsername(inviteCode);
                if(worker == null){
                    throw new Exception("店员编号错误，请返回重试。");
                }
            }
            Shop shop = Shop.getShopById(shopId);
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            WechatUser wechatUser = WechatUser.getWechatUserByMemberId(user.getMemberId());
            //获取活动
            Map<String,Object> recharge = Promotion.getPromotion2ItemById(rechargeId);
            if(recharge==null){
                throw new Exception("请选择充值金额，返回重试。");
            }
            //创建订单
            RechargeOrder rechargeOrder = new RechargeOrder();
            rechargeOrder.setMemberId(getSessionAttr(SESSION_USER_MEMBER_ID))
                    .setPayType(3)
                    .setPayId("")
                    .setPrepayId("")
                    .setOrderId("ORDER"+Utils.createRandom(true,16))
                    .setStatus(0)
                    .setAddTime(new Date())
                    .setRemark(String.format("用户%s[%s]于%s充值返现活动%.2f元，%d小时后返现%.2f元。推荐员工：%s。",
                            user.getRealName(),user.getUsername(), DateUtil.formatDateTime(rechargeOrder.getAddTime()),
                            Double.valueOf((String)recharge.get("充值")),Integer.valueOf((String)recharge.get("返现时间")),
                            Double.valueOf((String)recharge.get("返现金额")),worker==null?"无":(worker.getRealName()+"["+worker.getUsername()+"]")))
                    .setMoney(new BigDecimal((String)recharge.get("充值")))
                    .setPayMoney(new BigDecimal((String)recharge.get("充值")))
                    .setDiscount(100)
                    .setDiscountMoney(BigDecimal.ZERO)
                    .setPromotion(1)
                    .setRecommendType(worker==null?0:3)
                    .setRecommendId(worker==null?0:worker.getId())
                    .setRoyaltyRate(worker==null?0:worker.getRoyaltyRate())
                    .setRoyalty(worker==null?BigDecimal.ZERO:(rechargeOrder.getPayMoney().multiply(new BigDecimal(worker.getRoyaltyRate())).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP)))
                    .setReturnMoney(new BigDecimal((String)recharge.get("返现金额")))
                    .setIsPayPromotion(false);
            if(!rechargeOrder.save()){
                throw new Exception("创建订单失败");
            }
            //向微信获取预付码
            Config config = Config.getConfig();
            SortedMap<String,Object> params = new TreeMap<>();
            params.put("appid", config.getWxAppId());
            params.put("mch_id",config.getWxMchId());
            params.put("device_info",user.getMemberId());
            params.put("nonce_str", Utils.createRandom(false, 32));
            params.put("body",config.getCompany()+"-"+(shop==null?"总店":shop.getName())+"充值");
            params.put("detail",rechargeOrder.getRemark());
            params.put("out_trade_no",rechargeOrder.getOrderId());
            params.put("total_fee",String.valueOf(rechargeOrder.getPayMoney().multiply(new BigDecimal(100)).intValue()));
            params.put("spbill_create_ip",IPUtils.getIp(getRequest()));
            String url = config.getSiteUrl()+"/wechartNotify";
            params.put("notify_url",url);
            params.put("trade_type","JSAPI");
            params.put("openid",wechatUser.getOpenId());
            params.put("sign", PayUtil.wechatPayCreateSign("utf-8",params,config.getWxAppSecret()));
            //转为xml
            String requestXML = PayUtil.getRequestXml(params);
            Map<String,String> headers = new HashMap<>();
            headers.put("content-type", "text/xml");
            String resultXML = HttpKit.post(config.getWxPayApi(),null,requestXML,headers);
            //logger.info(requestXML);
            //logger.info(resultXML);
            Map<String,String> result = PayUtil.xmlToMap(resultXML);
            if(result==null){
                ret.set("code", JsonCodeType.CODE_FAILED)
                        .set("message","微信订单请求串创建失败，请返回重试。");
            }else{
                //签名验证
                //logger.info(result);
                if(PayUtil.verifySign(result,config.getWxAppSecret())){
                    //查看内容
                    if(!"SUCCESS".equals(result.get("return_code"))){
                        ret.set("code", JsonCodeType.CODE_FAILED)
                                .set("message",	result.get("return_msg")==null?"网络请求失败":result.get("return_msg"));
                    }else{
                        if(!"SUCCESS".equals(result.get("result_code"))){
                            ret.set("code", JsonCodeType.CODE_FAILED)
                                    .set("message",	result.get("err_code_des")==null?"微信订单创建失败":((result.get("err_code")==null?"":result.get("err_code"))+":"+result.get("err_code_des")));
                        }else{
                            //订单创建成功
                            rechargeOrder.setPrepayId(result.get("prepay_id"));
                            rechargeOrder.update();
                            String timestamp = String.valueOf(System.currentTimeMillis()/1000);
                            SortedMap<String,Object> params2 = new TreeMap<>();
                            params2.put("appId",config.getWxAppId());
                            params2.put("timeStamp",timestamp);
                            params2.put("nonceStr",Utils.createRandom(false,32));
                            params2.put("package","prepay_id="+rechargeOrder.getPrepayId());
                            params2.put("signType","MD5");
                            params2.put("paySign",PayUtil.wechatPayCreateSign("utf-8",params2,config.getWxAppSecret()));
                            ret.set("code",JsonCodeType.CODE_SUCCESS)
                                    .set("message","微信订单创建成功")
                                    .set("data",params2);
                        }
                    }
                }else{
                    ret.set("code", JsonCodeType.CODE_FAILED)
                            .set("message","签名验证失败");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ret.set("code", JsonCodeType.CODE_FAILED)
                    .set("message",e.getMessage());
        }
        renderJson(ret);
    }

    @Clear
    public void wechartNotify(){
        try{
            String ret = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
            String resultXML = HttpKit.readData(getRequest());
            Map<String,String> result = PayUtil.xmlToMap(resultXML);
            //签名验证
            Config config = Config.getConfig();
            if(PayUtil.verifySign(result,config.getWxAppSecret())){
                //查看内容
                if("SUCCESS".equals(result.get("return_code"))){
                    if("SUCCESS".equals(result.get("result_code"))){
                        //检查是否已经收到过该订单
                        RechargeOrder rechargeOrder = RechargeOrder.getRecordByOrderId(result.get("out_trade_no"));
                        if(rechargeOrder==null){
                            throw new Exception("无效的订单号");
                        }
                        RechargePayOrderWechat payOrderWechat = RechargePayOrderWechat.getPayOrderByPayId(result.get("transaction_id"));
                        if(payOrderWechat == null){
                            //写入数据
                            payOrderWechat = new RechargePayOrderWechat();
                            payOrderWechat.setPayId(result.get("transaction_id"))
                                    .setAppId(result.get("appid"))
                                    .setMchId(result.get("mch_id"))
                                    .setSeqNo(result.get("device_info"))
                                    .setNonceStr(result.get("nonce_str"))
                                    .setSign(result.get("sign"))
                                    .setOpenId(result.get("openid"))
                                    .setIsSubscribe("Y".equals(result.get("is_subscribe")))
                                    .setTradeType(result.get("trade_type"))
                                    .setBankType(result.get("bank_type"))
                                    .setTotalFee(Integer.valueOf(result.get("total_fee")))
                                    .setFeeType(result.get("fee_type"))
                                    .setCashFee(Integer.valueOf(result.get("cash_fee")))
                                    .setCashFeeType(result.get("cash_fee_type")==null?"":result.get("cash_fee_type"))
                                    .setCouponFee(StrKit.isBlank(result.get("coupon_fee"))?0:Integer.valueOf(result.get("coupon_fee")))
                                    .setCouponCount(StrKit.isBlank(result.get("coupon_count"))?0:Integer.valueOf(result.get("coupon_count")))
                                    .setCouponIds("")
                                    .setCouponFees("")
                                    .setOutTradeNo(result.get("out_trade_no"))
                                    .setAttach(result.get("attach")==null?"":result.get("attach"))
                                    .setTimeEnd(DateUtil.formatString(result.get("time_end"),"yyyyMMddHHmmss"));
                            if(payOrderWechat.save()){
                                //写入数据
                                User user = User.getRecordByMemberId(rechargeOrder.getMemberId());
                                user.setMoney(user.getMoney().add(rechargeOrder.getMoney()));
                                user.update();
                                rechargeOrder.setPayId(payOrderWechat.getPayId());
                                rechargeOrder.setPayType(3);
                                rechargeOrder.setStatus(3);
                                rechargeOrder.setAddTime(new Date());
                                rechargeOrder.update();
                                renderText(ret);
                                //通知用户购买成功
                                WechatUser wechatUser = WechatUser.getWechatUserByMemberId(user.getMemberId());
                                if(wechatUser!=null){
                                    String remark = "如有疑问，请咨询"+config.getPhone()+"。";
                                    //获取活动
                                    if(rechargeOrder.getPromotion()==1){
                                        Map<String,Object> recharge = Promotion.getPromotion2ItemByMoney(rechargeOrder.getMoney());
                                        Promotion promotion = Promotion.getPromotionById(1);
                                        if(recharge!=null){
                                            remark = String.format("您已成功参加[%s]活动，%d小时后返现%.2f元。",
                                                    promotion.getSummary(),Integer.valueOf((String)recharge.get("返现时间")),
                                                    Double.valueOf((String)recharge.get("返现金额")));
                                        }else{
                                            remark = "您已成功参加["+promotion.getSummary()+"]活动。";
                                        }
                                    }else if(rechargeOrder.getPromotion()==2){
                                        Promotion promotion = Promotion.getPromotionById(2);
                                        JSONObject jsonObject = JSON.parseObject(promotion.getRule());
                                        remark = String.format("您已成功参加[%s]活动，本次消费%s元，赠送%s元，赠送金额下次可抵扣%s%%，赠送金额抵扣完后将获得%s元现金返现。",
                                                promotion.getSummary(),rechargeOrder.getMoney(),rechargeOrder.getMoney().multiply(new BigDecimal(jsonObject.getString("返现比例"))).divide(new BigDecimal(100.00),BigDecimal.ROUND_HALF_UP),
                                                jsonObject.getString("消费可抵单订比例"),rechargeOrder.getMoney());
                                    }else if(rechargeOrder.getPromotion()==3){
                                        Promotion promotion = Promotion.getPromotionById(3);
                                        JSONObject jsonObject = JSON.parseObject(promotion.getRule());
                                        remark = String.format("您已成功参加[%s]活动，获得赠送金额%s元，赠送金额下次可抵扣%s%%。",
                                                promotion.getSummary(),rechargeOrder.getMoney().multiply(new BigDecimal(jsonObject.getString("返现比例"))).divide(new BigDecimal(100.00),BigDecimal.ROUND_HALF_UP),
                                                jsonObject.getString("消费可抵单订比例"),rechargeOrder.getMoney());
                                    }
                                    String dataString = String.format("{\"first\": {\"value\":\"您已成功充值\",\"color\":\"#173177\"},\"keynote1\":{\"value\":\"%s\",\"color\":\"#173177\"},\"keynote2\": {\"value\":\"%s元 [实付%s元]\",\"color\":\"#173177\"},\"remark\":{\"value\":\"%s\",\"color\":\"#173177\"}}",
                                           DateUtil.formatDateTime(rechargeOrder.getAddTime()),rechargeOrder.getMoney(),rechargeOrder.getPayMoney(),remark);
                                    PayUtil.sendMessage(wechatUser.getOpenId(),config.getWxRechargeMessageId(),config.getWxAppId(),config.getWxAppSecret(),JSON.parseObject(dataString),getSession());
                                }
                                return;
                            }
                        }else{
                            renderText(ret);
                            return;
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        renderText("");
    }

    /**
     * 支付成功
     */
    public void paySuccess(){
        try{
            String prepayId = getPara("prepay_id","");
            RechargeOrder rechargeOrder = RechargeOrder.getRecordByPrepayId(prepayId);
            Map<String,Object> map = new HashMap<>();
            map.put("tip","["+(rechargeOrder==null?"--":rechargeOrder.getPayMoney().toString())+"元] 支付成功");
            map.put("content",rechargeOrder==null?"未知订单":rechargeOrder.getRemark());
            map.put("firstUrl","/recharge"+SystemConfig.sExt);
            map.put("firstTitle","返回");
            setAttr("info",map);
            render("Jump.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 支付失败
     */
    public void payFailed(){
        try{
            String prepayId = getPara("prepayId","");
            RechargeOrder rechargeOrder = RechargeOrder.getRecordByPrepayId(prepayId);
            Map<String,Object> map = new HashMap<>();
            map.put("tip","支付失败");
            map.put("content",rechargeOrder==null?"未知订单":rechargeOrder.getRemark());
            map.put("firstUrl","/recharge"+SystemConfig.sExt);
            map.put("firstTitle","返回");
            setAttr("info",map);
            render("Jump.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 继续支付
     */
    public void doRechargeAgain(){
        try{
            Integer id = getParaToInt(0,0);
            RechargeOrder rechargeOrder = RechargeOrder.getRecordById(id);
            if(rechargeOrder.getStatus()>1){
                Map<String,Object> map = new HashMap<>();
                map.put("tip","该订单已经支付");
                map.put("content",rechargeOrder==null?"未知订单":rechargeOrder.getRemark());
                map.put("firstUrl","/recharge"+SystemConfig.sExt);
                map.put("firstTitle","返回");
                setAttr("info",map);
                render("Jump.fmk");
            }else if(StrKit.isBlank(rechargeOrder.getPrepayId())){
                Map<String,Object> map = new HashMap<>();
                map.put("tip","预付码不存在，请重新创建订单");
                map.put("content",rechargeOrder==null?"未知订单":rechargeOrder.getRemark());
                map.put("firstUrl","/recharge"+SystemConfig.sExt);
                map.put("firstTitle","返回");
                setAttr("info",map);
                render("Jump.fmk");
            }else{
                Config config = Config.getConfig();
                String timestamp = String.valueOf(System.currentTimeMillis()/1000);
                SortedMap<String,Object> params2 = new TreeMap<>();
                params2.put("appId",config.getWxAppId());
                params2.put("timeStamp",timestamp);
                params2.put("nonceStr",Utils.createRandom(false,32));
                params2.put("package","prepay_id="+rechargeOrder.getPrepayId());
                params2.put("signType","MD5");
                params2.put("paySign",PayUtil.wechatPayCreateSign("utf-8",params2,config.getWxAppSecret()));
                Ret ret = new Ret();
                ret.set("code",JsonCodeType.CODE_SUCCESS)
                        .set("message","微信订单创建成功")
                        .set("data",params2);
                renderJson(ret);
            }
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 个人信息页
     */
    public void profile(){
        try{
            Map<String,Object> map = new HashMap<>();
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            map.put("username",user.getUsername());
            map.put("levelName",user.getLevelName());
            map.put("user", user);
            setAttr("info",map);
        }catch (Exception e){
            e.printStackTrace();
        }
        render("Profile.fmk");
    }

    /**
     * 修改个人头像
     */
    public void doModifyAvatar(){
        Map<String,Object> map = null;
        try{
            UploadFile file = getFile();
            map = new HashMap<>();

            if(file == null){
                throw new Exception("未获取到文件");
            }
            String ext = file.getFileName().substring(file.getFileName().lastIndexOf("."));
            if(!ext.equals(".jpg") && !ext.equals(".gif") && !ext.equals(".bmp") && !ext.equals(".jpeg") && !ext.equals(".png") && !ext.equals(".svg")){
                throw new Exception("图片后缀名错误");
            }
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            String newAvatarUrl = Common.pictureUploadPath+ File.separator+user.getMemberId()+"-"+System.currentTimeMillis()+ext;//加随机数为了避免缓存
            newAvatarUrl = newAvatarUrl.replace("\\","/");
            newAvatarUrl = newAvatarUrl.replace("//","/");
            String newAvatarFullFileName = PathKit.getWebRootPath() + newAvatarUrl;
            newAvatarFullFileName = newAvatarFullFileName.replace("\\","/");
            newAvatarFullFileName = newAvatarFullFileName.replace("//","/");
            Utils.zoomImage(file.getFile().getAbsolutePath(),newAvatarFullFileName,300);//缩放图片至300px

            //删除旧的头像？还是留着吧
            user.setAvatar(newAvatarUrl);
            if(!user.update()){
                throw new Exception("写入数据库失败");
            }
            file.getFile().delete();

            map.put("tip","操作成功");
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }
        setAttr("info",map);
        render("Jump.fmk");
    }

    /**
     * 修改姓名页面
     */
    public void modifyRealName(){
        Map<String,Object> map = null;
        try{
            map = new HashMap<>();
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            map.put("realName",user.getRealName());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("ProfileModifyRealName.fmk");
    }

    /**
     * 修改姓名
     */
    public void doModifyRealName(){
        Map<String,Object> map = null;
        try{
            String realName = getPara("realName");
            map = new HashMap<>();
            if(StrKit.isBlank(realName) || realName.length() >16) {
                throw new Exception("真实姓名长度不正确");
            }

            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));
            if(!realName.equals(user.getRealName())){
                user.setRealName(realName);
                if(!user.update()){
                    throw new Exception("写入数据库失败");
                }
            }

            map.put("tip","操作成功");
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }
        setAttr("info",map);
        render("Jump.fmk");
    }

    /**
     * 修改手机号页面
     */
    public void modifyTel(){
        Map<String,Object> map = null;
        try{
            map = new HashMap<>();
            User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
            map.put("phone",user.getPhone());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("ProfileModifyTel.fmk");
    }

    /**
     * 修改手机号
     */
    public void doModifyTel(){
        Map<String,Object> map = null;
        try{
            String phone = getPara("phone");
            if(!Utils.isPhone(phone)){
                throw new Exception("手机号码格式不正确");
            }
            map = new HashMap<>();
            User user = User.getRecordByMemberId(getSessionAttr(SESSION_USER_MEMBER_ID));

            if(!phone.equals(user.getPhone())){
                user.setPhone(phone);
                if(!user.update()){
                    throw new Exception("写入数据库失败");
                }
            }else{
                map.put("content","手机号相同，无需修改");
            }

            map.put("tip","操作成功");
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }
        setAttr("info",map);
        render("Jump.fmk");
    }

    /**
     * 修改性别
     */
    public void doModifySex(){
        Ret data = new Ret();
        data.set("status","error");
        data.set("content","处理失败");
        try{
            String sex = getPara("sex");
            if(StrKit.notBlank(sex) && (sex.equals("男") || sex.equals("女"))){
                boolean bSex = sex.equals("男");
                User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
                if(user.getSex() != bSex){
                    user.setSex(bSex);
                    if(!user.update()){
                        throw new Exception("写入数据库失败");
                    }
                }
                data.set("status","success");
                data.set("content","保存成功");
            }else{
                throw new Exception("参数错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            data.set("status","error");
            data.set("content","错误："+e.getMessage());
        }
        renderJson(data);
    }

    /**
     * 修改地址页面
     */
    public void modifyAddress(){
        Map<String,Object> map = null;
        try{
            map = new HashMap<>();
            User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
            map.put("address",user.getAddress());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("ProfileModifyAddress.fmk");
    }

    /**
     * 修改地址
     */
    public void doModifyAddress(){
        Map<String,Object> map = new HashMap<>();
        try{
            String address = getPara("address");
            if(StrKit.notBlank(address)){
                User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
                if(!user.getAddress().equals(address)){
                    user.setAddress(address);
                    if(!user.update()){
                        throw new Exception("写入数据库失败");
                    }
                }
                map.put("content","地址修改成功");
                map.put("tip","操作成功");
                map.put("firstUrl","/profile"+SystemConfig.sExt);
                map.put("firstTitle","返回");
            }else{
                throw new Exception("参数错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("content",e.getMessage());
            map.put("tip","操作成功");
            map.put("firstUrl","/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }
    }

    /**
     * 切换身份
     */
    public void doChangeToWorker(){
        try{
            User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
            if(!user.isWorker()){
                throw new Exception("您不是员工，不能切换");
            }
            setCookie(IndexController.COOKIE_CUR_ROLE,"worker",999999999);
            redirect("/worker"+SystemConfig.sExt);
        }catch (Exception e){
            e.printStackTrace();
            setAttr("content",e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     * 切换身份
     */
    public void doChangeToUser(){
        try{
            User user = User.getRecordByMemberId(getSessionAttr(IndexController.SESSION_USER_MEMBER_ID));
            if(!user.isWorker()){
                throw new Exception("您不是员工，不能切换");
            }
            setCookie(IndexController.COOKIE_CUR_ROLE,"user",999999999);
            redirect("/"+SystemConfig.sExt);
        }catch (Exception e){
            e.printStackTrace();
            setAttr("content",e.getMessage());
            render("ErrorPage.fmk");
        }
    }
}
