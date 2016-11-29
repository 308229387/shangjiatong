package com.merchantplatform.model;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.adapter.BaseRecyclerViewAdapter;
import com.merchantplatform.adapter.CallRecordAdapter;
import com.merchantplatform.bean.CallRecordBean;
import com.merchantplatform.fragment.CallRecordFragment;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordModel extends BaseModel {

    private CallRecordFragment context;
    private View view;
    private XRecyclerView xrv_callrecord;
    private CallRecordAdapter mAdapter;
    private ArrayList<CallRecordBean> listData;

    public CallRecordModel(CallRecordFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        initView(inflater, container);
        setListener();
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_call_record, container, false);
        xrv_callrecord = (XRecyclerView) view.findViewById(R.id.xrv_callrecord);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_callrecord.setLayoutManager(layoutManager);
        xrv_callrecord.setRefreshProgressStyle(ProgressStyle.BallPulse);
        xrv_callrecord.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        Drawable dividerDrawable = ContextCompat.getDrawable(context.getContext(), R.drawable.divider_sample);
        xrv_callrecord.addItemDecoration(xrv_callrecord.new DividerItemDecoration(dividerDrawable));//Item之间添加分割线
    }

    private void setListener() {
        xrv_callrecord.setLoadingListener(new RecyclerViewLoadingListener());
    }


    public void initAdapter() {
        initData();
        setAdapterListener();
    }

    private void initData() {
        listData = new ArrayList<>();
        mAdapter = new CallRecordAdapter(context.getContext(), listData);
        xrv_callrecord.setAdapter(mAdapter);
        xrv_callrecord.refresh();//刚进来的时候可以直接进行拉接口
    }

    private void setAdapterListener() {
        mAdapter.setOnItemClickListener(new OnCallItemClickListener());
    }

    public View getView() {
        return view;
    }

    private class RecyclerViewLoadingListener implements XRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            //模拟网络加载的等待时间
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    listData.clear();
                    getNetResponseData();
                    mAdapter.notifyDataSetChanged();
                    xrv_callrecord.refreshComplete();
                }
            }, 1000);
        }

        @Override
        public void onLoadMore() {
            if (listData.size() > 50) {
                xrv_callrecord.setNoMore(true);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNetResponseData();
                        mAdapter.notifyDataSetChanged();
                        xrv_callrecord.loadMoreComplete();
                    }
                }, 1000);
            }
        }
    }

    private void getNetResponseData() {
        int dataSize = listData.size();
        for (int i = dataSize; i < dataSize + 10; i++) {
            CallRecordBean callRecordBean = new CallRecordBean();
            callRecordBean.setCallRecordId(i);
            callRecordBean.setPhoneNum("1388888888" + i);
            callRecordBean.setCallCount(i);
            callRecordBean.setCallTime("08:" + i);
            callRecordBean.setCallType(i + "");
            callRecordBean.setPhoneNumCity("北京");
            callRecordBean.setPhoneState("呼入");
            listData.add(callRecordBean);
        }
    }

    private class OnCallItemClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, Object item) {
            Toast.makeText(context.getContext(), "点击了Item" + ((CallRecordBean) item).getCallRecordId() + "可以查看详情", Toast.LENGTH_SHORT).show();
        }
    }
}