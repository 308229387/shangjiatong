package com.merchantplatform.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by linyueyang on 17/1/9.
 */

public class InfoDetailResponse implements Serializable {

    InfoDetailBean data;

    public InfoDetailBean getData() {
        return data;
    }

    public void setData(InfoDetailBean data) {
        this.data = data;
    }
}