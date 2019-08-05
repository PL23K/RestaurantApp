package com.pl23k.restaurant.handler;

import com.jfinal.handler.Handler;

/**
 * Created by HelloWorld on 2019-03-27.
 */
public class GlobalHandler extends Handler {
    @Override
    public void handle(String target, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, boolean[] isHandled) {

        next.handle(target,request,response,isHandled);
    }
}
