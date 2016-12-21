package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.CallDetailModel;

public class CallDetailActivity extends BaseActivity<CallDetailModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_detail);
        initView();
        initData(getIntent());
        registPhoneBroadcast();
    }

    private void initView() {
        model.initView();
        model.setTitleBar();
        model.setRecyclerView();
        model.setListener();
    }

    private void initData(Intent intent) {
        model.initData(intent);
        model.initAdapter();
    }

    private void registPhoneBroadcast() {
        model.registPhoneBroadcast();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.releasePhoneMonitor();
    }

    @Override
    public CallDetailModel createModel() {
        return new CallDetailModel(this);
    }
}