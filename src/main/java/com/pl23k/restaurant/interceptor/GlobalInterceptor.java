package com.pl23k.restaurant.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.pl23k.restaurant.common.SystemConfig;
import com.pl23k.restaurant.model.Config;
import com.pl23k.restaurant.utils.Utils;
import org.apache.log4j.Logger;

/**
 * Created by PL23K on 2018-12-20.
 */
public class GlobalInterceptor implements Interceptor {
    static Logger logger = Logger.getLogger("GlobalActionInterceptor");// 一般为当前的类名

    public void intercept(Invocation inv) {

        Controller controller = inv.getController();
        controller.getResponse().addHeader("Access-Control-Allow-Origin", "*");
        controller.setAttr("ad", SystemConfig.sAdminAddr);
        controller.setAttr("ext", "");

        Config config = Config.getConfigById(1);//获取配置
        controller.setAttr("config", config);
        controller.setAttr("Utils", new Utils());
        inv.invoke();
    }
}
