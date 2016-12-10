package com.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by 58 on 2016/12/1.
 */
@Entity(generateConstructors = false)
public class CallDetail {

    @Id
    private long id;//从后台获取到的ID
    private String userId;//用户Id
    private String phone;//电话号码
    private String local;//归属地
    private String cate;//商业类别
    private long callTime;//打电话时间
    private long backTime;//请求接口的回传参数
    private long entryTime;//通话持续时长
    private int callResult;//电话操作结果(接通、未接通)，以此做聚合的区分条件
    private int type;//电话类型（呼入呼出）
    private boolean isDeleted;//数据是否被删除

    public CallDetail() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
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

    public long getCallTime() {
        return this.callTime;
    }

    public void setCallTime(long callTime) {
        this.callTime = callTime;
    }

    public long getBackTime() {
        return this.backTime;
    }

    public void setBackTime(long backTime) {
        this.backTime = backTime;
    }

    public long getEntryTime() {
        return this.entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
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

    public boolean getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
