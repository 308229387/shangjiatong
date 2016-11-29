package com.android.gmacs.event;

import com.common.gmacs.parse.talk.Talk;

import java.util.List;

/**
 * Created by zhaobing on 16/7/26.
 */
public class RecentTalksEvent {
    List<Talk> mTalks;

    public RecentTalksEvent(List<Talk> mTalks) {
        this.mTalks = mTalks;
    }

    public List<Talk> getTalks() {
        return mTalks;
    }
}
