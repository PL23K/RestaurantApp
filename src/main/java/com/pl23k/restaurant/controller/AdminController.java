package com.pl23k.restaurant.controller;

import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.DbKit;
import com.jfinal.upload.UploadFile;
import com.pl23k.restaurant.common.HeydayCaptchaRender;
import com.pl23k.restaurant.common.SystemConfig;
import com.pl23k.restaurant.constants.Common;
import com.pl23k.restaurant.constants.DealErrorType;
import com.pl23k.restaurant.interceptor.AdminInterceptor;
import com.pl23k.restaurant.model.*;
import com.pl23k.restaurant.service.AdminDealService;
import com.pl23k.restaurant.service.AdminViewService;
import com.pl23k.restaurant.utils.DESUtil;
import com.pl23k.restaurant.utils.IPLocation.IPUtils;
import com.pl23k.restaurant.utils.Utils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URLDecoder;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

/**
 * Created by PL23K on 2018-12-13.
 */
@Before(AdminInterceptor.class)
public class AdminController extends Controller {
    private static Logger logger = Logger.getLogger(AdminController.class);

    public static String SESSION_ADMIN_ID = "SESSION_ADMIN_ID";
    public static String SESSION_ADMIN_USERNAME = "SESSION_ADMIN_USERNAME";
    public static String SESSION_ADMIN_REAL_NAME = "SESSION_ADMIN_REAL_NAME";

    public void index(){
        try{
            render("Index.fmk");
        }catch (Exception e){
            renderError(500);
        }
    }

    @Clear(AdminInterceptor.class)
    public void login(){
        try{
            render("Login.fmk");
        }catch (Exception e){
            e.printStackTrace();
            renderError(500);
        }
    }

    @Clear(AdminInterceptor.class)
    public void doLogin(){
        try{
            String username = getPara("username","");
            String password = getPara("password","");
            Boolean validate = validateCaptcha("captcha");
            if(!validate){
                throw new Exception("验证码输入错误");
            }
            username = URLDecoder.decode(username, "UTF-8");
            Admin admin = Admin.getAdminByUsername(username);
            //1、验证密码
            if (admin==null || !DESUtil.decryStr(admin.getPassword()).equals(password)) {
                throw new Exception("账号密码错误");
           }

            setAttr("code", "success");
            //添加Session
            setSessionAttr(SESSION_ADMIN_ID, admin.getId());
            setSessionAttr(SESSION_ADMIN_USERNAME, admin.getUsername());
            setSessionAttr(SESSION_ADMIN_REAL_NAME, admin.getRealName());
            //添加cookie
            setCookie("adminUsername",username,999999999);
            //做记录
            String ip = IPUtils.getIp(getRequest());
            AdminLoginLog.addLoginLog(admin,ip);
        }catch (Exception e){
            renderError(500);
            setAttr("code", "error");
            setAttr("message", e.getMessage());
        }
        renderJson();
    }
    /*
    @Clear(AdminInterceptor.class)
    public void adminEnter(){
        try{
            if(true) {
                String username = "admin";
                Admin admin = Admin.getAdminByUsername(username);
                //添加Session
                setSessionAttr(SESSION_ADMIN_ID, admin.getId());
                setSessionAttr(SESSION_ADMIN_USERNAME, admin.getUsername());
                setSessionAttr(SESSION_ADMIN_REAL_NAME, admin.getRealName());
                //添加cookie
                setCookie("adminUsername", username, 999999999);
                redirect("/"+ SystemConfig.sAdminAddr);
            }else{
                renderText("登录错误");
            }
        }catch (Exception e){
            setAttr("code", "error");
            setAttr("message", e.getMessage());
            renderText(e.getMessage());
        }
    }
    */

    /**
     * 获取验证码
     */
    @Clear
    public void captcha(){
        render(new HeydayCaptchaRender());
    }

    public void doLogout(){
        getSession().invalidate();
        redirect("/"+SystemConfig.sAdminAddr);
    }

