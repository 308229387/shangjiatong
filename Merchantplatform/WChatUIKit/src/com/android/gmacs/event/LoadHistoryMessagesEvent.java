package com.android.gmacs.event;

import com.common.gmacs.parse.message.Message;

import java.util.List;

/**
 * Created by zhaobing on 16/7/25.
 */
public class LoadHistoryMessagesEvent {

    List<Message> messages;

    public LoadHistoryMessagesEvent(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

}
