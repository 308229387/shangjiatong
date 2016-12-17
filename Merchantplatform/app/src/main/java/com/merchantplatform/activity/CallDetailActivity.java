package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.CallDetailModel;

public class CallDetailActivity extends BaseActivity<CallDetailModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_record);
        initView();
        initData(getIntent());
    }

    private void initView() {
        model.initView();
        model.setTitleBar();
        model.setListener();
    }

    private void initData(Intent intent) {
        model.initAdapter();
        model.initData(intent);
    }

    @Override
    public CallDetailModel createModel() {
        return new CallDetailModel(this);
    }
}