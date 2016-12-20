package com.android.gmacs.activity;


import android.os.Bundle;
import android.widget.ListView;

import com.android.gmacs.R;
import com.android.gmacs.adapter.SystemNotificationAdapter;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationActivity extends BaseActivity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_notification_layout);
        setBackEnable(false);
        setTitle("系统通知");
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.system_notification_list);
        listView.setAdapter(new SystemNotificationAdapter(this));
    }

    @Override
    protected void initData() {

    }
}
