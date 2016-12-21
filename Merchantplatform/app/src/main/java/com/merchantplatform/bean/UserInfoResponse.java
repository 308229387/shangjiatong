package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/16.
 */

public class UserInfoResponse implements Serializable {

    private bean data;

    public bean getData() {
        return data;
    }

    public void setData(bean data) {
        this.data = data;
    }

    public class bean implements Serializable{
        private String bindPhone;
        private String phone;
        private String sex;

        public String getBindPhone() {
            return bindPhone;
        }

        public void setBindPhone(String bindPhone) {
            this.bindPhone = bindPhone;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }
}
