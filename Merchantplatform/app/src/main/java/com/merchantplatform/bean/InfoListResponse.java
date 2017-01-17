package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by linyueyang on 17/1/9.
 */

public class InfoListResponse implements Serializable {

    List<InfoListBean> data;

    public List<InfoListBean> getData() {
        return data;
    }

    public void setData(List<InfoListBean> data) {
        this.data = data;
    }
}