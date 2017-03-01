package com.android.gmacs.event;

import com.common.gmacs.parse.message.Message;

/**
 * Created by zhaobing on 16/10/17.
 */

public class ValidateCaptchaEvent {
    private boolean success;
    private Message message;

    public ValidateCaptchaEvent(boolean success, Message message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Message getMessage() {
        return message;
    }
}
