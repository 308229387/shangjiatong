package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/17.
 */

public class LoginResponse implements Serializable{

    private data data;

    public LoginResponse.data getData() {
        return data;
    }

    public void setData(LoginResponse.data data) {
        this.data = data;
    }

    public class data implements Serializable{
        private String verified;

        public String getVerified() {
            return verified;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }
    }
}
