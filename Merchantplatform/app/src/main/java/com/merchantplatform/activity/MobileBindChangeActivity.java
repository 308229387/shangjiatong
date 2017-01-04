package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.MobileBindChangeModel;

/**
 * Created by 58 on 2016/12/9.
 */

public class MobileBindChangeActivity  extends BaseActivity<MobileBindChangeModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_bind_change);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
    }

    @Override
    public MobileBindChangeModel createModel() {
        return new MobileBindChangeModel(this);
    }
}
