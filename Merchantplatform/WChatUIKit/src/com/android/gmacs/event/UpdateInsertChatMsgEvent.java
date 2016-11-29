package com.android.gmacs.event;

import com.common.gmacs.parse.message.Message;

/**
 * Created by zhangxiaoshuang on 16/10/24.
 */
public class UpdateInsertChatMsgEvent {
    private Message message;
    public UpdateInsertChatMsgEvent(Message message){
        this.message=message;
    }

    public Message getMessage() {
        return message;
    }
}
