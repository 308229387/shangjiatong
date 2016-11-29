package com.android.gmacs.event;

import com.common.gmacs.parse.contact.Remark;

/**
 * Created by zhaobing on 16/7/26.
 */
public class RemarkEvent {
    String userId;
    int userSource;
    Remark remark;

    public RemarkEvent(String userId, int userSource, Remark remark) {
        this.userId = userId;
        this.userSource = userSource;
        this.remark = remark;
    }

    public int getUserSource() {
        return userSource;
    }

    public String getUserId() {
        return userId;
    }

    public Remark getRemark() {
        return remark;
    }

}
