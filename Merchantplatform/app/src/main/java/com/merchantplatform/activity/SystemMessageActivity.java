package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.SystemMessageActivityModel;

/**
 * Created by 58 on 2016/12/15.
 */

public class SystemMessageActivity extends BaseActivity<SystemMessageActivityModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
    }

    @Override
    public SystemMessageActivityModel createModel() {
        return new SystemMessageActivityModel(this);
    }
}
