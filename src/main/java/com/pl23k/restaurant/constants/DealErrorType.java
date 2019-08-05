package com.pl23k.restaurant.constants;

/**
 * Created by HelloWorld on 2017/7/28.
 */
public class DealErrorType {

    //通用成功
    public static final int COMMON_ERROR_SUCCESS = 1000;
    //未知错误
    public static final int COMMON_ERROR_UNKNOW = 1001;
    //通用失败
    public static final int COMMON_ERROR_FAILED = 1002;
    //通用用户数据错误
    public static final int COMMON_ERROR_USER_INFO_ERROR = 1003;
    //通用操作数据错误
    public static final int COMMON_ERROR_DATA_ERROR = 1004;
    //通用用户被冻结
    public static final int COMMON_ERROR_USER_FROZEN = 1005;
    //通用用户未激活
    public static final int COMMON_ERROR_USER_UNACTIVE = 1006;
    //通用用户余额错误
    public static final int COMMON_ERROR_COIN_ERROR = 1007;
    //通用用户余额不足
    public static final int COMMON_ERROR_LAKE_COIN = 1008;
    //通用无权限
    public static final int COMMON_ERROR_NO_PERMISSION = 1009;


    //管理员
    public static final int ADMIN_USERNAME_TOO_SHORT = 2001;
    public static final int ADMIN_PASSWORD_TOO_SHORT = 2002;

    //用户
    public static final int USER_USERNAME_TOO_SHORT = 3001;
    public static final int USER_PASSWORD_TOO_SHORT = 3002;
    public static final int USER_USERNAME_ALREADY_HAVE = 3003;

}
