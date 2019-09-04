package com.pl23k.restaurant.model.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;
import com.pl23k.restaurant.model.RechargeOrder;

import java.util.Date;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseRechargeOrder<M extends BaseRechargeOrder<M>> extends Model<M> implements IBean {

	public M setId(Integer id) {
		set("id", id);
		return (M)this;
	}

	public Integer getId() {
		return getInt("id");
	}

	public M setMemberId(String memberId) {
		set("memberId", memberId);
		return (M)this;
	}

	public String getMemberId() {
		return getStr("memberId");
	}

	public M setPayType(Integer payType) {
		set("payType", payType);
		return (M)this;
	}

	public Integer getPayType() {
		return getInt("payType");
	}

	public M setPayId(String payId) {
		set("payId", payId);
		return (M)this;
	}

	public String getPayId() {
		return getStr("payId");
	}

	public M setOrderId(String orderId) {
		set("orderId", orderId);
		return (M)this;
	}

	public String getOrderId() {
		return getStr("orderId");
	}

	public M setStatus(Integer status) {
		set("status", status);
		return (M)this;
	}

	public Integer getStatus() {
		return getInt("status");
	}

	public M setAddTime(java.util.Date addTime) {
		set("addTime", addTime);
		return (M)this;
	}

	public java.util.Date getAddTime() {
		if(get("addTime")==null){
			set("addTime",new Date(0));
			RechargeOrder recharge = RechargeOrder.getRecordById(getId());
			if(recharge!=null){
				recharge.setAddTime(new Date(0));
				recharge.update();
			}
		}
		return get("addTime");
	}

	public M setRemark(String remark) {
		set("remark", remark);
		return (M)this;
	}

	public String getRemark() {
		return getStr("remark");
	}

	public M setMoney(java.math.BigDecimal money) {
		set("money", money);
		return (M)this;
	}

	public java.math.BigDecimal getMoney() {
		return get("money");
	}

	public M setPayMoney(java.math.BigDecimal payMoney) {
		set("payMoney", payMoney);
		return (M)this;
	}

	public java.math.BigDecimal getPayMoney() {
		return get("payMoney");
	}

	public M setDiscountMoney(java.math.BigDecimal discountMoney) {
		set("discountMoney", discountMoney);
		return (M)this;
	}

	public java.math.BigDecimal getDiscountMoney() {
		return get("discountMoney");
	}

	public M setDiscount(Integer discount) {
		set("discount", discount);
		return (M)this;
	}

	public Integer getDiscount() {
		return getInt("discount");
	}

	public M setPromotion(Integer promotion) {
		set("promotion", promotion);
		return (M)this;
	}

	public Integer getPromotion() {
		return getInt("promotion");
	}

	public M setRecommendType(Integer recommendType){
		set("recommendType",recommendType);
		return (M)this;
	}

	public Integer getRecommendType(){
		return getInt("recommendType");
	}

	public M setRecommendId(Integer recommendId){
		set("recommendId",recommendId);
		return (M)this;
	}

	public Integer getRecommendId(){
		return getInt("recommendId");
	}

	public M setShopId(Integer shopId){
		set("shopId",shopId);
		return (M)this;
	}

	public Integer getShopId(){
		return getInt("shopId");
	}

	public M setRoyaltyRate(Integer royaltyRate){
		set("royaltyRate",royaltyRate);
		return (M)this;
	}

	public Integer getRoyaltyRate(){
		return getInt("royaltyRate");
	}

	public M setRoyalty(java.math.BigDecimal royalty) {
		set("royalty", royalty);
		return (M)this;
	}

	public java.math.BigDecimal getRoyalty() {
		return get("royalty");
	}

	public M setIsPayPromotion(Boolean isPayPromotion){
		set("isPayPromotion",isPayPromotion);
		return (M)this;
	}

	public Boolean getIsPayPromotion(){
		return getBoolean("isPayPromotion");
	}

	public M setPrepayId(String prepayId){
		set("prepayId",prepayId);
		return (M)this;
	}

	public String getPrepayId(){
		return get("prepayId");
	}

	public M setEvidence(String evidence) {
		set("evidence", evidence);
		return (M)this;
	}

	public String getEvidence() {
		return getStr("evidence");
	}

	public M setReturnMoney(java.math.BigDecimal returnMoney) {
		set("returnMoney", returnMoney);
		return (M)this;
	}

	public java.math.BigDecimal getReturnMoney() {
		return get("returnMoney");
	}
}