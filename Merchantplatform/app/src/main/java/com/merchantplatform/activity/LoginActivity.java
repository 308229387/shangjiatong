package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.LoginActivityModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 * 描    述：登陆页，此界面为Passport界面
 */

public class LoginActivity extends BaseActivity<LoginActivityModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        removeOtherActivity();
        init();
    }

    private void removeOtherActivity() {
        model.removeOtherActivity();
    }

    private void init() {
        model.createCallback();
        model.registerCallback();
        model.createRequest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.unregisterLoginSDK();
    }

    @Override
    public LoginActivityModel createModel() {
        return new LoginActivityModel(this);
    }
}
