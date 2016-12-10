package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.SettingActivityModel;

/**
 * Created by 58 on 2016/12/8.
 */

public class SettingActivity extends BaseActivity<SettingActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
    }

    @Override
    public SettingActivityModel createModel() {
        return new SettingActivityModel(this);
    }
}
