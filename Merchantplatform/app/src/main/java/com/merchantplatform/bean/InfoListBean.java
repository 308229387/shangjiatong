package com.merchantplatform.bean;

/**
 * Created by linyueyang on 17/1/5.
 */

public class InfoListBean {

    private String infoId;
    private String sortId;//数字,//回传给server
    private String pic;//"http://pic1.58cdn.com.cn/p1/small/n_14987037362703.jpg",
    private String title;//"老牌搬家公司、全市低价",
    private String addDate;//"2016/06/25",
    private int auditState;//数字,//1”、“11”状态，前端显示为“显示中”;“2”状态，显示为“审核中”；“4”状态显示为“审核失败”；“0”状态显示为“已删除”
    private String auditFailMsg;//"信息审核不通过"，
    private String pv;//,1280,//浏览1280次
    private int jzShow;//1,//0-不是精准状态，1-是精准状态
    private int topShow;//1,//0-不是置顶状态，1-是置顶状态

    public String getInfoId() {
        return infoId;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public String getSortId() {
        return sortId;
    }

    public void setSortId(String sortId) {
        this.sortId = sortId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
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

    public int getAuditState() {
        return auditState;
    }

    public void setAuditState(int auditState) {
        this.auditState = auditState;
    }

    public String getAuditFailMsg() {
        return auditFailMsg;
    }

    public void setAuditFailMsg(String auditFailMsg) {
        this.auditFailMsg = auditFailMsg;
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
}
