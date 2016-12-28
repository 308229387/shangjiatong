package com.merchantplatform.activity;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ListView;

import com.Utils.SystemNotification;
import com.android.gmacs.R;
import com.android.gmacs.activity.BaseActivity;
import com.callback.DialogCallback;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.merchantplatform.adapter.SystemNotificationXAdapter;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.bean.SystemNotificationList;
import com.okhttputils.OkHttpUtils;
import com.utils.Urls;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationActivity extends BaseActivity {
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

        mXRecyclerView = (XRecyclerView) findViewById(com.merchantplatform.R.id.xrv_callrecord);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        EventBus.getDefault().register(this);
        new Thread() {
            public void run() {
                showData();
            }
        }.start();
        setListener();
    }

    private void showData() {
        temp = SystemNotificationOperate.queryAll(SystemNotificationActivity.this);
        xAdapter = new SystemNotificationXAdapter(SystemNotificationActivity.this, temp);
        mXRecyclerView.setAdapter(xAdapter);
    }

    public void setListener() {
        mXRecyclerView.setLoadingListener(new RecyclerViewLoadingListener());


    }

    @Override
    protected void initData() {

    }

    public void get() {
        Long sortld = null;
        if (temp.size() > 0)
            sortld = temp.get(0).getSortId();
        else
            sortld = Long.valueOf(0);
        OkHttpUtils.get(Urls.SYSTEM_NOTIFICATION)
                .params("sortId", String.valueOf(sortld))
                .execute(new GetSystemNotification(this));
    }


    private class RecyclerViewLoadingListener implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            get();
            mXRecyclerView.setNoMore(true);
        }

        @Override
        public void onLoadMore() {
        }

    }

    private class GetSystemNotification extends DialogCallback<SystemNotificationList> {
        public GetSystemNotification(SystemNotificationActivity context) {
            super(context);
        }

        @Override
        public void onResponse(boolean isFromCache, SystemNotificationList s, Request request, @Nullable Response response) {
            mXRecyclerView.refreshComplete();
            for (SystemNotification a : s.getData()) {
                saveDataToDB(a);
            }
        }

    }

    private void saveDataToDB(SystemNotification temp1) {
        final SystemNotificationDetial data = new SystemNotificationDetial();
        data.setType(temp1.getType());
        data.setTitle(temp1.getTitle());
        data.setSortId(temp1.getSortId());
        data.setId(temp1.getId());
        data.setContent(temp1.getContent());
        data.setContentType(temp1.getContentType());
        data.setDescribe(temp1.getDescribe());
        new Thread() {
            @Override
            public void run() {
                SystemNotificationOperate.insertOrReplace(HyApplication.getApplication(), data);
                xAdapter.notifyDataSetChanged();
            }
        }.start();
    }

    @Subscribe
    public void onEvent(SystemNotification action) {
        temp.clear();
        temp.addAll(SystemNotificationOperate.queryAll(this));
        SystemNotificationOperate.updateData(this);
        xAdapter.notifyDataSetChanged();
    }

}
