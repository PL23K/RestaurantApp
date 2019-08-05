package com.pl23k.restaurant.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by HelloWorld on 2019-07-24.
 */
public class Test {

    public static void main(String[] args){
        String remark = "用户周志华[190722228]于[2019-07-24 11:13:18]充值返现活动500.00元，24小时后返现200.00元。推荐员工：呵呵[190737630]。";
        remark = remark.substring(remark.indexOf("元，")+2);
        remark = remark.substring(0,remark.indexOf("小时"));
        Integer hour = 24;
        Calendar old = Calendar.getInstance();
        old.setTime(DateUtil.formatStringTime("2019-07-24 11:13:18"));
        old.add(Calendar.HOUR,hour);
        System.out.println(DateUtil.formatDateTime(old.getTime()));
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        if(old.before(now)){
            System.out.println("发送");
        }else{
            System.out.println("不发送");
        }
    }
}
