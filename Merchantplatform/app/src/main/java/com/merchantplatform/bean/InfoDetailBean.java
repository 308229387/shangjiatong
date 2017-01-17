package com.merchantplatform.bean;

import java.util.List;

/**
 * Created by linyueyang on 17/1/5.
 */

public class InfoDetailBean {

    private String infoId;
    private List<String> pic;
    private String title;//"老牌搬家公司、全市低价",
    private String addDate;//"2016/06/25",
    private String pv;//,1280,//浏览1280次
    private int jzShow;//1,//0-不是精准状态，1-是精准状态
    private int topShow;//1,//0-不是置顶状态，1-是置顶状态

    private int state;//帖子状态
    private String name;//联系人
    private String phone;//电话
    private String catename;//类别
    private String servicearea;//服务区域
    private String address;//地址
    private String serviceintroduce;//服务介绍
    private String auditFailMsg;//审核失败原因


    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public List<String> getPic() {
        return pic;
    }

    public void setPic(List<String> pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public int getJzShow() {
        return jzShow;
    }

    public void setJzShow(int jzShow) {
        this.jzShow = jzShow;
    }

    public int getTopShow() {
        return topShow;
    }

    public void setTopShow(int topShow) {
        this.topShow = topShow;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCatename() {
        return catename;
    }

    public void setCatename(String catename) {
        this.catename = catename;
    }

    public String getServicearea() {
        return servicearea;
    }

    public void setServicearea(String servicearea) {
        this.servicearea = servicearea;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getServiceintroduce() {
        return serviceintroduce;
    }

    public void setServiceintroduce(String serviceintroduce) {
        this.serviceintroduce = serviceintroduce;
    }

    public String getAuditFailMsg() {
        return auditFailMsg;
    }

    public void setAuditFailMsg(String auditFailMsg) {
        this.auditFailMsg = auditFailMsg;
    }
}
