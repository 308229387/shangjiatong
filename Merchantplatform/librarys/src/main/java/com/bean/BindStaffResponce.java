package com.bean;

import java.io.Serializable;

/**
 * Created by linyueyang on 17/2/28.
 */

public class BindStaffResponce implements Serializable {

    BindStaffBean data;

    public BindStaffBean getData() {
        return data;
    }

    public void setData(BindStaffBean data) {
        this.data = data;
    }
}
