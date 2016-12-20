package com.android.gmacs.activity;


import android.os.Bundle;

import com.android.gmacs.R;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_notification_layout);
        setBackEnable(false);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }
}
