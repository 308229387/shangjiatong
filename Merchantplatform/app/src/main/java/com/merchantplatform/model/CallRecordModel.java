package com.merchantplatform.model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.callback.DialogCallback;
import com.db.dao.CallDetail;
import com.db.dao.CallList;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.merchantplatform.R;
import com.okhttputils.OkHttpUtils;
import com.okhttputils.callback.AbsCallback;
import com.okhttputils.https.HttpsUtils;
import com.utils.Urls;
import com.xrecyclerview.BaseRecyclerViewAdapter;
import com.merchantplatform.adapter.CallRecordAdapter;
import com.merchantplatform.fragment.CallRecordFragment;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordModel extends BaseModel {

    private CallRecordFragment context;
    private View view;
    private XRecyclerView xrv_callrecord;
    private CallRecordAdapter mAdapter;
    private ArrayList<CallList> listData;

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
        xrv_callrecord.refresh();//刚进来的时候可以直接刷新
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
            listData.clear();
            getRefreshData();
            mAdapter.notifyDataSetChanged();
            xrv_callrecord.refreshComplete();
        }

        @Override
        public void onLoadMore() {
            getLoadMoreData();
        }
    }

    private void getRefreshData() {
        long maxBackTime = CallDetailDaoOperate.queryMaxBackTime(context.getContext());
        getNetResponseData(maxBackTime + "", "1");
    }

    private void getLoadMoreData() {
        ArrayList<CallList> callLists = (ArrayList<CallList>) CallListDaoOperate.queryLimitData(context.getContext(), 20);
        if (callLists != null && callLists.size() > 0) {
            listData.addAll(callLists);
        } else {
//            getNetResponseData();
        }

        if (listData.size() > 50) {
            xrv_callrecord.setNoMore(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    getNetResponseData();
                    mAdapter.notifyDataSetChanged();
                    xrv_callrecord.loadMoreComplete();
                }
            }, 1000);
        }
    }

    private void getNetResponseData(String backTime, String refreshType) {
        OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", refreshType)
                .execute(new getPhoneIncreaseDataResponse(context.getActivity()));
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetail> {

        public getPhoneIncreaseDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetail callDetail, Request request, @Nullable Response response) {

        }
    }

    private class OnCallItemClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(context.getContext(), "position" + position, Toast.LENGTH_SHORT).show();
        }
    }
}