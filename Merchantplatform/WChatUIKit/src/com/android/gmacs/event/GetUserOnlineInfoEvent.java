package com.android.gmacs.event;

import com.common.gmacs.parse.contact.UserOnlineInfo;

/**
 * Created by zhaobing on 16/10/24.
 */

public class GetUserOnlineInfoEvent {
    String userId;
    int userSource;
    UserOnlineInfo userOnlineInfo;

    public GetUserOnlineInfoEvent(String userId, int userSource, UserOnlineInfo userOnlineInfo) {
        this.userId = userId;
        this.userSource = userSource;
        this.userOnlineInfo = userOnlineInfo;
    }
}
