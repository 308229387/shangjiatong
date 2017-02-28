package com.merchantplatform.bean;


import com.Utils.Urls;

/**
 * Created by 58 on 2016/10/20.
 */

public class RechargeOrder {

    private String productName;  //商品名字
    private String productDesc;  //商品描述
    private float orderMoney;  //交易金额，保留两位小数
    private String buyAccountId;  //购买用户 ID，用户在 58 系统中的用户 ID
    private static final String payFrom = "22";   //业务方从哪里过 来的
    private static final String platfrom = "app";  //业务方从哪里过 来的
    private String merid;  //商户ID
    private String rechargeType; //充值类型
    private String cookie; //用户登录cookie 示例：www58com=“xxx”; PPU=“xxx"

    /**
     * 生成订单信息
     *
     * @param merid       商户id
     * @param productName 商品名字
     * @param productDesc 商品描述
     * @param orderMoney  交易金额
     */
    public void setOrderContent(String merid, String productName, String productDesc, float orderMoney) {
        this.setMerid("1149");
        this.setProductName(productName);
        this.setProductDesc(productDesc);
        this.setOrderMoney(orderMoney);
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public float getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(float orderMoney) {
        this.orderMoney = orderMoney;
    }

    public String getBuyAccountId() {
        return buyAccountId;
    }

    public void setBuyAccountId(String buyAccountId) {
        this.buyAccountId = buyAccountId;
    }

    public String getPayFrom() {
        return payFrom;
    }

    public String getPlatfrom() {
        return platfrom;
    }

    public String getMerid() {
        return merid;
    }

    public void setMerid(String merid) {
        this.merid = merid;
    }

    public String getNotifyUrl() {
        return Urls.RECHARGE_NOTIFY_URL;
    }

    public String getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(String rechargeType) {
        this.rechargeType = rechargeType;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}