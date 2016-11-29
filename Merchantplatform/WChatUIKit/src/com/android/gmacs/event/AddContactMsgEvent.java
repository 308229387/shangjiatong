package com.android.gmacs.event;

/**
 * Created by zhangxiaoshuang on 16/10/21.
 */
public class AddContactMsgEvent {
    private String contactId;
    private int contactSource;

    public AddContactMsgEvent(String contactId, int contactSource) {
        this.contactId = contactId;
        this.contactSource = contactSource;
    }


    public String getContactId() {
        return contactId;
    }

    public int getContactSource() {
        return contactSource;
    }
}
