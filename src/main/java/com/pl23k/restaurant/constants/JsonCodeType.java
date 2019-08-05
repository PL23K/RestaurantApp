package com.pl23k.restaurant.constants;

/**
 * Created by HelloWorld on 2017/7/28.
 */
public class JsonCodeType {

    /**
     * 通用
     */
    //成功
    public static final int CODE_SUCCESS = 0;
    //参数错误
    public static final int CODE_DATA_ERROR = 1;
    //内部错误
    public static final int CODE_ERROR = 2;
    //用户信息验证失败
    public static final int CODE_VERIFY_ERROR = 3;
    //操作失败
    public static final int CODE_FAILED = 4;
    //目标不存在
    public static final int CODE_NO_TARGET = 5;
    //没有权限
    public static final int CODE_NO_PERMISSION = 6;


    /**
     * 支付
     */
    //	已支付
    public static final int CODE_PAY_ALREADY_PAID = 110101;
}
