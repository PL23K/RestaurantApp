package com.pl23k.restaurant.common;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.ITask;
import com.pl23k.restaurant.model.Promotion;
import com.pl23k.restaurant.model.RechargeOrder;
import com.pl23k.restaurant.model.User;
import com.pl23k.restaurant.utils.DatabaseTool;
import com.pl23k.restaurant.utils.DateUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by HelloWorld on 2019-07-16.
 */
public class ReturnTask implements ITask {
    public static boolean bDealingReturnTask = false;
    public static Logger logger = Logger.getLogger(ReturnTask.class);

    @Override
    public void stop() {
        logger.info("-----Return task stopped.------");
    }

    @Override
    public void run() {
        logger.info("-----Return task start.------");
        bDealingReturnTask = true;

        //1、获取未返现记录
        logger.info("-----0.1 备份会员数据库.------");
        List<RechargeOrder> rechargeOrders = RechargeOrder.getUnReturnRechargeOrder();
        if(rechargeOrders!=null){
            for(RechargeOrder rechargeOrder:rechargeOrders){
                if(rechargeOrder.getReturnMoney().compareTo(BigDecimal.ZERO) > 0){
                    try{
                        String remark = rechargeOrder.getRemark();
                        remark = remark.substring(remark.indexOf("元，")+2);
                        remark = remark.substring(0,remark.indexOf("小时"));
                        Integer hour = Integer.valueOf(remark);
                        Calendar old = Calendar.getInstance();
                        old.setTime(rechargeOrder.getAddTime());
                        old.add(Calendar.HOUR,hour);
                        Calendar now = Calendar.getInstance();
                        now.setTime(new Date());
                        if(old.before(now)){
                            //发放
                            User user = User.getRecordByMemberId(rechargeOrder.getMemberId());
                            user.setMoney(user.getMoney().add(rechargeOrder.getReturnMoney()))
                                    .update();
                            rechargeOrder.setIsPayPromotion(true)
                                    .update();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    rechargeOrder.setIsPayPromotion(true)
                            .update();
                }
            }
        }


        bDealingReturnTask = false;
    }
}
