package com.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 58 on 2016/12/6.
 */
@Entity(generateConstructors = false)
public class CallList {

    @Id
    private long id;
    private long timeStamp;
    private String callType;
    private String phoneNum;

    public CallList() {
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
}
