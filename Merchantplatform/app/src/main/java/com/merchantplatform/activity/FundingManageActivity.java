package com.merchantplatform.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.merchantplatform.R;
import com.merchantplatform.model.FundingManageModel;

/**
 * Created by 58 on 2016/12/8.
 */

public class FundingManageActivity extends BaseActivity<FundingManageModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_funding_manage);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
        model.initWebViewContainer();
        model.initRechargePage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.destoryWebView();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (model.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public FundingManageModel createModel() {
        return new FundingManageModel(this);
    }
}
