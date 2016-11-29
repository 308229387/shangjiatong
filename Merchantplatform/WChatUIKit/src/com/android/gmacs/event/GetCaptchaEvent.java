package com.android.gmacs.event;

import com.common.gmacs.parse.captcha.Captcha;
import com.common.gmacs.parse.message.Message;

/**
 * Created by zhangxiaoshuang on 16/10/15.
 */
public class GetCaptchaEvent {
    private Captcha captcha;
    private Message message;

    public GetCaptchaEvent(Captcha captcha, Message message) {
        this.captcha = captcha;
        this.message = message;
    }

    public Captcha getCaptcha() {
        return captcha;
    }

    public Message getMessage() {
        return message;
    }
}
