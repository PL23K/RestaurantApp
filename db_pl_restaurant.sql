/*
Navicat MySQL Data Transfer

Source Server         : Local
Source Server Version : 50717
Source Host           : localhost:3306
Source Database       : db_pl_restaurant

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-08-05 16:38:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_admin
-- ----------------------------
DROP TABLE IF EXISTS `tb_admin`;
CREATE TABLE `tb_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '管理员用户名',
  `password` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '管理员密码',
  `realName` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '管理员姓名',
  `role` int(11) NOT NULL DEFAULT '0' COMMENT '0超级管理员 1分店管理员',
  `phone` varchar(20) COLLATE utf8_unicode_ci DEFAULT '' COMMENT '电话号码',
  `email` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `lastLoginTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '上次登录时间',
  `lastLoginIp` int(11) NOT NULL DEFAULT '0' COMMENT '上次登录IP',
  `lastLoginArea` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '上次登录地点',
  `curLoginTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '当前登录时间',
  `curLoginIp` int(11) NOT NULL DEFAULT '0' COMMENT '当前登录IP',
  `curLoginArea` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '当前登录地点',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `shopId` int(11) NOT NULL DEFAULT '0' COMMENT '所属的店铺ID',
  `royalty` int(11) NOT NULL DEFAULT '0' COMMENT '提成 %比  暂时不用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `k1` (`username`) USING HASH
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_admin
-- ----------------------------
INSERT INTO `tb_admin` VALUES ('1', 'admin', 'ByBhEATY53A=', '超级管理员', '0', '', '', '2019-08-05 11:04:22', '2130706433', '本机地址 本机地址  ', '2019-08-05 11:08:01', '2130706433', '本机地址 本机地址  ', '2019-07-11 14:39:36', '0', '0');

-- ----------------------------
-- Table structure for tb_admin_login_log
-- ----------------------------
DROP TABLE IF EXISTS `tb_admin_login_log`;
CREATE TABLE `tb_admin_login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '管理员用户名',
  `loginTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '登录时间',
  `loginIp` int(11) NOT NULL DEFAULT '0' COMMENT '登录IP',
  `loginArea` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '登录地点',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_admin_login_log
-- ----------------------------

-- ----------------------------
-- Table structure for tb_config
-- ----------------------------
DROP TABLE IF EXISTS `tb_config`;
CREATE TABLE `tb_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `siteName` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '网站名称',
  `siteUrl` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '网站地址',
  `record` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '备案',
  `phone` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '客服电话',
  `email` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '官方邮箱',
  `company` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '公司名称',
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '公司地址',
  `isActive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否营业',
  `wxAppId` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信APPID',
  `wxAppSecret` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信APP秘钥',
  `wxUserInfoCall` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信用户回调接口',
  `wxLoginUrl` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信登录字符串',
  `wxMchId` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '商家ID',
  `wxPayApi` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信支付接口',
  `wxTemplateId` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信消息模板',
  `wxRechargeMessageId` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信充值模板ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_config
-- ----------------------------
INSERT INTO `tb_config` VALUES ('1', '测试餐饮', 'http://www.pl23k.com', '苏ICP备19022040号-1', '1234567890', 'admin@admin.com', '测试餐饮', '浙江省 杭州市 江干区', '1', '0', '0', '0', '0', '0', '0', '0', '0');

-- ----------------------------
-- Table structure for tb_consume
-- ----------------------------
DROP TABLE IF EXISTS `tb_consume`;
CREATE TABLE `tb_consume` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberId` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '会员ID',
  `workerId` int(11) NOT NULL DEFAULT '0' COMMENT '操作人员ID',
  `totalMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '消费总金额',
  `payTotalMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '实付总额',
  `money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除现金',
  `coin` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除金币',
  `evidence` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '小票拍照',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '扣除时间',
  `beforeMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除前现金',
  `beforeCoin` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除后金币',
  `afterMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除后金币',
  `afterCoin` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '扣除后金币',
  `status` tinyint(8) NOT NULL DEFAULT '0' COMMENT '0 完成   1已退回',
  `rejectAdmin` int(11) NOT NULL DEFAULT '0' COMMENT '退回管理员ID',
  `discount` int(11) NOT NULL DEFAULT '0' COMMENT '折扣 %',
  `remark` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_consume
-- ----------------------------

-- ----------------------------
-- Table structure for tb_promotion
-- ----------------------------
DROP TABLE IF EXISTS `tb_promotion`;
CREATE TABLE `tb_promotion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '促销名称',
  `summary` varchar(500) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '促销介绍',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序  越小越靠前',
  `isActive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启动',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `rule` varchar(500) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '规则 json',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_promotion
-- ----------------------------
INSERT INTO `tb_promotion` VALUES ('1', '套餐一', '充值次日返赠送现金', '1', '1', '2019-07-16 02:42:42', '[{\"id\":1,\"充值\":\"0.01\",\"返现时间\":\"24\",\"返现金额\":\"50\"},\r\n{\"id\":2,\"充值\":\"500\",\"返现时间\":\"24\",\"返现金额\":\"200\"},\r\n{\"id\":3,\"充值\":\"1000\",\"返现时间\":\"24\",\"返现金额\":\"500\"},\r\n{\"id\":4,\"充值\":\"2000\",\"返现时间\":\"24\",\"返现金额\":\"1200\"},\r\n]');
INSERT INTO `tb_promotion` VALUES ('2', '套餐二', '充值送抵券', '2', '1', '2019-07-16 02:43:10', '{\"返现比例\":\"100\",\"消费可抵单订比例\":\"20\"}');
INSERT INTO `tb_promotion` VALUES ('3', '套餐三', '商城员工送抵券', '3', '1', '2019-07-23 21:02:38', '{\"返现比例\":\"100\",\"消费可抵单订比例\":\"20\"}');

-- ----------------------------
-- Table structure for tb_recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_recharge_order`;
CREATE TABLE `tb_recharge_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberId` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '会员标识',
  `payType` tinyint(8) NOT NULL DEFAULT '0' COMMENT '充值来源 0管理员 1代理 2支付宝 3微信 4网银 5其他',
  `payId` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '支付流水号（支付前是预付号，支付后是支付号）',
  `prepayId` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '预付码',
  `orderId` varchar(35) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '订单编号',
  `status` tinyint(8) NOT NULL DEFAULT '0' COMMENT '订单状态 0未付款 1付款中 2已付款 3已完成',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `remark` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '订单备注',
  `money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '应付金额',
  `payMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '本次支付的金额',
  `discountMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '本次优惠金额',
  `discount` int(11) NOT NULL DEFAULT '0' COMMENT '折扣 %比',
  `promotion` int(11) NOT NULL DEFAULT '0' COMMENT '活动类型',
  `recommendType` tinyint(8) NOT NULL DEFAULT '0' COMMENT '操作人员类型  0自行充值  1管理员充值 2店铺推荐 3店员推荐 4用户推荐',
  `recommendId` int(11) NOT NULL DEFAULT '0' COMMENT '对应推荐者的ID',
  `shopId` int(11) NOT NULL DEFAULT '0' COMMENT '充值时所在的店铺，只有员工或店铺管理员充值的才有这个标识',
  `royaltyRate` int(11) NOT NULL DEFAULT '0' COMMENT '提成比例（快照）',
  `royalty` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '提成金额',
  `isPayPromotion` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已返现',
  `evidence` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '充值证据，员工充值时需要证据',
  `returnMoney` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '待入账金额',
  PRIMARY KEY (`id`),
  KEY `k1` (`memberId`),
  KEY `k2` (`memberId`,`orderId`),
  KEY `k3` (`orderId`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_recharge_order
-- ----------------------------
INSERT INTO `tb_recharge_order` VALUES ('36', 'A190737630', '0', '', '', 'ORDER3396398843953374', '3', '2019-07-25 15:45:22', '用户PL23K[190737630]于[2019-07-25 15:45:22]充值[充值次日返现活动]2000.00元，24小时后返现1200.00元。推荐员工：呵呵d[190737630]。', '2000.00', '2000.00', '0.00', '100', '1', '3', '2', '1', '0', '0.00', '1', '/upload/file-1564040722117.jpg', '100.00');

-- ----------------------------
-- Table structure for tb_recharge_pay_order_wechat
-- ----------------------------
DROP TABLE IF EXISTS `tb_recharge_pay_order_wechat`;
CREATE TABLE `tb_recharge_pay_order_wechat` (
  `payId` varchar(32) COLLATE utf8_unicode_ci NOT NULL,
  `appId` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信开放平台审核通过的应用APPID',
  `mchId` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信支付分配的商户号',
  `seqNo` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '设备号',
  `nonceStr` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '随机字符串',
  `sign` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '签名',
  `openId` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '用户在商户appid下的唯一标识',
  `isSubscribe` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户是否关注公众账号，1-关注，0-未关注',
  `tradeType` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '交易类型，默认APP',
  `bankType` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '银行类型，采用字符串类型的银行标识',
  `totalFee` int(11) NOT NULL DEFAULT '0' COMMENT '交易金额，单位为分',
  `feeType` varchar(8) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY',
  `cashFee` int(11) NOT NULL DEFAULT '0' COMMENT '现金支付金额订单现金支付金额，单位分',
  `cashFeeType` varchar(16) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '货币类型，符合ISO4217标准的三位字母代码，默认人民币：CNY',
  `couponFee` int(11) NOT NULL DEFAULT '0' COMMENT '代金券或立减优惠金额<=订单总金额，订单总金额-代金券或立减优惠金额=现金支付金额，单位分',
  `couponCount` int(11) NOT NULL DEFAULT '0' COMMENT '代金券或立减优惠使用数量',
  `couponIds` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '代金券或立减优惠ID集，最多20个 例：[22,333,444,555]',
  `couponFees` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '单个代金券或立减优惠支付金额，最多20个 例：[22,333,444,555]',
  `outTradeNo` varchar(32) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '本台内订单号',
  `attach` varchar(128) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '商家数据包，原样返回',
  `timeEnd` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '完成时间',
  PRIMARY KEY (`payId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_recharge_pay_order_wechat
-- ----------------------------

-- ----------------------------
-- Table structure for tb_shop
-- ----------------------------
DROP TABLE IF EXISTS `tb_shop`;
CREATE TABLE `tb_shop` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '店铺名称',
  `summary` varchar(500) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '介绍',
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '店铺地址',
  `picture` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '店铺封面',
  `notice` varchar(500) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '店铺公告',
  `phone` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '联系电话',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `config` text COLLATE utf8_unicode_ci NOT NULL COMMENT 'json配置项',
  `isActive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_shop
-- ----------------------------
INSERT INTO `tb_shop` VALUES ('1', '测试餐饮A店', '欢迎光临测试餐饮A店', '浙江省杭州市江干区', '', '1、新品种到货\r\n2、买一送一', '15111111111', '2019-07-17 14:45:38', '', '1');

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberId` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '用户ID',
  `username` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '用户名',
  `realName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '姓名/昵称',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别 0女 1男',
  `birthday` date NOT NULL DEFAULT '0000-00-00' COMMENT '生日',
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '地址',
  `phone` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '电话',
  `email` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '邮箱',
  `avatar` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '头像地址',
  `isActive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否激活（冻结）',
  `coin` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '金币（平台金币）',
  `money` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '充值余额',
  `level` int(11) NOT NULL DEFAULT '0' COMMENT '等级',
  `wechatId` int(11) NOT NULL DEFAULT '0' COMMENT '绑定的微信ID',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '注册时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES ('1', 'A190000000', 'pl23k', 'ByBhEATY53A=', '测试员', '1', '2019-07-17', '', '12345678901', '123456@qq.com', '', '1', '0.00', '0.00', '1', '0', '2019-07-17 11:41:14');

-- ----------------------------
-- Table structure for tb_user_level
-- ----------------------------
DROP TABLE IF EXISTS `tb_user_level`;
CREATE TABLE `tb_user_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '键',
  `discount` int(11) NOT NULL DEFAULT '0' COMMENT '折扣 %比',
  `condition` int(11) NOT NULL DEFAULT '0' COMMENT '成为条件，消费满N 可自动升级为此等级',
  `sort` int(11) NOT NULL DEFAULT '0' COMMENT '排序  越小越靠前',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_user_level
-- ----------------------------
INSERT INTO `tb_user_level` VALUES ('1', '普通', '100', '0', '1', '2019-07-17 08:00:00');
INSERT INTO `tb_user_level` VALUES ('2', 'A类', '95', '1000', '2', '2019-07-17 08:00:00');
INSERT INTO `tb_user_level` VALUES ('3', 'B类', '85', '3000', '3', '2019-07-17 08:00:00');
INSERT INTO `tb_user_level` VALUES ('4', 'C类', '75', '5000', '4', '2019-07-17 08:00:00');

-- ----------------------------
-- Table structure for tb_wechat_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_wechat_user`;
CREATE TABLE `tb_wechat_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `memberId` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '绑定的用户',
  `openId` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信公众号ID',
  `wechatId` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '微信号',
  `nickName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信昵称',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别',
  `avatarUrl` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '头像地址',
  `accessToken` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '授权码',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `accessTokenTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '授权码时间',
  `unionId` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '联合ID',
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '地址',
  `privilege` varchar(500) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '特权json',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iOpenId` (`openId`) USING BTREE,
  KEY `iMemberId` (`memberId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_wechat_user
-- ----------------------------

-- ----------------------------
-- Table structure for tb_worker
-- ----------------------------
DROP TABLE IF EXISTS `tb_worker`;
CREATE TABLE `tb_worker` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '登录名',
  `password` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '密码',
  `realName` varchar(50) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '性别 0女 1男',
  `birthday` date NOT NULL DEFAULT '0000-00-00' COMMENT '生日',
  `phone` varchar(20) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '联系方式',
  `address` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '地址',
  `avatar` varchar(255) COLLATE utf8_unicode_ci NOT NULL DEFAULT '' COMMENT '头像',
  `workAddTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '入职时间',
  `addTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '添加时间',
  `isActive` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否启用',
  `shopId` int(11) NOT NULL DEFAULT '0' COMMENT '所属店铺ID',
  `royaltyRate` int(11) NOT NULL DEFAULT '0' COMMENT '提成比例  %',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ----------------------------
-- Records of tb_worker
-- ----------------------------
