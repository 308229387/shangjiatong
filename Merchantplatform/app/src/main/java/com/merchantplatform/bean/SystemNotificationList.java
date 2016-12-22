package com.merchantplatform.bean;

import com.Utils.SystemNotification;

import java.io.Serializable;
import java.util.ArrayList;

public class SystemNotificationList implements Serializable {
    private ArrayList<SystemNotification> data;

    public ArrayList<SystemNotification> getData() {
        return data;
    }

    public void setData(ArrayList<SystemNotification> data) {
        this.data = data;
    }

}
