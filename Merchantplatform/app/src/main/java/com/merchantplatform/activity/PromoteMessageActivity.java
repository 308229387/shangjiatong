package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.PromoteMessageActivityModel;

/**
 * Created by 58 on 2017/1/6.
 */

public class PromoteMessageActivity extends BaseActivity<PromoteMessageActivityModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_message);
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
    public PromoteMessageActivityModel createModel() {
        return new PromoteMessageActivityModel(this);
    }
}
