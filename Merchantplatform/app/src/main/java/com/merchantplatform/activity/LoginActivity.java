package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.LoginActivityModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 */

public class LoginActivity extends BaseActivity<LoginActivityModel> {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        model.createCallback();
        model.setCallback();
        model.createRequest();
    }

    @Override
    public LoginActivityModel createModel() {
        return new LoginActivityModel(this);
    }
}
