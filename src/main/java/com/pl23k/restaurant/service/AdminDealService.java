package com.pl23k.restaurant.service;

import com.jfinal.kit.StrKit;
import com.pl23k.restaurant.constants.DealErrorType;
import com.pl23k.restaurant.model.*;
import com.pl23k.restaurant.utils.DESUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by HelloWorld on 2019-03-28.
 */
public class AdminDealService {
    /**
     *  处理添加管理员
     * @param admin
     * @param adminUsername
     * @return
     */
    public static Integer doAddAdmin(Admin admin, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(admin == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                if(StrKit.isBlank(admin.getUsername()) || admin.getUsername().length()<4){
                    ret = DealErrorType.ADMIN_USERNAME_TOO_SHORT;
                    throw new Exception("用户名不可少于4个字符");
                }
                //检查空值
                if(StrKit.isBlank(admin.getRealName())){
                    admin.setRealName("");
                }
                if(StrKit.isBlank(admin.getPhone())){
                    admin.setPhone("");
                }
                if(StrKit.isBlank(admin.getEmail())){
                    admin.setEmail("");
                }
                if(admin.getRoyalty() == null){
                    admin.setRoyalty(0);
                }

                //判断是修改还是新增
                if(admin.getId()==null || admin.getId()<1){
                    //新增
                    if(StrKit.isBlank(admin.getPassword()) || admin.getPassword().length()<6){
                        ret = DealErrorType.ADMIN_PASSWORD_TOO_SHORT;
                        throw new Exception("密码不可少于6个字符");
                    }
                    admin.setPassword(DESUtil.encryStr(admin.getPassword()));
                    admin.setAddTime(new Date());
                    if(admin.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    Admin admin2 = Admin.getAdminById(admin.getId());
                    if(StrKit.isBlank(admin.getPassword())){
                        admin.setPassword(admin2.getPassword());
                    }else{
                        if(admin.getPassword().length()<6){
                            ret = DealErrorType.ADMIN_PASSWORD_TOO_SHORT;
                            throw new Exception("密码不可少于6个字符");
                        }
                        admin.setPassword(DESUtil.encryStr(admin.getPassword()));
                    }
                    admin.setCurLoginIp(admin2.getCurLoginIp());
                    admin.setCurLoginArea(admin2.getCurLoginArea());
                    admin.setCurLoginTime(admin2.getCurLoginTime()==null?(new Date(0)):admin2.getCurLoginTime());
                    admin.setLastLoginIp(admin2.getLastLoginIp());
                    admin.setLastLoginArea(admin2.getLastLoginArea());
                    admin.setLastLoginTime(admin2.getLastLoginTime()==null?(new Date(0)):admin2.getLastLoginTime());
                    admin.setAddTime(admin2.getAddTime());

                    //更新
                    if(admin.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *  处理添加活动
     * @param promotion
     * @param adminUsername
     * @return
     */
    public static Integer doAddPromotion(Promotion promotion, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(promotion == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                if(StrKit.isBlank(promotion.getName())){
                    ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
                    throw new Exception("请输入活动名");
                }
                //检查空值
                if(StrKit.isBlank(promotion.getSummary())){
                    promotion.setSummary("");
                }
                if(StrKit.isBlank(promotion.getRule())){
                    promotion.setRule("");
                }
                if(promotion.getIsActive() == null){
                    promotion.setIsActive(false);
                }

                //判断是修改还是新增
                if(promotion.getId()==null || promotion.getId()<1){
                    //新增
                    promotion.setAddTime(new Date());
                    if(promotion.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    Promotion promotion1 = Promotion.getPromotionById(promotion.getId());
                    promotion.setAddTime(promotion1.getAddTime());

                    //更新
                    if(promotion.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *  处理添加等级
     * @param userLevel
     * @param adminUsername
     * @return
     */
    public static Integer doAddUserLevel(UserLevel userLevel, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(userLevel == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                if(StrKit.isBlank(userLevel.getName())){
                    ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
                    throw new Exception("请输入等级名");
                }
                //检查空值
                if(userLevel.getDiscount()==null || userLevel.getDiscount()<1){
                    userLevel.setDiscount(1);
                }
                if(userLevel.getCondition()==null || userLevel.getCondition()<0){
                    userLevel.setCondition(0);
                }
                if(userLevel.getSort() == null || userLevel.getSort()<0){
                    userLevel.setSort(0);
                }

                //判断是修改还是新增
                if(userLevel.getId()==null || userLevel.getId()<1){
                    //新增
                    userLevel.setAddTime(new Date());
                    if(userLevel.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    UserLevel userLevel1 = UserLevel.getRecordById(userLevel.getId());
                    userLevel.setAddTime(userLevel1.getAddTime());

                    //更新
                    if(userLevel.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *  处理添加用户
     * @param user
     * @param adminUsername
     * @return
     */
    public static Integer doAddUser(User user, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(user == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                //检查空值
                if(StrKit.isBlank(user.getRealName())){
                    user.setRealName("");
                }
                if(user.getSex()==null){
                    user.setSex(false);
                }
                if(user.getBirthday()==null){
                    user.setBirthday(new Date(0));
                }
                if(StrKit.isBlank(user.getAddress())){
                    user.setAddress("");
                }
                if(StrKit.isBlank(user.getPhone())){
                    user.setPhone("");
                }
                if(StrKit.isBlank(user.getEmail())){
                    user.setEmail("");
                }
                if(StrKit.isBlank(user.getAvatar())){
                    user.setAvatar("");
                }
                if(user.getIsActive()==null){
                    user.setIsActive(true);//要反一下
                }
                if(user.getCoin()==null || user.getCoin().compareTo(BigDecimal.ZERO)<0){
                    user.setCoin(BigDecimal.ZERO);
                }
                if(user.getMoney()==null || user.getMoney().compareTo(BigDecimal.ZERO)<0){
                    user.setMoney(BigDecimal.ZERO);
                }
                if(user.getLevel()==null || user.getLevel()<1 || user.getLevel()>4){
                    user.setLevel(1);
                }
                if(user.getWechatId()==null){
                    user.setWechatId(0);
                }

                //判断是修改还是新增
                if(user.getId()==null || user.getId()<1){
                    //新增
                    //检查用户名
                    if(StrKit.isBlank(user.getUsername()) || user.getUsername().length()<4){
                        ret = DealErrorType.USER_USERNAME_TOO_SHORT;
                        throw new Exception("用户名不可少于4位");
                    }
                    User tempUser = User.getRecordByUsername(user.getUsername());
                    if(tempUser!=null){
                        ret = DealErrorType.USER_USERNAME_ALREADY_HAVE;
                        throw new Exception("用户名不可少于4位");
                    }
                    if(StrKit.isBlank(user.getPassword()) || user.getPassword().length()<6){
                        ret = DealErrorType.USER_PASSWORD_TOO_SHORT;
                        throw new Exception("密码不可少于6位");
                    }
                    user.setPassword(DESUtil.encryStr(user.getPassword()));
                    user.setAddTime(new Date());
                    if(user.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    User user1 = User.getRecordById(user.getId());
                    if(StrKit.notBlank(user.getPassword())){
                        if(user.getPassword().length()<6){
                            ret = DealErrorType.USER_PASSWORD_TOO_SHORT;
                            throw new Exception("密码不可少于6位");
                        }else{
                            user.setPassword(DESUtil.encryStr(user.getPassword()));
                        }
                    }else{
                        user.setPassword(user1.getPassword());
                    }
                    user.setAddTime(user1.getAddTime());
                    user.setMemberId(user1.getMemberId());
                    user.setCoin(user1.getCoin());
                    user.setMoney(user1.getMoney());
                    user.setWechatId(user1.getWechatId());

                    //更新
                    if(user.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *  处理添加店铺
     * @param shop
     * @param adminUsername
     * @return
     */
    public static Integer doAddShop(Shop shop, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(shop == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                //检查空值
                if(StrKit.isBlank(shop.getName())){
                    ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
                    throw new Exception("参数错误");
                }
                if(StrKit.isBlank(shop.getSummary())){
                    shop.setSummary("");
                }
                if(StrKit.isBlank(shop.getAddress())){
                    shop.setAddress("");
                }
                if(StrKit.isBlank(shop.getPicture())){
                    shop.setPicture("");
                }
                if(StrKit.isBlank(shop.getNotice())){
                    shop.setNotice("");
                }
                if(StrKit.isBlank(shop.getPhone())){
                    shop.setPhone("");
                }
                if(StrKit.isBlank(shop.getConfig())){
                    shop.setConfig("");
                }
                if(shop.getIsActive()==null){
                    shop.setIsActive(false);
                }
                //判断是修改还是新增
                if(shop.getId()==null || shop.getId()<1){
                    //新增
                    shop.setAddTime(new Date());
                    if(shop.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    Shop shop1 = Shop.getShopById(shop.getId());
                    shop.setAddTime(shop1.getAddTime());

                    //更新
                    if(shop.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }

    /**
     *  处理添加员工
     * @param worker
     * @param adminUsername
     * @return
     */
    public static Integer doAddWorker(Worker worker, String adminUsername){
        Integer ret = DealErrorType.COMMON_ERROR_FAILED;
        try{
            if(worker == null || StrKit.isBlank(adminUsername)){
                ret = DealErrorType.COMMON_ERROR_DATA_ERROR;
            }else{
                //检查空值
                if(StrKit.isBlank(worker.getRealName())){
                    worker.setRealName("");
                }
                if(worker.getSex()==null){
                    worker.setSex(false);
                }
                if(worker.getBirthday()==null){
                    worker.setBirthday(new Date(0));
                }
                if(StrKit.isBlank(worker.getAddress())){
                    worker.setAddress("");
                }
                if(StrKit.isBlank(worker.getPhone())){
                    worker.setPhone("");
                }
                if(StrKit.isBlank(worker.getAvatar())){
                    worker.setAvatar("");
                }
                if(worker.getIsActive()==null){
                    worker.setIsActive(false);
                }
                if(worker.getRoyaltyRate()==null || worker.getRoyaltyRate()<0){
                    worker.setRoyaltyRate(0);
                }
                if(worker.getWorkAddTime()==null){
                    worker.setWorkAddTime(new Date(0));
                }
                if(worker.getShopId()==null){
                    worker.setShopId(0);
                }

                //判断是修改还是新增
                if(worker.getId()==null || worker.getId()<1){
                    //新增
                    //检查用户名
                    if(StrKit.isBlank(worker.getUsername()) || worker.getUsername().length()<4){
                        ret = DealErrorType.USER_USERNAME_TOO_SHORT;
                        throw new Exception("用户名不可少于4位");
                    }
                    Worker temp = Worker.getRecordByUsername(worker.getUsername());
                    if(temp!=null){
                        ret = DealErrorType.USER_USERNAME_ALREADY_HAVE;
                        throw new Exception("用户名不可少于4位");
                    }
                    if(StrKit.isBlank(worker.getPassword()) || worker.getPassword().length()<6){
                        ret = DealErrorType.USER_PASSWORD_TOO_SHORT;
                        throw new Exception("密码不可少于6位");
                    }
                    worker.setPassword(DESUtil.encryStr(worker.getPassword()));
                    worker.setAddTime(new Date());
                    if(worker.save()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }else{
                    //修改
                    Worker worker1 = Worker.getRecordById(worker.getId());
                    if(StrKit.notBlank(worker.getPassword())){
                        if(worker.getPassword().length()<6){
                            ret = DealErrorType.USER_PASSWORD_TOO_SHORT;
                            throw new Exception("密码不可少于6位");
                        }else{
                            worker.setPassword(DESUtil.encryStr(worker.getPassword()));
                        }
                    }else{
                        worker.setPassword(worker1.getPassword());
                    }
                    worker.setAddTime(worker1.getAddTime());

                    //更新
                    if(worker.update()){
                        ret = DealErrorType.COMMON_ERROR_SUCCESS;
                    }else{
                        ret = DealErrorType.COMMON_ERROR_FAILED;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ret;
    }
}
