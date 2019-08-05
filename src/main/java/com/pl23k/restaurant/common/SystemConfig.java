package com.pl23k.restaurant.common;

import com.jfinal.config.*;
import com.jfinal.core.Const;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.cron4j.Cron4jPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.pl23k.restaurant.controller.AdminController;
import com.pl23k.restaurant.controller.IndexController;
import com.pl23k.restaurant.controller.ShopController;
import com.pl23k.restaurant.controller.WorkerController;
import com.pl23k.restaurant.interceptor.GlobalInterceptor;
import com.pl23k.restaurant.model._MappingKit;
import com.pl23k.restaurant.utils.IPLocation.Locator;
import com.pl23k.restaurant.utils.Utils;
import com.pl23k.restaurant.handler.MasqueradingHandler;
import org.apache.log4j.Logger;

/**
 * Created by PL23K on 2018-12-03.
 */
public class SystemConfig extends JFinalConfig{

    public static Prop prop;
    private static Logger logger = Logger.getLogger(SystemConfig.class);

    public static String sAdminAddr = "admin"; //管理后台的登录地址
    public static String sExt = ""; //模糊后缀

    /**
     * 先加载开发环境配置，然后尝试加载生产环境配置，生产环境配置不存在时不会抛异常
     * 在生产环境部署时后动创建 demo-config-pro.txt，添加的配置项可以覆盖掉
     * demo-config-dev.txt 中的配置项
     */
    static void loadConfig() {
        if (prop == null) {
            prop = PropKit.use("system_config_dev.txt").appendIfExists("system_config_release.txt");
        }
    }


    @Override
    public void configConstant(Constants constants) {
        loadConfig();
        sAdminAddr = prop.get("adminPath");//管理后台的登录地址
        sExt = prop.get("ext");
        if("none".equalsIgnoreCase(sExt)){
            sExt = "";
        }
        constants.setDevMode(prop.getBoolean("devMode", false));
        constants.setViewType(ViewType.FREE_MARKER);
        constants.setFreeMarkerTemplateUpdateDelay(60);
        //设置IP定位器
        Locator.init(Utils.CatPath(PathKit.getRootClassPath(),prop.get("ipData")));//使用方法：Locator.getFullAddr("180.114.117.228");

        constants.setMaxPostSize(50* Const.DEFAULT_MAX_POST_SIZE);

        logger.info("System started.");
    }

    @Override
    public void configRoute(Routes routes) {
        routes.add("/", IndexController.class ,"/view");	// 第三个参数为该Controller的视图存放路径
        routes.add("/"+sAdminAddr, AdminController.class,"/admin" );
        routes.add("/sm", ShopController.class ,"/shop");//shopmanager
        routes.add("/worker", WorkerController.class ,"/view");//shopmanager
    }

    @Override
    public void configEngine(Engine engine) {

    }

    public static DruidPlugin createDruidPlugin() {
        loadConfig();
        return new DruidPlugin(prop.get("jdbcUrl"), prop.get("user"), prop.get("password").trim());
    }

    @Override
    public void configPlugin(Plugins plugins) {

        // 配置 druid 数据库连接池插件
        DruidPlugin druidPlugin = new DruidPlugin(prop.get("jdbcUrl"), prop.get("user"), prop.get("password").trim());
        plugins.add(druidPlugin);

        // 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        // 所有映射在 MappingKit 中自动化搞定
        _MappingKit.mapping(arp);
        plugins.add(arp);

        //定时任务插件
        Cron4jPlugin cp = new Cron4jPlugin("cron4jConfig.txt");
        plugins.add(cp);

        //获取配置数据

    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new GlobalInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new MasqueradingHandler());
    }
}