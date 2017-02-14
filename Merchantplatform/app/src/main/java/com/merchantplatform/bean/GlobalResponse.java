package com.merchantplatform.bean;

import java.io.Serializable;

/**
 * Created by 58 on 2016/12/17.
 */

public class GlobalResponse implements Serializable{

    private data data;

    public GlobalResponse.data getData() {
        return data;
    }

    public void setData(GlobalResponse.data data) {
        this.data = data;
    }

    public class data implements Serializable{
        private String appUrl; //app下载Url地址
        private String isPayOpen; //我的资金开关
        private String version ; //app版本号
        private String isForceUpdate; //是否强制更新
        private String staffContactPhone; //客服联系电话
        private String isUserFundsOpen; //充值开关
        private int isVip;

        public int getIsVip() {
            return isVip;
        }

        public String getAppUrl() {
            return appUrl;
        }

        public void setAppUrl(String appUrl) {
            this.appUrl = appUrl;
        }

        public String getIsPayOpen() {
            return isPayOpen;
        }

        public void setIsPayOpen(String isPayOpen) {
            this.isPayOpen = isPayOpen;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getIsForceUpdate() {
            return isForceUpdate;
        }

        public void setIsForceUpdate(String isForceUpdate) {
            this.isForceUpdate = isForceUpdate;
        }

        public String getStaffContactPhone() {
            return staffContactPhone;
        }

        public void setStaffContactPhone(String staffContactPhone) {
            this.staffContactPhone = staffContactPhone;
        }

        public String getIsUserFundsOpen() {
            return isUserFundsOpen;
        }

        public void setIsUserFundsOpen(String isUserFundsOpen) {
            this.isUserFundsOpen = isUserFundsOpen;
        }
    }
}
