package com.push.bean;

import java.io.Serializable;


public class PushBean implements Serializable {
    protected String pushTime;
    protected int pushType;
    private String data;
    protected String pushTitle; // 推送的 title (APNs推送同样适用此字段)
    protected String pushMessage; // 推送的 message (APNs推送同样适用此字段)
    protected int msgCate; // 消息分类页聚合类别(当前有3类:系统消息,运营活动,留言)
    protected int busiCode; // 业务逻辑
    //2015.12.28增加通知未读状态
    protected boolean isRead;

    public int getPushType() {
        return pushType;
    }

    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public int getMsgCate() {
        return msgCate;
    }

    public void setMsgCate(int msgCate) {
        this.msgCate = msgCate;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public String getPushTitle() {
        return pushTitle;
    }

    public void setPushTitle(String pushTitle) {
        this.pushTitle = pushTitle;
    }

    public String getPushMessage() {
        return pushMessage;
    }

    public void setPushMessage(String pushMessage) {
        this.pushMessage = pushMessage;
    }

    public int getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(int busiCode) {
        this.busiCode = busiCode;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
