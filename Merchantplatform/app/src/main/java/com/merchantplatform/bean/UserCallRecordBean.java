package com.merchantplatform.bean;

/**
 * Created by 58 on 2016/12/15.
 */

public class UserCallRecordBean {
    private long backTime;
    private String ids;
    private long beginTime;
    private long endTime;
    private int recordState;

    public long getBackTime() {
        return backTime;
    }

    public void setBackTime(long backTime) {
        this.backTime = backTime;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRecordState() {
        return recordState;
    }

    public void setRecordState(int recordState) {
        this.recordState = recordState;
    }
}
