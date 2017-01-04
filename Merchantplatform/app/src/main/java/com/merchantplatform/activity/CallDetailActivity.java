package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.bean.CallListNotificationDetail;
import com.merchantplatform.model.CallDetailModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CallDetailActivity extends BaseActivity<CallDetailModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_detail);
        initView();
        initData(getIntent());
        registerEventBus();
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

    private void registerEventBus() {
        model.registerEventBus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CallListNotificationDetail callListNotificationDetail) {
        model.onEvent(callListNotificationDetail);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.unRegisterEventBus();
    }

    @Override
    public CallDetailModel createModel() {
        return new CallDetailModel(this);
    }
}