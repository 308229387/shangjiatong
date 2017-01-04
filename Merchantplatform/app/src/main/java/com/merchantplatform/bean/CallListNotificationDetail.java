package com.merchantplatform.bean;

/**
 * Created by 58 on 2016/12/22.
 */

public class CallListNotificationDetail {
    private String jumpMsg;

    public CallListNotificationDetail(String jumpMsg) {
        this.jumpMsg = jumpMsg;
    }

    public String getJumpMsg() {
        return jumpMsg;
    }

    public void setJumpMsg(String jumpMsg) {
        this.jumpMsg = jumpMsg;
    }
}
