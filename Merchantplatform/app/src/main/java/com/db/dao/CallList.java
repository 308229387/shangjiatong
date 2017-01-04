package com.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by 58 on 2016/12/6.
 */
@Entity(generateConstructors = false)
public class CallList {

    @Id(autoincrement = true)
    private Long id;
    private String userId;//用户Id
    private String phone;//电话号码
    private int phoneCount;//聚合后的次数
    private int callResult;//电话操作结果(接通、未接通)
    private int type;//电话类型（呼入呼出）
    private String local;//归属地
    private String cate;//商业类别
    private String callTime;//打电话时间

    public CallList() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPhoneCount() {
        return this.phoneCount;
    }

    public void setPhoneCount(int phoneCount) {
        this.phoneCount = phoneCount;
    }

    public int getCallResult() {
        return this.callResult;
    }

    public void setCallResult(int callResult) {
        this.callResult = callResult;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocal() {
        return this.local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getCate() {
        return this.cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getCallTime() {
        return this.callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }
}