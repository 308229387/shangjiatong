package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by songyongmeng on 2017/2/8.
 */

public class GetSystemTime implements Serializable {
    private data data;

    public GetSystemTime.data getData() {
        return data;
    }

    public void setData(GetSystemTime.data data) {
        this.data = data;
    }

    public class data implements Serializable {
        private String currentTime;

        public String getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(String currentTime) {
            this.currentTime = currentTime;
        }
    }
}
