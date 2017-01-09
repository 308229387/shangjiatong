package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.UpPromoteActivityModel;

/**
 * Created by 58 on 2017/1/9.
 */

public class UpPromoteActivity extends BaseActivity<UpPromoteActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_up_promote);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
        model.initWebViewContainer();
        model.initPromotePage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.destoryWebView();
    }

    @Override
    public UpPromoteActivityModel createModel() {
        return new UpPromoteActivityModel(this);
    }
}
