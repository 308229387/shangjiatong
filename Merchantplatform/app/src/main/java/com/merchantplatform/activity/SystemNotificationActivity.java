package com.merchantplatform.activity;


import android.os.Bundle;
import android.widget.ListView;

import com.android.gmacs.R;
import com.android.gmacs.activity.BaseActivity;
import com.merchantplatform.adapter.SystemNotificationAdapter;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationActivity extends BaseActivity {
    private ListView listView;
    private SystemNotificationAdapter adapter;

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
        adapter = new SystemNotificationAdapter(this);
        listView.setAdapter(adapter);
    }

    @Override
    protected void initData() {
        ArrayList<SystemNotificationDetial> temp = SystemNotificationOperate.queryAll(this);
        adapter.setData(temp);
    }

}
