package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.PushActivityModel;
import com.push.WPushListener;
import com.wuba.wbpush.Push;

/**
 * Created by 58 on 2016/11/28.
 */

public class PushActivity extends BaseActivity<PushActivityModel> implements WPushListener {

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
        model.setPushListener(this);
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
        model.setPushListener(null);
    }

    @Override
    public PushActivityModel createModel() {
        return new PushActivityModel(this);
    }

    @Override
    public void onDeviceIDAvalible(String deviceID) {
        model.onModelDeviceIDAvalible(deviceID);
    }

    @Override
    public void onError(int errorCode, String errorString) {
        model.onModelError(errorCode, errorString);
    }

    @Override
    public void onNotificationClicked(String messageId) {
        model.onModelNotificationClicked(messageId);
    }

    @Override
    public void OnMessage(Push.PushMessage message) {
       model.OnModelMessage(message);
    }
}
