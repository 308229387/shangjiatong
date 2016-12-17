package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/17.
 */

public class BindMobileResponse implements Serializable {

    private data data;

    public BindMobileResponse.data getData() {
        return data;
    }

    public void setData(BindMobileResponse.data data) {
        this.data = data;
    }

    public class data implements Serializable{
        private String msg;
        private String status;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
