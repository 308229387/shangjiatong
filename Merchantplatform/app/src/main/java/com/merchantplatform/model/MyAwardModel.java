package com.merchantplatform.model;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.Utils.TitleBar;
import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.MyAwardActivity;
import com.merchantplatform.adapter.MyAwardAdapter;
import com.merchantplatform.bean.MyAwardResponse;
import com.okhttputils.OkHttpUtils;
import com.utils.Urls;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

public class MyAwardModel extends BaseModel {

    private MyAwardActivity context;
    private TitleBar tb_my_award_title;
    private XRecyclerView xrv_my_award;
    private ArrayList<MyAwardResponse.award> myAwardList;
    private MyAwardAdapter myAwardAdapter;

    public MyAwardModel(MyAwardActivity context) {
        this.context = context;
    }

    public void initView() {
        tb_my_award_title = (TitleBar) context.findViewById(R.id.tb_my_award_title);
        xrv_my_award = (XRecyclerView) context.findViewById(R.id.xrv_my_award);
    }

    public void setTitleBar() {
        tb_my_award_title.setImmersive(true);
        tb_my_award_title.setBackgroundColor(Color.WHITE);
        tb_my_award_title.setLeftImageResource(R.mipmap.title_back);
        tb_my_award_title.setTitle("我的奖品");
        tb_my_award_title.setTitleColor(Color.BLACK);
    }

    public void setXRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_my_award.setLayoutManager(layoutManager);
//        xrv_my_award.setEmptyView();
    }

    public void setListener() {
        tb_my_award_title.setLeftClickListener(new OnBackPressed());
        xrv_my_award.setLoadingListener(new LoadingListener());
    }

    public void initAdapter() {
        myAwardList = new ArrayList<>();
        myAwardAdapter = new MyAwardAdapter(context, myAwardList);
        xrv_my_award.setAdapter(myAwardAdapter);
    }

    public void getNewData() {
        OkHttpUtils.get(Urls.MY_AWARD_LIST)
                .params("refreshType", "1")
                .execute(new getNewDataResponse(context, false));
    }

    private void getLoadMoreData() {
        OkHttpUtils.get(Urls.MY_AWARD_LIST)
                .params("id", myAwardList.get(myAwardList.size() - 1).getId())
                .params("refreshType", "0")
                .execute(new getLoadMoreDataResponse(context, false));
    }

    private class OnBackPressed implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            context.finish();
        }
    }

    private class LoadingListener implements XRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            getNewData();
        }

        @Override
        public void onLoadMore() {
            getLoadMoreData();
        }
    }

    private class getNewDataResponse extends DialogCallback<MyAwardResponse> {

        public getNewDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, MyAwardResponse myAwardResponse, Request request, @Nullable Response response) {
            if (myAwardResponse != null && myAwardResponse.getData() != null) {
                myAwardList.clear();
                myAwardList.addAll(myAwardResponse.getData());
                myAwardAdapter.notifyDataSetChanged();
            }
        }
    }

    private class getLoadMoreDataResponse extends DialogCallback<MyAwardResponse> {

        public getLoadMoreDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, MyAwardResponse myAwardResponse, Request request, @Nullable Response response) {
            if (myAwardResponse != null && myAwardResponse.getData() != null) {
                myAwardList.addAll(myAwardResponse.getData());
                myAwardAdapter.notifyDataSetChanged();
            }
        }
    }
}
