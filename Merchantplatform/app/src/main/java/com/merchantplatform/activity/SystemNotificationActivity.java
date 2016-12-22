package com.merchantplatform.activity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ListView;

import com.android.gmacs.R;
import com.android.gmacs.activity.BaseActivity;
import com.common.gmacs.utils.ToastUtil;
import com.merchantplatform.adapter.SystemNotificationAdapter;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.merchantplatform.adapter.SystemNotificationXAdapter;
import com.merchantplatform.model.CallRecordModel;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationActivity extends BaseActivity {
    private ListView listView;
    private SystemNotificationAdapter adapter;
    private SystemNotificationXAdapter xAdapter;
    private XRecyclerView mXRecyclerView;
    ArrayList<SystemNotificationDetial> temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_notification_layout);
        setBackEnable(false);
        setTitle("系统通知");
    }

    @Override
    protected void initView() {
//        listView = (ListView) findViewById(R.id.system_notification_list);
//        adapter = new SystemNotificationAdapter(this);
//        listView.setAdapter(adapter);

        mXRecyclerView = (XRecyclerView) findViewById(com.merchantplatform.R.id.xrv_callrecord);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        temp = SystemNotificationOperate.queryAll(this);
        setListener();
    }

    public void setListener() {
        mXRecyclerView.setLoadingListener(new RecyclerViewLoadingListener());
        adapter = new SystemNotificationAdapter(this);
        xAdapter = new SystemNotificationXAdapter(this, temp);
        mXRecyclerView.setAdapter(xAdapter);
    }

    @Override
    protected void initData() {

    }


    private class RecyclerViewLoadingListener implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            ToastUtil.showToast("刷新");
        }

        @Override
        public void onLoadMore() {
            ToastUtil.showToast("加载");
        }
    }
}
