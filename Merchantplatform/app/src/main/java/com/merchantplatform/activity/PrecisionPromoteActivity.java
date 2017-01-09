package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.PrecisionPromoteActivityModel;

/**
 * Created by 58 on 2017/1/9.
 */

public class PrecisionPromoteActivity  extends BaseActivity<PrecisionPromoteActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precision_promote);
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
    public PrecisionPromoteActivityModel createModel() {
        return new PrecisionPromoteActivityModel(this);
    }
}
