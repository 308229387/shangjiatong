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
        private String appUrl;
        private String isPayOpen; //ios支付开关
        private String version ;

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
    }
}
