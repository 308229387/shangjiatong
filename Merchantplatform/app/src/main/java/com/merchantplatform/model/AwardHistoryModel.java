package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.Utils.TitleBar;
import com.Utils.Urls;
import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.AwardHistoryActivity;
import com.merchantplatform.activity.MyAwardActivity;
import com.merchantplatform.adapter.AwardHistoryAdapter;
import com.merchantplatform.bean.AwardHistoryResponse;
import com.okhttputils.OkHttpUtils;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class AwardHistoryModel extends BaseModel {

    private AwardHistoryActivity context;
    private TitleBar tb_my_award_title;
    private XRecyclerView xrv_my_award;
    private View emptyView;
    private TextView tv_award_list_empty;
    private ArrayList<AwardHistoryResponse.AwardHistory> awardHistoryList;
    private AwardHistoryAdapter awardHistoryAdapter;

    public AwardHistoryModel(AwardHistoryActivity context) {
        this.context = context;
    }

    public void initView() {
        tb_my_award_title = (TitleBar) context.findViewById(R.id.tb_my_award_title);
        xrv_my_award = (XRecyclerView) context.findViewById(R.id.xrv_my_award);
        emptyView = context.findViewById(R.id.layout_award_list_empty);
        tv_award_list_empty = (TextView) context.findViewById(R.id.tv_award_list_empty);
        tv_award_list_empty.setText("暂时还没有人中奖哦~");
    }

    public void setTitleBar() {
        tb_my_award_title.setImmersive(true);
        tb_my_award_title.setBackgroundColor(Color.WHITE);
        tb_my_award_title.setLeftImageResource(R.mipmap.title_back);
        tb_my_award_title.setTitle("中奖记录");
        tb_my_award_title.setTitleColor(Color.BLACK);
        tb_my_award_title.setActionTextColor(context.getResources().getColor(R.color.common_text_orange));
    }

    public void setXRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_my_award.setLayoutManager(layoutManager);
    }

    public void setListener() {
        tb_my_award_title.setLeftClickListener(new OnBackPressed());
        tb_my_award_title.addAction(new goToMyAwardAction("我的奖品"));
        xrv_my_award.setLoadingListener(new LoadingListener());
    }

    public void initAdapter() {
        awardHistoryList = new ArrayList<>();
        awardHistoryAdapter = new AwardHistoryAdapter(context, awardHistoryList);
        xrv_my_award.setAdapter(awardHistoryAdapter);
    }

    public void getNewData() {
        emptyView.setVisibility(View.GONE);
        OkHttpUtils.get(Urls.LOTTERY_HISTORY)
                .params("refreshType", "1")
                .execute(new getNewDataResponse(context, false));
    }

    private void getLoadMoreData() {
        OkHttpUtils.get(Urls.LOTTERY_HISTORY)
                .params("id", awardHistoryList.get(awardHistoryList.size() - 1).getId())
                .params("refreshType", "0")
                .execute(new getLoadMoreDataResponse(context, false));
    }

    private class OnBackPressed implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            context.finish();
        }
    }

    private class goToMyAwardAction extends TitleBar.TextAction {

        public goToMyAwardAction(String text) {
            super(text);
        }

        @Override
        public void performAction(View view) {
            Intent intent = new Intent(context, MyAwardActivity.class);
            context.startActivity(intent);
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

    private class getNewDataResponse extends DialogCallback<AwardHistoryResponse> {

        public getNewDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, AwardHistoryResponse awardHistoryResponse, Request request, @Nullable Response response) {
            if (awardHistoryResponse != null && awardHistoryResponse.getData() != null && awardHistoryResponse.getData().size() > 0) {
                awardHistoryList.clear();
                awardHistoryList.addAll(awardHistoryResponse.getData());
                awardHistoryAdapter.notifyDataSetChanged();
                xrv_my_award.refreshComplete();
            } else {
                emptyView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private class getLoadMoreDataResponse extends DialogCallback<AwardHistoryResponse> {

        public getLoadMoreDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, AwardHistoryResponse awardHistoryResponse, Request request, @Nullable Response response) {
            if (awardHistoryResponse != null && awardHistoryResponse.getData() != null) {
                if (awardHistoryResponse.getData().size() > 0) {
                    awardHistoryList.addAll(awardHistoryResponse.getData());
                    awardHistoryAdapter.notifyDataSetChanged();
                    xrv_my_award.loadMoreComplete();
                } else {
                    xrv_my_award.setNoMore(true);
                }
            }
        }
    }
}