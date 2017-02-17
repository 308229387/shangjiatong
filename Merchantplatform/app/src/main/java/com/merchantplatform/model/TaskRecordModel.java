package com.merchantplatform.model;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.Utils.TitleBar;
import com.callback.JsonCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.TaskRecordActivity;
import com.merchantplatform.adapter.TaskRecordAdapter;
import com.merchantplatform.bean.IntegralRecordResponse;
import com.okhttputils.OkHttpUtils;
import com.utils.ToastUtils;
import com.utils.Urls;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by songyongmeng on 2017/2/10.
 */
public class TaskRecordModel extends BaseModel {
    private TaskRecordActivity context;
    private XRecyclerView listRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private TitleBar title;
    private TaskRecordAdapter adapter;
    private ArrayList<IntegralRecordResponse.dataInfo> list = new ArrayList<>();
    private int tag = 1;
    private long id;

    public TaskRecordModel(TaskRecordActivity context) {
        this.context = context;
    }

    public void init() {
        title = (TitleBar) context.findViewById(R.id.tb_setting_title);
        listRecyclerView = (XRecyclerView) context.findViewById(R.id.task_record_recycler);
        mLayoutManager = new LinearLayoutManager(context);
        adapter = new TaskRecordAdapter(context);
        listRecyclerView.setLayoutManager(mLayoutManager);
        listRecyclerView.setHasFixedSize(true);
        listRecyclerView.setAdapter(adapter);
        listRecyclerView.setLoadingListener(new RecyclerViewLoadingListener());
        initData();
        getData();
    }

    public void initData() {
        title.setImmersive(true);
        title.setBackgroundColor(Color.WHITE);
        title.setLeftImageResource(R.mipmap.title_back);
        title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        title.setTitle("积分记录");
        title.setTitleColor(Color.BLACK);
        title.setDividerColor(Color.parseColor("#DFE0E1"));
    }

    public void getData() {
        OkHttpUtils.get(Urls.INTEGRAL_LIST)
                .params("id", String.valueOf(id))
                .params("refreshType", tag + "")
                .execute(new GetTaskRecord());
    }

    private class RecyclerViewLoadingListener implements XRecyclerView.LoadingListener {
        @Override
        public void onRefresh() {
            tag = 1;
            id = 0;
            getData();
        }

        @Override
        public void onLoadMore() {
            tag = 0;
            getData();
        }

    }

    public class GetTaskRecord extends JsonCallback<IntegralRecordResponse> {

        @Override
        public void onResponse(boolean isFromCache, IntegralRecordResponse s, Request request, @Nullable Response response) {
            try {
                if (s.getData().size() > 0 && tag == 1) {
                    list.clear();
                    for (IntegralRecordResponse.dataInfo a : s.getData())
                        list.add(a);
                    id = list.get(list.size() - 1).getId();
                    listRecyclerView.refreshComplete();
                }
                if (s.getData().size() > 0 && tag == 0) {
                    for (IntegralRecordResponse.dataInfo a : s.getData())
                        list.add(a);
                    id = list.get(list.size() - 1).getId();
                    listRecyclerView.loadMoreComplete();
                }
                if(s.getData().size()==0)
                    listRecyclerView.setNoMore(true);


                adapter.setData(list);

            } catch (Exception e) {
                ToastUtils.showToast("解析错误");
            }
        }

    }
}
