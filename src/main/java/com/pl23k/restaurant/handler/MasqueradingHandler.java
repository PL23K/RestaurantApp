package com.pl23k.restaurant.handler;

import com.jfinal.handler.Handler;
import com.jfinal.kit.StrKit;
import com.pl23k.restaurant.common.SystemConfig;

/**
 * Created by HelloWorld on 2017/12/14.
 */
public class MasqueradingHandler extends Handler {

    @Override
    public void handle(String target, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, boolean[] isHandled) {

        //伪装后缀
        if(StrKit.isBlank(SystemConfig.sExt) || target.contains("upload") || target.contains("ueditor") || target.contains("/api")){
            next.handle(target, request, response, isHandled);
        }else{
            if (target.contains(SystemConfig.sExt)) {//有缺陷，如果网址里有参数包含这个后缀怎么办
                target = target.replace(SystemConfig.sExt,"");
                next.handle(target, request, response, isHandled);
            }else if(target.equals("/")){
                next.handle(target, request, response, isHandled);
            }
        }
    }
}
