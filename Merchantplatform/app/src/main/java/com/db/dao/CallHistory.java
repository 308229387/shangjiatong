package com.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 58 on 2016/12/1.
 */
@Entity(generateConstructors = false)
public class CallHistory {

    @Id
    private long id;//从后台获取到的ID
    private long timeStamp;//时间戳
    private String callType;//电话类型
    private String phoneNum;//电话号码
    private String callCity;//来电号码归属地
    private String businessType;//号码服务范围分类
    private long callDate;//呼叫日期
    private long callDuration;//通话持续时长
    private String newData;//新字段
    private String newData2;//更新新字段2

    public CallHistory() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getCallType() {
        return this.callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getPhoneNum() {
        return this.phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCallCity() {
        return this.callCity;
    }

    public void setCallCity(String callCity) {
        this.callCity = callCity;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public long getCallDate() {
        return this.callDate;
    }

    public void setCallDate(long callDate) {
        this.callDate = callDate;
    }

    public long getCallDuration() {
        return this.callDuration;
    }

    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }

    public String getNewData() {
        return this.newData;
    }

    public void setNewData(String newData) {
        this.newData = newData;
    }

    public String getNewData2() {
        return this.newData2;
    }

    public void setNewData2(String newData2) {
        this.newData2 = newData2;
    }
}
