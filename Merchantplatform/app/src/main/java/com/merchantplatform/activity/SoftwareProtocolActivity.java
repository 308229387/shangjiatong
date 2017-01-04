package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.SoftwareProtocolActivityModel;

/**
 * Created by 58 on 2016/12/10.
 */

public class SoftwareProtocolActivity extends BaseActivity<SoftwareProtocolActivityModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_protocol);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.initIntorductionPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.destoryWebView();
    }

    @Override
    public SoftwareProtocolActivityModel createModel() {
        return new SoftwareProtocolActivityModel(this);
    }
}