    /**
     * 上传图片接口
     */
    public void doPictureUpload(){
        Ret data = new Ret();
        data.set("message","")
                .set("code",0)
                .set("status","error");
        try{
            UploadFile uploadFile = getFile("file");

            //保存图片
            String picture;
            String pictureThumb;
            String ext = uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf("."));
            long  time = System.currentTimeMillis();
            picture = Common.pictureUploadPath+ File.separator+"picture-"+time+ext;//加随机数为了避免缓存
            pictureThumb = Common.pictureUploadPath+ File.separator+"picture-"+time+"-thumb"+ext;//加随机数为了避免缓存
            picture = picture.replace("\\","/");
            picture = picture.replace("//","/");
            pictureThumb = pictureThumb.replace("\\","/");
            pictureThumb = pictureThumb.replace("//","/");
            String newPictureFullFileName = PathKit.getWebRootPath()+File.separator+picture;
            String newThumbPictureFullFileName = PathKit.getWebRootPath()+File.separator+pictureThumb;
            newPictureFullFileName = newPictureFullFileName.replace("\\","/");
            newPictureFullFileName = newPictureFullFileName.replace("//","/");
            newThumbPictureFullFileName = newThumbPictureFullFileName.replace("\\","/");
            newThumbPictureFullFileName = newThumbPictureFullFileName.replace("//","/");
            uploadFile.getFile().renameTo(new File(newPictureFullFileName));
            uploadFile.getFile().delete();
            Utils.zoomImage(newPictureFullFileName,newThumbPictureFullFileName,110);//创建缩放图  110px

            //成功
            data.set("message","上传成功")
                    .set("picture",picture)
                    .set("thumb",pictureThumb)
                    .set("code",1)
                    .set("status","success");
        }catch (Exception e){
            e.printStackTrace();
        }
        renderJson(data);
    }

    /**
     * 主面板
     */
    public void main(){
        try{
            //基本信息
            Properties props = System.getProperties();
            ServletContext context = getRequest().getServletContext();
            String serverInfo = context.getServerInfo();
            setAttr("system",props.getProperty("os.name")+" "+props.getProperty("os.arch"));
            setAttr("jdk",props.getProperty("java.version"));
            setAttr("tomcat",serverInfo);
            try{
                DatabaseMetaData dmd  = DbKit.getConfig().getDataSource().getConnection().getMetaData();
                setAttr("db", dmd.getDatabaseProductName()+" "+dmd.getDatabaseProductVersion());
            }catch (Exception e){
                setAttr("db", "获取失败");
            }
            setAttr("userTotal",User.getUserTotal());
            setAttr("userTodayTotal",User.getUserTodayTotal());
            setAttr("rechargeTotal",RechargeOrder.getRechargeTotal());
            setAttr("rechargeTotalReal",RechargeOrder.getRechargeTotalReal());
            setAttr("rechargeTodayTotal",RechargeOrder.getRechargeTodayTotal());
            setAttr("rechargeTodayTotalReal",RechargeOrder.getRechargeTodayTotalReal());
            setAttr("consumeTotal",Consume.getConsumeTotal());
            setAttr("consumeTotalReal",Consume.getConsumeTotaReal());
            setAttr("consumeTodayTotal",Consume.getConsumeTodayTotal());
            setAttr("consumeTodayTotalReal",Consume.getConsumeTodayTotalReal());
            //充值
            setAttr("recharges1",RechargeOrder.getLastWeekRecharges());
            setAttr("recharges2",RechargeOrder.getThisWeekRecharges());
            //消费
            render("Main.fmk");
        }catch (Exception e){
            e.printStackTrace();
            renderError(500);
        }
    }

    /**
     * 基本信息
     */
    public void baseInfo(){
        try{
            render("BaseInfo.fmk");
        }catch (Exception e){
            e.printStackTrace();
            renderError(500);
        }
    }

    /**
     * 修改基本信息
     */
    public void doModifyBaseInfo(){
        try{
            Config config = getModel(Config.class);
            if(config==null || config.getId()!=1 || StrKit.isBlank(config.getSiteName())){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "请检查输入项"
                        , "/"+SystemConfig.sAdminAddr+"/baseInfo"+SystemConfig.sExt, "基础信息", 5, this);
            }else{
                if(config.getIsActive()==null){
                    config.setIsActive(false);
                }
                if(config.update()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "修改成功"
                            , "/"+SystemConfig.sAdminAddr+"/baseInfo"+SystemConfig.sExt, "基础信息", 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改失败"
                            , "/"+SystemConfig.sAdminAddr+"/baseInfo"+SystemConfig.sExt, "基础信息", 5, this);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+"/baseInfo"+SystemConfig.sExt, "基础信息", 5, this);
        }
    }

    /**
     * 管理员列表
     */
    public void adminList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getAdminListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("AdminList.fmk");
    }

    /**
     * 添加/修改管理员
     */
    public void addAdmin(){
        try{
            Integer id = getParaToInt(0);
            String self = getPara(1);
            Admin admin = Admin.getAdminById(id);
            setAttr("self","no");
            if(admin!=null){
                if(StrKit.notBlank(self)){
                    setAttr("self","self");
                }
                setAttr("modifyAdmin",admin);
            }
            setAttr("shopList",Shop.getAllShop(true));
            render("AddAdmin.fmk");
        }catch (Exception e){
            e.printStackTrace();
            if(StrKit.notBlank(getPara(1))){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/addAdmin/"+getPara(0)+"-"+getPara(1)+SystemConfig.sExt, "修改资料", 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/adminList"+SystemConfig.sExt, "管理员列表", 5, this);
            }
        }
    }

    /**
     *  添加/修改管理员
     */
    public void doAddAdmin(){
        String self = getPara(0);
        String jumpUrl = "self".equals(self)? ("/addAdmin/"+getSessionAttr(SESSION_ADMIN_ID)+"-"+self):("/adminList");
        String jumpTitle = "self".equals(self)? ("修改资料"):("管理员列表");
        try{
            Admin admin = getModel(Admin.class);
            int ret = AdminDealService.doAddAdmin(admin,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+jumpUrl+SystemConfig.sExt, jumpTitle, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+jumpUrl+SystemConfig.sExt, jumpTitle, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_NO_PERMISSION){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "权限出错，请重新登录"
                        , "/"+SystemConfig.sAdminAddr+jumpUrl+SystemConfig.sExt, jumpTitle, 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+jumpUrl+SystemConfig.sExt, jumpTitle, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+jumpUrl+SystemConfig.sExt, jumpTitle, 5, this);
        }
    }

    /**
     * 删除管理员记录
     */
    public void doDeleteAdmin(){
        try{
            Integer id = getParaToInt(0);
            Admin admin = Admin.getAdminById(id);
            if(admin!=null){
                if(admin.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+"/adminList"+SystemConfig.sExt, "管理员列表", 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+"/adminList"+SystemConfig.sExt, "管理员列表", 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "投诉记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/adminList"+SystemConfig.sExt, "管理员列表", 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+"/adminList"+SystemConfig.sExt, "管理员列表", 5, this);
        }
    }

    /**
     * 活动列表
     */
    public void promotionList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getPromotionListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("PromotionList.fmk");
    }

    /**
     * 添加/修改活动
     */
    public void addPromotion(){
        try{
            Integer id = getParaToInt(0);
            Promotion promotion = Promotion.getPromotionById(id);
            if(promotion!=null){
                setAttr("modify",promotion);
            }
            render("AddPromotion.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
        }
    }

    /**
     *  添加/修改活动
     */
    public void doAddPromotion(){
        try{
            Promotion promotion = getModel(Promotion.class);
            int ret = AdminDealService.doAddPromotion(promotion,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_DATA_ERROR){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "输入错误"
                        , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
        }
    }

    /**
     * 删除活动记录
     */
    public void doDeletePromotion(){
        try{
            Integer id = getParaToInt(0);
            Promotion promotion = Promotion.getPromotionById(id);
            if(promotion!=null){
                if(promotion.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+"/promotionList"+SystemConfig.sExt, "活动列表", 5, this);
        }
    }

    /**
     * 用户等级列表
     */
    public void userLevelList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getUserLevelListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("UserLevelList.fmk");
    }

    /**
     * 添加/修改等级
     */
    public void addUserLevel(){
        try{
            Integer id = getParaToInt(0);
            UserLevel userLevel = UserLevel.getRecordById(id);
            if(userLevel!=null){
                setAttr("modify",userLevel);
            }
            render("AddUserLevel.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
        }
    }

    /**
     *  添加/修改等级
     */
    public void doAddUserLevel(){
        try{
            UserLevel userLevel = getModel(UserLevel.class);
            int ret = AdminDealService.doAddUserLevel(userLevel,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_DATA_ERROR){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "输入错误"
                        , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
        }
    }

    /**
     * 删除等级记录
     */
    public void doDeleteUserLevel(){
        try{
            Integer id = getParaToInt(0);
            UserLevel userLevel = UserLevel.getRecordById(id);
            if(userLevel!=null){
                if(userLevel.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+"/userLevelList"+SystemConfig.sExt, "等级列表", 5, this);
        }
    }

    /**
     * 用户列表
     */
    public void userList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getUserListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("UserList.fmk");
    }

    /**
     * 添加/修改用户
     */
    public void addUser(){
        String url = "/userList";
        String title = "用户列表";
        try{
            Integer id = getParaToInt(0);
            User user = User.getRecordById(id);
            if(user!=null){
                setAttr("modify",user);
            }
            setAttr("levelList",UserLevel.getUserLevelListBySort());
            render("AddUser.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     *  添加/修改用户
     */
    public void doAddUser(){
        String url = "/userList";
        String title = "用户列表";
        try{
            User user = getModel(User.class);
            int ret = AdminDealService.doAddUser(user,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_DATA_ERROR){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "输入错误"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_USERNAME_TOO_SHORT){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "用户名不可少于4位数"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_PASSWORD_TOO_SHORT){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "密码不可少于6位数"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_USERNAME_ALREADY_HAVE){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "用户名已存在"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 删除等级用户
     */
    public void doDeleteUser(){
        String url = "/userList";
        String title = "用户列表";
        try{
            Integer id = getParaToInt(0);
            User user = User.getRecordById(id);
            if(user!=null){
                if(user.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 店铺列表
     */
    public void shopList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getShopListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("ShopList.fmk");
    }

    /**
     * 添加/修改店铺
     */
    public void addShop(){
        String url = "/shopList";
        String title = "店铺列表";
        try{
            Integer id = getParaToInt(0);
            Shop shop = Shop.getShopById(id);
            if(shop!=null){
                setAttr("modify",shop);
            }
            render("AddShop.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     *  添加/修改店铺
     */
    public void doAddShop(){
        String url = "/shopList";
        String title = "店铺列表";
        try{
            Shop shop = getModel(Shop.class);
            int ret = AdminDealService.doAddShop(shop,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_DATA_ERROR){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "输入错误"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 删除等级店铺
     */
    public void doDeleteShop(){
        String url = "/shopList";
        String title = "店铺列表";
        try{
            Integer id = getParaToInt(0);
            Shop shop = Shop.getShopById(id);
            if(shop!=null){
                if(shop.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 员工列表
     */
    public void workerList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("shopList",Shop.getAllShop(true));
        setAttr("info", AdminViewService.getWorkerListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("WorkerList.fmk");
    }

    /**
     * 添加/修改员工
     */
    public void addWorker(){
        String url = "/workerList";
        String title = "员工列表";
        try{
            Integer id = getParaToInt(0);
            Worker worker = Worker.getRecordById(id);
            if(worker!=null){
                setAttr("modify",worker);
            }
            setAttr("shopList",Shop.getAllShop(true));
            render("AddWorker.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     *  添加/修改员工
     */
    public void doAddWorker(){
        String url = "/workerList";
        String title = "员工列表";
        try{
            Worker worker = getModel(Worker.class);
            int ret = AdminDealService.doAddWorker(worker,getSessionAttr(SESSION_ADMIN_USERNAME));
            if(ret == DealErrorType.COMMON_ERROR_SUCCESS){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "添加/修改成功"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_FAILED){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.COMMON_ERROR_DATA_ERROR){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "输入错误"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_USERNAME_TOO_SHORT){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "用户名不可少于4位数"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_PASSWORD_TOO_SHORT){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "密码不可少于6位数"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else if(ret == DealErrorType.USER_USERNAME_ALREADY_HAVE){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "用户名已存在"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "添加/修改失败，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "修改信息失败，请检查输入项"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 删除员工
     */
    public void doDeleteWorker(){
        String url = "/workerList";
        String title = "员工列表";
        try{
            Integer id = getParaToInt(0);
            Worker worker = Worker.getRecordById(id);
            if(worker!=null){
                if(worker.delete()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "删除成功"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "删除失败，请重试"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }
            }else{
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "记录不存在，请重试"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 充值列表
     */
    public void rechargeOrderList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getRechargeOrderListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("RechargeOrderList.fmk");
    }

    /**
     * 消费列表
     */
    public void consumeList(){
        int pageNumber = getParaToInt(0, 1);
        Integer searchType = 0;
        String searchKey = "";
        String startTime = "";
        String endTime = "";
        if (getPara(1) != null) {
            searchType = getParaToInt(1, 0);
            searchKey = getPara(2);
            try {
                if (searchKey == null) {
                    searchKey = "";
                } else if (!searchKey.equals("")) {
                    searchKey = URLDecoder.decode(searchKey, "UTF-8");//new String(searchKey.getBytes("iso-8859-1"),"utf-8");
                }
            } catch (Exception e) {
                searchKey = "";
                e.printStackTrace();
            }
            startTime = getPara(3);
            endTime = getPara(4);
        } else {
            searchType = getParaToInt("searchType", 0);
            searchKey = getPara("searchKey");
            startTime = getPara("startTime");
            endTime = getPara("endTime");
        }
        if(searchType > 0){
            setAttr("code","success");
            setAttr("message","搜索完成");
        }
        setAttr("info", AdminViewService.getConsumeListData(pageNumber, searchType, searchKey, startTime, endTime, getSessionAttr(SESSION_ADMIN_USERNAME)));
        render("ConsumeList.fmk");
    }

    /**
     * 退回消费
     */
    public void rejectConsume(){
        String url = "/consumeList";
        String title = "消费列表";
        try{
            Integer id = getParaToInt(0);
            Consume consume = Consume.getRecordById(id);
            if(consume!=null){
                setAttr("modify",consume);
            }
            render("RejectConsume.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 处理退回消费
     */
    public void doRejectConsume(){
        String url = "/consumeList";
        String title = "消费列表";
        try{
            Integer id = getParaToInt("id");
            Consume consume = Consume.getRecordById(id);
            if(consume==null){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "消费记录不存在"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else{
                String remark = getPara("remark");
                if(remark!=null){
                    consume.setRemark("######退回备注："+remark);
                }
                consume.setStatus(1);
                consume.setRejectAdmin(getSessionAttr(AdminController.SESSION_ADMIN_ID));
                //TODO返现
                if(consume.update()){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "保存成功"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }else{
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "保存失败"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 转换员工
     */
    public void changeWorker(){
        String url = "/userList";
        String title = "用户列表";
        try{
            Integer id = getParaToInt(0);
            User user = User.getRecordById(id);
            if(user == null){
                throw new Exception("用户不存在 ");
            }
            if(user.isWorker()){
                throw new Exception("该用户已经是员工 ");
            }
            setAttr("shopList",Shop.getAllShop(true));
            setAttr("user",user);
            render("ChangeWorker.fmk");
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, e.getMessage()
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }

    /**
     * 处理转为员工
     */
    public void doChangeWorker(){
        String url = "/userList";
        String title = "用户列表";
        try{
            Integer id = getParaToInt("id");
            Integer shopId = getParaToInt("shopId",0);
            Integer royaltyRate = getParaToInt("royaltyRate",0);
            User user = User.getRecordById(id);
            if(user==null){
                InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "用户不存在"
                        , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
            }else{
                Worker worker = Worker.getRecordByUsername(user.getUsername());
                if(worker!=null){
                    InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "该用户已经是员工"
                            , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                }else{
                    worker = new Worker();
                    worker.setUsername(user.getUsername())
                            .setPassword(user.getPassword())
                            .setRealName(user.getRealName())
                            .setSex(user.getSex())
                            .setBirthday(user.getBirthday())
                            .setPhone(user.getPhone())
                            .setAddress(user.getAddress())
                            .setAvatar(user.getAvatar())
                            .setWorkAddTime(new Date())
                            .setAddTime(new Date())
                            .setIsActive(true)
                            .setShopId(shopId)
                            .setRoyaltyRate(royaltyRate);
                    if(worker.save()){
                        InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.SUCCESS, "操作成功"
                                , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                    }else{
                        InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "操作失败"
                                , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            InfoSkip.adminInfoSkip(InfoSkip.INFO_TYPE.ERROR, "数据出错，请重试"
                    , "/"+SystemConfig.sAdminAddr+url+SystemConfig.sExt, title, 5, this);
        }
    }
}
