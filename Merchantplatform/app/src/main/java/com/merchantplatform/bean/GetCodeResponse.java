package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/17.
 */

public class GetCodeResponse implements Serializable {

    private data data;

    public GetCodeResponse.data getData() {
        return data;
    }

    public void setData(GetCodeResponse.data data) {
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
