package com.merchantplatform.bean;

/**
 * Created by 58 on 2016/12/15.
 */

public class UserCallRecordBean {
    private long beginTime;
    private long endTime;
    private int recordState;

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
