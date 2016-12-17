package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/16.
 */

public class FeedbackResponse implements Serializable {

    private bean data;

    public bean getData() {
        return data;
    }

    public void setData(bean data) {
        this.data = data;
    }

    public class bean implements Serializable{
        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
