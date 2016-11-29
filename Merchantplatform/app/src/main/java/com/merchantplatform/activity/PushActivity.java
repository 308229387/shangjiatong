package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.PushActivityModel;

/**
 * Created by 58 on 2016/11/28.
 */

public class PushActivity extends BaseActivity<PushActivityModel>  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push);
        init();
    }

    private void init(){
        model.initLayout();
        model.initListener();
        model.initHandler();
        model.setPushListener();
        model.goToNewsActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.refreshLogInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.clearPushListener();
    }

    @Override
    public PushActivityModel createModel() {
        return new PushActivityModel(this);
    }

}
