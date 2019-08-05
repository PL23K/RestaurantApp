package com.pl23k.restaurant.common;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.cron4j.ITask;
import com.pl23k.restaurant.utils.DatabaseTool;
import com.pl23k.restaurant.utils.DateUtil;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by HelloWorld on 2019-07-16.
 */
public class DailyTask implements ITask {
    public static boolean bDealingDailyTask = false;
    public static Logger logger = Logger.getLogger(DailyTask.class);

    @Override
    public void stop() {
        logger.info("-----Daily task stopped.------");
    }

    @Override
    public void run() {
        logger.info("-----Daily task start.------");
        bDealingDailyTask = true;
        try {
            Thread.sleep(1000);    //延迟1分钟，等待系统正常工作结束
        } catch (Exception e) {
            e.printStackTrace();
        }
        //1、备份数据库
        logger.info("-----0.1 备份会员数据库.------");
        String dbFile = PropKit.get("dbBackPath") + File.separator + "db_pl_restaurant-" + DateUtil.formatDateTime(new Date()) + ".db";
        DatabaseTool.backup(PropKit.get("mysqlPath").replace("+", " "), PropKit.get("mysqlIp"), PropKit.get("mysqlPort"), PropKit.get("user"),
                PropKit.get("password"), PropKit.get("database"), dbFile);
        try {
            Thread.sleep(1000);    //延迟1秒，再发出备份请求
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean ret;
        //2、日结

        bDealingDailyTask = false;
    }
}
