package com.android.gmacs.event;

/**
 * Created by zhaobing on 16/7/26.
 */
public class StarEvent {
    String userId;
    int userSource;
    boolean star;

    public StarEvent(String userId, int userSource, boolean star) {
        this.userId = userId;
        this.userSource = userSource;
        this.star = star;
    }

    public String getUserId() {
        return userId;
    }

    public int getUserSource() {
        return userSource;
    }

    public boolean isStar() {
        return star;
    }
}
