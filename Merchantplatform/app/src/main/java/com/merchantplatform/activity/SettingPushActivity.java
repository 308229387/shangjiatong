package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.SettingPushActivityModel;

/**
 * Created by 58 on 2016/12/9.
 */

public class SettingPushActivity  extends BaseActivity<SettingPushActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_push);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
    }

    @Override
    public SettingPushActivityModel createModel() {
        return new SettingPushActivityModel(this);
    }
}
