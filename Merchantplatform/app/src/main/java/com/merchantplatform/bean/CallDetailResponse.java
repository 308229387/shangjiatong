package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by 58 on 2016/12/10.
 */

public class CallDetailResponse implements Serializable {

    public ArrayList<bean> getData() {
        return data;
    }

    public void setData(ArrayList<bean> data) {
        this.data = data;
    }

    private ArrayList<bean> data;

    public class bean implements Serializable {

        private long id;//从后台获取到的ID
        private String phone;//电话号码
        private String local;//归属地
        private String cate;//商业类别
        private long callTime;//打电话时间
        private long backTime;//请求接口的回传参数
        private long entryTime;//通话持续时长
        private int callResult;//电话操作结果(接通、未接通)，以此做聚合的区分条件
        private int type;//电话类型（呼入呼出）

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLocal() {
            return local;
        }

        public void setLocal(String local) {
            this.local = local;
        }

        public String getCate() {
            return cate;
        }

        public void setCate(String cate) {
            this.cate = cate;
        }

        public long getCallTime() {
            return callTime;
        }

        public void setCallTime(long callTime) {
            this.callTime = callTime;
        }

        public long getBackTime() {
            return backTime;
        }

        public void setBackTime(long backTime) {
            this.backTime = backTime;
        }

        public long getEntryTime() {
            return entryTime;
        }

        public void setEntryTime(long entryTime) {
            this.entryTime = entryTime;
        }

        public int getCallResult() {
            return callResult;
        }

        public void setCallResult(int callResult) {
            this.callResult = callResult;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
