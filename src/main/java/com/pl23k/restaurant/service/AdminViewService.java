package com.pl23k.restaurant.service;

import com.jfinal.plugin.activerecord.Page;
import com.pl23k.restaurant.model.*;
import com.sun.corba.se.spi.orbutil.threadpool.Work;

import java.io.File;
import java.util.*;

/**
 * Created by HelloWorld on 2019-03-28.
 */
public class AdminViewService {
    /**
     * 管理员列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getAdminListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<Admin> page = null;
            if (searchType <= 0) {
                page = Admin.getAdminByPage(pageNumber);
            } else {
                page = Admin.getAdminByPage(pageNumber); //暂时没有更多搜索功能
                /*
                page = Notice.searchNoticeByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime,true);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
                */
            }

            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 活动列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getPromotionListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<Promotion> page = null;
            if (searchType <= 0) {
                page = Promotion.getPromotionByPage(pageNumber);
            } else {
                page = Promotion.getPromotionByPage(pageNumber); //暂时没有更多搜索功能
                /*
                page = Notice.searchNoticeByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime,true);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
                */
            }

            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 用户等级列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getUserLevelListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<UserLevel> page = null;
            if (searchType <= 0) {
                page = UserLevel.getRecordByPage(pageNumber);
            } else {
                page = UserLevel.getRecordByPage(pageNumber); //暂时没有更多搜索功能
                /*
                page = Notice.searchNoticeByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime,true);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
                */
            }

            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 用户列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getUserListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<User> page = null;
            if (searchType <= 0) {
                page = User.getRecordByPage(pageNumber);
            } else {
                page = User.searchRecordByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
            }

            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 店铺列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getShopListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<Shop> page = null;
            if (searchType <= 0) {
                page = Shop.getRecordByPage(pageNumber);
            } else {
                page = Shop.searchRecordByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
            }

            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 员工列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getWorkerListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<Worker> page = null;
            if (searchType <= 0) {
                page = Worker.getRecordByPage(pageNumber);
            } else {
                page = Worker.searchRecordByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
            }
            if(searchType==3){
                try{
                    Shop shop = Shop.getShopById(Integer.valueOf(searchKey));
                    if(shop!=null){
                        map.put("shopName",shop.getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 充值列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getRechargeOrderListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<RechargeOrder> page = null;
            if (searchType <= 0) {
                page = RechargeOrder.getRecordByPage(pageNumber);
            } else {
                page = RechargeOrder.searchRecordByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
            }
            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 消费列表数据
     * @param pageNumber
     * @param searchType
     * @param searchKey
     * @param startTime
     * @param endTime
     * @param adminUsername
     * @return
     */
    public static Map<String, Object> getConsumeListData(int pageNumber, int searchType, String searchKey, String startTime, String endTime, String adminUsername) {
        Map<String, Object> map = null;
        try {
            //searchType
            map = new HashMap<>();
            Page<Consume> page = null;
            if (searchType <= 0) {
                page = Consume.getRecordByPage(pageNumber);
            } else {
                page = Consume.searchRecordByPage(pageNumber, searchType, searchKey.trim(), startTime, endTime);
                if (page != null) {
                    map.put("searchType", searchType);
                    map.put("searchKey", searchKey);
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                }
            }
            map.put("page", page);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
