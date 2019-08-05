package com.pl23k.restaurant.controller;

import com.jfinal.core.Controller;

/**
 * Created by lucias
 */
public class InfoSkip {

    /**
     * 消息类型字典
     */
    public enum INFO_TYPE{
        ERROR,
        SUCCESS
    }

    /**
     * 页面跳转信息
     *
     * @param type 信息类型  1成功 0失败
     * @param message   信息
     * @param url 跳转的页面
     * @param name 页面的名称
     * @param second 跳转延时的时间
     * @param c 控制器
     */
    public static void adminInfoSkip(INFO_TYPE type,String message,String url,String name,int second,Controller c){

        c.setAttr("type", INFO_TYPE.SUCCESS == type? 1:0);
        c.setAttr("pageLink",url);
        c.setAttr("pageName",name);
        c.setAttr("message",message);
        c.setAttr("delay",second);

        c.render("/admin/InfoSkip.fmk");

    }
}
