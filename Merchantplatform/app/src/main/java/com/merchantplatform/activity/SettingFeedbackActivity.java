package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.SettingFeedbackActivityModel;

/**
 * Created by 58 on 2016/12/10.
 */

public class SettingFeedbackActivity  extends BaseActivity<SettingFeedbackActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_feedback);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
    }

    @Override
    protected void onDestroy() {
        model.destoryOperate();
        super.onDestroy();
    }

    @Override
    public SettingFeedbackActivityModel createModel() {
        return new SettingFeedbackActivityModel(this);
    }
}
