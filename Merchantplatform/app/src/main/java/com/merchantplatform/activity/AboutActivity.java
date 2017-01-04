package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.AboutActivityModel;

/**
 * Created by 58 on 2016/11/25.
 */
public class AboutActivity extends BaseActivity<AboutActivityModel>{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init();
    }

    private void init(){
        model.initView();
        model.initTitleBar();
        model.setVersionName();
        model.setSoftwareUsage();
    }

    @Override
    public AboutActivityModel createModel() {
        return new AboutActivityModel(this);
    }
}
