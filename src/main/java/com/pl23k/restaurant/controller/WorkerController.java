package com.pl23k.restaurant.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.*;
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
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import org.apache.log4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by PL23K on 2018-12-13.
 */
@Before(UserAuthInterceptor.class)
public class WorkerController extends Controller {
    private static Logger logger = Logger.getLogger(WorkerController.class);

    /**
     * 首页
     */
    //@Clear
    //@Before(GlobalInterceptor.class)
    public void index(){
        try{
            Worker worker =  Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));//Worker.getRecordByUsername("190737630");//

            /*/
            User user = User.getRecordByMemberId("A190737630");
            setSessionAttr(IndexController.SESSION_USER_NAME, user.getUsername());
            setSessionAttr(IndexController.SESSION_USER_MEMBER_ID,user.getMemberId());
            setSessionAttr(IndexController.SESSION_LOGIN_ERROR_TIMES,0);
            //cookie记录用户名
            setCookie(IndexController.COOKIE_USER_NMAE,user.getUsername(),999999999);
             //*/

            if(worker!=null){
                Map info = new HashMap();
                info.put("worker",worker);
                setAttr("info",info);
                render("WorkerIndex.fmk");
            }else{
                redirect("/worker/login"+SystemConfig.sExt);
            }
        }catch (Exception e){
            e.printStackTrace();
            redirect("/worker/login"+SystemConfig.sExt);
        }
    }

    @Clear
    @Before(GlobalInterceptor.class)
    public void login(){
        setAttr("content","微信登录失败，请重试");
        render("ErrorPage.fmk");
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
            renderQrCode(domain+"/register/"+getSessionAttr(IndexController.SESSION_USER_NAME)+SystemConfig.sExt+"#Worker"+ getSessionAttr(IndexController.SESSION_USER_NAME),300,300);
        }catch (Exception e){
            e.printStackTrace();
            renderError(500);
        }
    }

    /**
     * 查找用户
     */
    public void searchUser(){
        try{
            Map<String,Object> map = new HashMap<>();
            setAttr("info",map);
            render("SearchUser.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 显示会员信息
     */
    public void user(){
        try{
            String username = getPara("username","");
            User user = User.getRecordByUsername(username);
            Map<String,Object> map = new HashMap<>();
            BigDecimal returnMoney = RechargeOrder.getWaitMoneyByMemberId(user.getMemberId());
            if(returnMoney == null){
                returnMoney = BigDecimal.ZERO;
            }
            map.put("user",user);
            map.put("returnMoney",returnMoney);
            map.put("hasP1",RechargeOrder.hasP(1,user.getMemberId()));
            map.put("hasP2",RechargeOrder.hasP(2,user.getMemberId()));
            map.put("hasP3",RechargeOrder.hasP(3,user.getMemberId()));
            setAttr("info",map);
            render("WorkerUser.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     *  充值操作
     */
    public void recharge(){
        try{
            String username = getPara(0,"");
            User user = User.getRecordByUsername(username);
            Map<String,Object> map = new HashMap<>();
            map.put("user",user);
            map.put("rechargeList",Promotion.getPromotion2List());
            map.put("promotionList",Promotion.getPromotionList(true));
            setAttr("info",map);
            render("WorkerRecharge.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 处理充值
     */
    public void doRecharge(){
        try{
            UploadFile uploadFile = getFile("upfile");
            String targetUsername = getPara("targetUsername","");
            User targetUser = User.getRecordByUsername(targetUsername);
            Integer promotionId = getParaToInt("promotionId",0);
            BigDecimal money = new BigDecimal(getPara("money","0"));
            Ret picture = Utils.saveFile(uploadFile, Common.pictureUploadPath,false);
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            if(picture.getInt("code") == 0){
                throw new Exception("请上传照片");
            }
            if(targetUser == null){
                throw new Exception("目标用户不存在");
            }
            Config config = Config.getConfig();
            if(promotionId == 1){
                Map<String,Object> recharge  =  Promotion.getPromotion2ItemByMoney(money);
                if(recharge == null){
                    throw new Exception("活动一充值金额不正确，请充值对应档次金额");
                }
                //活动一
                RechargeOrder rechargeOrder = new RechargeOrder();
                rechargeOrder.setMemberId(targetUser.getMemberId())
                        .setPayType(0)
                        .setPayId("")
                        .setPrepayId("")
                        .setOrderId("ORDER"+Utils.createRandom(true,16))
                        .setStatus(3)
                        .setAddTime(new Date())
                        .setRemark(String.format("用户%s[%s]于[%s]充值[充值次日返现活动]%.2f元，%d小时后返现%.2f元。推荐员工：%s。",
                                targetUser.getRealName(),targetUser.getUsername(), DateUtil.formatDateTime(rechargeOrder.getAddTime()),
                                Double.valueOf((String)recharge.get("充值")),Integer.valueOf((String)recharge.get("返现时间")),
                                Double.valueOf((String)recharge.get("返现金额")),worker.getRealName()+"["+worker.getUsername()+"]"))
                        .setMoney(money)
                        .setPayMoney(money)
                        .setDiscountMoney(BigDecimal.ZERO)
                        .setDiscount(100)
                        .setPromotion(1)
                        .setRecommendType(3)
                        .setRecommendId(worker.getId())
                        .setShopId(worker.getShopId())
                        .setRoyaltyRate(worker.getRoyaltyRate())
                        .setRoyalty(money.multiply(new BigDecimal(worker.getRoyaltyRate())).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP))
                        .setReturnMoney(new BigDecimal((String)recharge.get("返现金额")))
                        .setIsPayPromotion(false)
                        .setEvidence(picture.getStr("picture"));
                if(rechargeOrder.save()){
                    //充值进账
                    targetUser.setMoney(targetUser.getMoney().add(money));
                    targetUser.update();

                    //通知用户购买成功
                    WechatUser wechatUser = WechatUser.getWechatUserByMemberId(targetUser.getMemberId());
                    if(wechatUser!=null){
                        String remark = "";
                        Promotion promotion = Promotion.getPromotionById(1);
                        if(recharge!=null){
                            remark = String.format("您已成功参加[%s]活动，%d小时后返现%.2f元。",
                                    promotion.getSummary(),Integer.valueOf((String)recharge.get("返现时间")),
                                    Double.valueOf((String)recharge.get("返现金额")));
                        }else{
                            remark = "您已成功参加["+promotion.getSummary()+"]活动。";
                        }
                        String dataString = String.format("{\"first\": {\"value\":\"您已成功充值\",\"color\":\"#173177\"},\"keynote1\":{\"value\":\"%s\",\"color\":\"#173177\"},\"keynote2\": {\"value\":\"%s元 [实付%s元]\",\"color\":\"#173177\"},\"remark\":{\"value\":\"%s\",\"color\":\"#173177\"}}",
                                DateUtil.formatDateTime(rechargeOrder.getAddTime()),rechargeOrder.getMoney(),rechargeOrder.getPayMoney(),remark);
                        PayUtil.sendMessage(wechatUser.getOpenId(),config.getWxRechargeMessageId(),config.getWxAppId(),config.getWxAppSecret(),JSON.parseObject(dataString),getSession());
                    }

                    Map<String,Object> map = new HashMap<>();
                    map.put("tip","["+rechargeOrder.getMoney().toString()+"元] 充值成功");
                    map.put("content",rechargeOrder.getRemark());
                    map.put("firstUrl","/worker/user"+SystemConfig.sExt+"?username="+targetUsername);
                    map.put("firstTitle","返回");
                    setAttr("info",map);
                    render("Jump.fmk");
                }else{
                    throw new Exception("充值保存失败");
                }
            }else if(promotionId == 2){
                //活动二
                if(money.compareTo(BigDecimal.ZERO) <=0){
                    throw new Exception("活动二充值金额不正确，需大于0");
                }
                if(targetUser.getCoin().compareTo(BigDecimal.ZERO) > 0){
                    throw new Exception("该用户还有赠送金额没用完，请先使用完赠送金额再可参加该活动");
                }
                //活动二
                Promotion promotion = Promotion.getPromotionById(2);
                JSONObject rule = JSON.parseObject(promotion.getRule());
                RechargeOrder rechargeOrder = new RechargeOrder();
                rechargeOrder.setMemberId(targetUser.getMemberId())
                        .setPayType(0)
                        .setPayId("")
                        .setPrepayId("")
                        .setOrderId("ORDER"+Utils.createRandom(true,16))
                        .setStatus(3)
                        .setAddTime(new Date())
                        .setRemark(String.format("用户%s[%s]于[%s]消费[消费多少赠多少活动]%.2f元，赠送金额%.2f元，每次消费可抵%d%%，推荐员工：%s。",
                                targetUser.getRealName(),targetUser.getUsername(), DateUtil.formatDateTime(rechargeOrder.getAddTime()),
                                money.doubleValue(),money.multiply(new BigDecimal(rule.getInteger("返现比例")).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP)),
                                rule.getInteger("消费可抵单订比例"),worker.getRealName()+"["+worker.getUsername()+"]"))
                        .setMoney(money)
                        .setPayMoney(money)
                        .setDiscountMoney(BigDecimal.ZERO)
                        .setDiscount(100)
                        .setPromotion(2)
                        .setRecommendType(3)
                        .setRecommendId(worker.getId())
                        .setShopId(worker.getShopId())
                        .setRoyaltyRate(worker.getRoyaltyRate())
                        .setRoyalty(money.multiply(new BigDecimal(worker.getRoyaltyRate())).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP))
                        .setReturnMoney(BigDecimal.ZERO)
                        .setIsPayPromotion(true)
                        .setEvidence(picture.getStr("picture"));
                if(rechargeOrder.save()){
                    //充值进账
                    targetUser.setCoin(targetUser.getCoin().add(money.multiply(new BigDecimal(rule.getInteger("返现比例")).divide(new BigDecimal(100)))));//因为money现金账户已被线下消耗掉了 所以只充抵券
                    targetUser.update();
                    //创建消费单
                    Consume consume = new Consume();
                    consume.setMemberId(targetUser.getMemberId())
                            .setWorkerId(worker.getId())
                            .setTotalMoney(money)
                            .setPayTotalMoney(money)
                            .setMoney(BigDecimal.ZERO)//因为是现金支付
                            .setCoin(BigDecimal.ZERO)
                            .setEvidence(picture.getStr("picture"))
                            .setAddTime(new Date())
                            .setBeforeMoney(targetUser.getMoney())
                            .setAfterMoney(targetUser.getMoney())
                            .setBeforeCoin(targetUser.getCoin())
                            .setAfterCoin(targetUser.getCoin())
                            .setStatus(0)
                            .setRejectAdmin(0)
                            .setDiscount(100)
                            .setRemark(String.format("用户%s[%s]于[%s]消费%.2f元[消耗充值余额0.00元 赠送余额0.00元][消费多少赠多少活动]，系统抵扣比例上限%d%%，操作员工：%s。",
                                    targetUser.getRealName(),targetUser.getUsername(), DateUtil.formatDateTime(consume.getAddTime()),
                                    money.doubleValue(),rule.getInteger("消费可抵单订比例"),worker.getRealName()+"["+worker.getUsername()+"]"));
                    consume.save();

                    //通知用户购买成功
                    WechatUser wechatUser = WechatUser.getWechatUserByMemberId(targetUser.getMemberId());
                    if(wechatUser!=null){
                        String remark = "";
                        remark = String.format("您已成功参加[%s]活动，本次消费%s元，赠送%s元，赠送金额下次可抵扣%s%%，赠送金额抵扣完后将获得%s元现金返现。",
                                promotion.getSummary(),rechargeOrder.getMoney(),rechargeOrder.getMoney().multiply(new BigDecimal(rule.getString("返现比例"))).divide(new BigDecimal(100.00),BigDecimal.ROUND_HALF_UP),
                                rule.getString("消费可抵单订比例"),rechargeOrder.getMoney());

                        String dataString = String.format("{\"first\": {\"value\":\"您已成功充值\",\"color\":\"#173177\"},\"keynote1\":{\"value\":\"%s\",\"color\":\"#173177\"},\"keynote2\": {\"value\":\"%s元 [实付%s元]\",\"color\":\"#173177\"},\"remark\":{\"value\":\"%s\",\"color\":\"#173177\"}}",
                                DateUtil.formatDateTime(rechargeOrder.getAddTime()),rechargeOrder.getMoney(),rechargeOrder.getPayMoney(),remark);
                        PayUtil.sendMessage(wechatUser.getOpenId(),config.getWxRechargeMessageId(),config.getWxAppId(),config.getWxAppSecret(),JSON.parseObject(dataString),getSession());
                    }

                    Map<String,Object> map = new HashMap<>();
                    map.put("tip","["+rechargeOrder.getMoney().toString()+"元] 充值成功");
                    map.put("content",rechargeOrder.getRemark());
                    map.put("firstUrl","/worker/user"+SystemConfig.sExt+"?username="+targetUsername);
                    map.put("firstTitle","返回");
                    setAttr("info",map);
                    render("Jump.fmk");
                }else{
                    throw new Exception("充值保存失败");
                }
            }else if(promotionId == 3){
                //活动三
                if(money.compareTo(BigDecimal.ZERO) <=0){
                    throw new Exception("活动三充值金额不正确，需大于0");
                }

                Promotion promotion = Promotion.getPromotionById(3);
                JSONObject rule = JSON.parseObject(promotion.getRule());
                RechargeOrder rechargeOrder = new RechargeOrder();
                rechargeOrder.setMemberId(targetUser.getMemberId())
                        .setPayType(0)
                        .setPayId("")
                        .setPrepayId("")
                        .setOrderId("ORDER"+Utils.createRandom(true,16))
                        .setStatus(3)
                        .setAddTime(new Date())
                        .setRemark(String.format("用户%s[%s]于[%s]充值[商城员工赠送活动]%.2f元[未真实到账]，赠送金额%.2f元，每次消费可抵%d%%。推荐员工：%s。",
                                targetUser.getRealName(),targetUser.getUsername(), DateUtil.formatDateTime(rechargeOrder.getAddTime()),
                                money.doubleValue(),money.multiply(new BigDecimal(rule.getInteger("返现比例")).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP)),
                                rule.getInteger("消费可抵单订比例"),worker.getRealName()+"["+worker.getUsername()+"]"))
                        .setMoney(money)
                        .setPayMoney(BigDecimal.ZERO)
                        .setDiscountMoney(BigDecimal.ZERO)
                        .setDiscount(100)
                        .setPromotion(3)
                        .setRecommendType(3)
                        .setRecommendId(worker.getId())
                        .setShopId(worker.getShopId())
                        .setRoyaltyRate(0)//无提成
                        .setRoyalty(BigDecimal.ZERO)
                        .setReturnMoney(BigDecimal.ZERO)
                        .setIsPayPromotion(true)
                        .setEvidence(picture.getStr("picture"));
                if(rechargeOrder.save()){
                    //充值进账
                    targetUser.setCoin(targetUser.getCoin().add(money.multiply(new BigDecimal(rule.getInteger("返现比例")).divide(new BigDecimal(100)))));//因为现金未真实到账 所以只充抵券
                    targetUser.update();

                    //通知用户购买成功
                    WechatUser wechatUser = WechatUser.getWechatUserByMemberId(targetUser.getMemberId());
                    if(wechatUser!=null){
                        String remark = "";
                        remark = String.format("您已成功参加[%s]活动，获得赠送金额%s元，赠送金额下次可抵扣%s%%。",
                                promotion.getSummary(),rechargeOrder.getMoney().multiply(new BigDecimal(rule.getString("返现比例"))).divide(new BigDecimal(100.00),BigDecimal.ROUND_HALF_UP),
                                rule.getString("消费可抵单订比例"),rechargeOrder.getMoney());
                        String dataString = String.format("{\"first\": {\"value\":\"您已成功充值\",\"color\":\"#173177\"},\"keynote1\":{\"value\":\"%s\",\"color\":\"#173177\"},\"keynote2\": {\"value\":\"%s元 [实付%s元]\",\"color\":\"#173177\"},\"remark\":{\"value\":\"%s\",\"color\":\"#173177\"}}",
                                DateUtil.formatDateTime(rechargeOrder.getAddTime()),rechargeOrder.getMoney(),rechargeOrder.getPayMoney(),remark);
                        PayUtil.sendMessage(wechatUser.getOpenId(),config.getWxRechargeMessageId(),config.getWxAppId(),config.getWxAppSecret(),JSON.parseObject(dataString),getSession());
                    }

                    Map<String,Object> map = new HashMap<>();
                    map.put("tip","["+rechargeOrder.getMoney().toString()+"元] 充值成功");
                    map.put("content",rechargeOrder.getRemark());
                    map.put("firstUrl","/worker/user"+SystemConfig.sExt+"?username="+targetUsername);
                    map.put("firstTitle","返回");
                    setAttr("info",map);
                    render("Jump.fmk");
                }else{
                    throw new Exception("充值保存失败");
                }

            }else{
                //错误
                throw new Exception("活动不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
            setAttr("content",e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     *  充值操作
     */
    public void consume(){
        try{
            String username = getPara(0,"");
            User user = User.getRecordByUsername(username);
            Map<String,Object> map = new HashMap<>();
            Promotion promotion = Promotion.getPromotionById(2);
            JSONObject rule = JSON.parseObject(promotion.getRule());
            map.put("rate",rule.get("消费可抵单订比例"));
            map.put("user",user);
            setAttr("info",map);
            render("WorkerConsume.fmk");
        }catch (Exception e){
            e.printStackTrace();
            setAttr("content",e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     * 处理消费
     */
    public void doConsume(){
        try{
            UploadFile uploadFile = getFile("upfile");
            Ret picture = Utils.saveFile(uploadFile, Common.pictureUploadPath,false);
            String targetUsername = getPara("targetUsername","");
            User targetUser = User.getRecordByUsername(targetUsername);
            BigDecimal totalMoney = new BigDecimal(getPara("totalMoney","0"));
            BigDecimal money = new BigDecimal(getPara("money","0"));
            BigDecimal coin = new BigDecimal(getPara("coin","0"));
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            Promotion promotion = Promotion.getPromotionById(2);
            JSONObject rule = JSON.parseObject(promotion.getRule());

            if(targetUser == null){
                throw new Exception("用户不存在");
            }
            if(totalMoney.compareTo(money.add(coin)) != 0){
                //throw new Exception("现金+抵券 不等于 总金额，请重新输入");
            }
            if(money.compareTo(BigDecimal.ZERO) >0 && targetUser.getMoney().compareTo(money) < 0){
                throw new Exception("现金账户金额不足");
            }
            if(coin.compareTo(BigDecimal.ZERO) >0 && targetUser.getCoin().compareTo(coin) < 0){
                throw new Exception("赠送金额不足");
            }
            BigDecimal rate = new BigDecimal(rule.getString("消费可抵单订比例"));
            BigDecimal exCoin = totalMoney.multiply(rate).divide(new BigDecimal(100),BigDecimal.ROUND_HALF_UP);
            if(exCoin.compareTo(coin) < 0){
                throw new Exception("本订单最多可抵扣"+rate+"% "+exCoin+"元");
            }
            if(picture.getInt("code") != 1){
                throw new Exception("请拍照留证");
            }

            Consume consume = new Consume();
            consume.setMemberId(targetUser.getMemberId())
                    .setWorkerId(worker.getId())
                    .setTotalMoney(totalMoney)
                    .setPayTotalMoney(totalMoney)
                    .setMoney(money)
                    .setCoin(coin)
                    .setEvidence(picture.getStr("picture"))
                    .setAddTime(new Date())
                    .setBeforeMoney(targetUser.getMoney())
                    .setAfterMoney(targetUser.getMoney().subtract(money))
                    .setBeforeCoin(targetUser.getCoin())
                    .setAfterCoin(targetUser.getCoin().subtract(coin))
                    .setStatus(0)
                    .setRejectAdmin(0)
                    .setDiscount(100)
                    .setRemark(String.format("用户%s[%s]于[%s]消费%.2f元[消耗充值余额%.2f元 赠送金额%.2f元]，系统抵扣比例上限%d%%，操作员工：%s。",
                            targetUser.getRealName(),targetUser.getUsername(), DateUtil.formatDateTime(consume.getAddTime()),
                            totalMoney.doubleValue(),money.doubleValue(),coin.doubleValue(),rate.intValue(),worker.getRealName()+"["+worker.getUsername()+"]"));
            consume.save();
            //扣钱
            targetUser.setMoney(targetUser.getMoney().subtract(money))
                    .setCoin(targetUser.getCoin().subtract(coin))
                    .update();
            //发送通知
            Config config = Config.getConfig();
            WechatUser wechatUser = WechatUser.getWechatUserByMemberId(targetUser.getMemberId());
            if(wechatUser!=null){
                Shop shop = Shop.getShopById(worker.getShopId());
                String dataString = String.format("{\"pay\": {\"value\":\"%s元 [充值扣%s元 赠送扣%s元 其他%s元]\",\"color\":\"#173177\"},\"address\":{\"value\":\"%s\",\"color\":\"#173177\"},\"time\": {\"value\":\"%s\",\"color\":\"#173177\"},\"remark\":{\"value\":\"%s\",\"color\":\"#173177\"}}",
                        consume.getTotalMoney().toString(),consume.getMoney().toString(),consume.getCoin().toString(),
                        consume.getTotalMoney().subtract(consume.getMoney()).subtract(consume.getCoin()).toString(),shop==null?"":shop.getName(),DateUtil.formatDateTime(consume.getAddTime()),("如有疑问，请咨询"+config.getPhone()+"。"));
                PayUtil.sendMessage(wechatUser.getOpenId(),config.getWxTemplateId(),config.getWxAppId(),config.getWxAppSecret(),JSON.parseObject(dataString),getSession());
            }
            Map<String,Object> map = new HashMap<>();
            map.put("tip","["+consume.getTotalMoney().toString()+"元] 消费成功");
            map.put("content",consume.getRemark());
            map.put("firstUrl","/worker/user"+SystemConfig.sExt+"?username="+targetUsername);
            map.put("firstTitle","返回");
            setAttr("info",map);
            render("Jump.fmk");
        }catch (Exception e){
            e.printStackTrace();
            setAttr("content",e.getMessage());
            render("ErrorPage.fmk");
        }
    }

    /**
     * 消费操作列表
     */
    public void consumeList(){
        try {
            Integer pageNumber = getParaToInt(0);
            if(pageNumber == null){
                pageNumber = getParaToInt("pageNumber",1);
            }
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            Page<Consume> pages = Consume.getRecordByPageWorkerId(pageNumber,worker.getId());
            Map<String,Object> map = new HashMap<>();
            map.put("consumeList",pages);
            map.put("todayConsume",Consume.getConsumeTodayTotalByWorkerId(worker.getId()));
            map.put("totalConsume",Consume.getConsumeTotalByWorkerId(worker.getId()));
            setAttr("info",map);
            render("WorkerConsumeList.fmk");
        }catch (Exception e){
            e.printStackTrace();
            render("ErrorPage.fmk");
        }
    }

    /**
     * 充值操作列表
     */
    public void rechargeList(){
        try {
            Integer pageNumber = getParaToInt(0);
            if(pageNumber == null){
                pageNumber = getParaToInt("pageNumber",1);
            }
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            Page<RechargeOrder> pages = RechargeOrder.getRecordByPageWorkerId(pageNumber,worker.getId());
            Map<String,Object> map = new HashMap<>();
            map.put("rechargeList",pages);
            map.put("todayRecharge",RechargeOrder.getRechargeTodayTotalByWorkerId(worker.getId()));
            map.put("totalRecharge",RechargeOrder.getRechargeTotalByWorkerId(worker.getId()));
            map.put("totalRoyalty",RechargeOrder.getTotalRoyaltyByWorkerId(worker.getId()));
            setAttr("info",map);
            render("WorkerRechargeList.fmk");
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            map.put("username",worker.getUsername());
            map.put("worker", worker);
            setAttr("info",map);
        }catch (Exception e){
            e.printStackTrace();
        }
        render("WorkerProfile.fmk");
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            String newAvatarUrl = Common.pictureUploadPath+ File.separator+worker.getUsername()+"-"+System.currentTimeMillis()+ext;//加随机数为了避免缓存
            newAvatarUrl = newAvatarUrl.replace("\\","/");
            newAvatarUrl = newAvatarUrl.replace("//","/");
            String newAvatarFullFileName = PathKit.getWebRootPath() + newAvatarUrl;
            newAvatarFullFileName = newAvatarFullFileName.replace("\\","/");
            newAvatarFullFileName = newAvatarFullFileName.replace("//","/");
            Utils.zoomImage(file.getFile().getAbsolutePath(),newAvatarFullFileName,300);//缩放图片至300px

            //删除旧的头像？还是留着吧
            worker.setAvatar(newAvatarUrl);
            if(!worker.update()){
                throw new Exception("写入数据库失败");
            }
            file.getFile().delete();

            map.put("tip","操作成功");
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            map.put("realName",worker.getRealName());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("WorkerProfileModifyRealName.fmk");
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

            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            if(!realName.equals(worker.getRealName())){
                worker.setRealName(realName);
                if(!worker.update()){
                    throw new Exception("写入数据库失败");
                }
            }

            map.put("tip","操作成功");
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            map.put("phone",worker.getPhone());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("WorkerProfileModifyTel.fmk");
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));

            if(!phone.equals(worker.getPhone())){
                worker.setPhone(phone);
                if(!worker.update()){
                    throw new Exception("写入数据库失败");
                }
            }else{
                map.put("content","手机号相同，无需修改");
            }

            map.put("tip","操作成功");
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }catch (Exception e){
            e.printStackTrace();
            map = new HashMap<>();
            map.put("tip","操作失败");
            map.put("content","处理异常："+e.getMessage());
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
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
                Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
                if(worker.getSex() != bSex){
                    worker.setSex(bSex);
                    if(!worker.update()){
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
            Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
            map.put("address",worker.getAddress());
        }catch (Exception e){
            e.printStackTrace();
        }
        setAttr("info",map);
        render("WorkerProfileModifyAddress.fmk");
    }

    /**
     * 修改地址
     */
    public void doModifyAddress(){
        Map<String,Object> map = new HashMap<>();
        try{
            String address = getPara("address");
            if(StrKit.notBlank(address)){
                Worker worker = Worker.getRecordByUsername(getSessionAttr(IndexController.SESSION_USER_NAME));
                if(!worker.getAddress().equals(address)){
                    worker.setAddress(address);
                    if(!worker.update()){
                        throw new Exception("写入数据库失败");
                    }
                }
                map.put("content","地址修改成功");
                map.put("tip","操作成功");
                map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
                map.put("firstTitle","返回");
            }else{
                throw new Exception("参数错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            map.put("content",e.getMessage());
            map.put("tip","操作成功");
            map.put("firstUrl","/worker/profile"+SystemConfig.sExt);
            map.put("firstTitle","返回");
        }
    }
}
