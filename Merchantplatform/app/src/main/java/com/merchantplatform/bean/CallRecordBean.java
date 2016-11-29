package com.merchantplatform.bean;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordBean {
    private int callRecordId;
    private String phoneState;
    private String phoneNum;
    private int callCount;
    private String phoneNumCity;
    private String callType;
    private String callTime;

    public int getCallRecordId() {
        return callRecordId;
    }

    public void setCallRecordId(int callRecordId) {
        this.callRecordId = callRecordId;
    }

    public String getPhoneState() {
        return phoneState;
    }

    public void setPhoneState(String phoneState) {
        this.phoneState = phoneState;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public String getPhoneNumCity() {
        return phoneNumCity;
    }

    public void setPhoneNumCity(String phoneNumCity) {
        this.phoneNumCity = phoneNumCity;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
}
