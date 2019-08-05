package com.pl23k.restaurant.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.pl23k.restaurant.common.SystemConfig;
import com.pl23k.restaurant.controller.IndexController;
import com.pl23k.restaurant.model.Config;
import com.pl23k.restaurant.model.User;
import com.pl23k.restaurant.utils.Utils;

/**
 * Created by HelloWorld on 2017/7/13.
 */
public class UserAuthInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        Controller controller = invocation.getController();

        //invocation.invoke();
        //*/
        //判断是否登录
        String loginUser = controller.getSessionAttr(IndexController.SESSION_USER_NAME);
        if (StrKit.notBlank(loginUser)) {//&& loginUser.canVisit(invocation.getActionKey())){
            invocation.invoke();
        }else{
            //判断是否微信客服端
            if(Utils.isWechat(invocation.getController().getRequest())){
                Config config = Config.getConfig();
                controller.redirect(config.getWxLoginUrl());//重新登录
            }else{
                if (invocation.getActionKey().equals("/") || invocation.getActionKey().equals("/logout")) {
                    controller.redirect("/login"+ SystemConfig.sExt);
                }else{
                    controller.redirect("/login"+ SystemConfig.sExt);
                    /*
                    controller.setAttr("status","failure");
                    controller.setAttr("content","<div class=\"alert alert-danger alert-middle\" >\n" +
                            "        <span class=\"entypo-attention\"></span>\n" +
                            "        <strong>登录信息失效!</strong>&nbsp;&nbsp;请重新 <a href='/login.asp' target='_top'>登录</a>。\n" +
                            "    </div>");
                    controller.renderJson();
                    */
                }
            }
        }
         //*/
    }
}
