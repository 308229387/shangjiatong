package com.merchantplatform.model;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.Utils.TitleBar;
import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.adapter.InfoListAdapter;
import com.merchantplatform.bean.InfoListBean;
import com.merchantplatform.bean.InfoListResponse;
import com.okhttputils.OkHttpUtils;
import com.utils.DisplayUtils;
import com.utils.Urls;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linyueyang on 17/1/4.
 */

public class infoListModel extends BaseModel {

    private Activity activity;

    InfoListAdapter infoListAdapter;
    XRecyclerView xrv_post;
    ArrayList<InfoListBean> postBeanList = new ArrayList();
    TitleBar titleBar;

    public infoListModel(Activity context) {
        this.activity = context;
    }

    /**
     * 初始化布局
     *
     * @param inflater
     * @param container
     * @return
     */
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        xrv_post = (XRecyclerView) view.findViewById(R.id.xrv_info);

        //recyclerView设置布局样式
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_post.setLayoutManager(layoutManager);

        infoListAdapter = new InfoListAdapter(activity, postBeanList);
        xrv_post.setAdapter(infoListAdapter);

        xrv_post.setLayoutManager(layoutManager);
        xrv_post.setRefreshProgressStyle(ProgressStyle.BallPulse);
        xrv_post.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        //mXRecyclerView.setEmptyView(emptyView);

        xrv_post.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(true);
            }

            @Override
            public void onLoadMore() {
                getData(false);
                xrv_post.loadMoreComplete();
            }
        });
        return view;
    }

    /**
     * 由于沉浸式状态栏需要设置布局高度
     */
    public void setHeaderHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int height = DisplayUtils.getStatusBarHeight(activity);
            int more = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, activity.getResources().getDisplayMetrics());
            if (titleBar != null) {
                titleBar.setPadding(0, height + more, 0, 0);
            }
        }
    }

    /**
     * 获取数据
     */
    public void getData(final boolean isRefresh) {

        //参数中sortId默认为0，正常读取最后一个item的sortId
        String sortId = "0";
        if (isRefresh) {
            postBeanList.clear();
            xrv_post.setNoMore(false);
        } else {
            if (null != postBeanList && postBeanList.size() > 0)
                sortId = postBeanList.get(postBeanList.size() - 1).getSortId();
        }

        OkHttpUtils.get(Urls.POST_LIST).params("sortId", sortId).execute(
                new DialogCallback<InfoListResponse>(activity) {
                    @Override
                    public void onResponse(boolean isFromCache, InfoListResponse infoListResponse, Request request, @Nullable Response response) {
                        if (infoListResponse != null && infoListResponse.getData() != null && infoListResponse.getData().size() > 0) {
                            postBeanList.addAll(infoListResponse.getData());
                            infoListAdapter.notifyDataSetChanged();

                            if (infoListResponse.getData().size() < 10) {
                                xrv_post.setNoMore(true);
                            }
                        } else {
                            xrv_post.setNoMore(true);
                        }
                        if (isRefresh) {
                            xrv_post.refreshComplete();
                        } else {
                            xrv_post.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        if (isRefresh) {
                            xrv_post.refreshComplete();
                        } else {
                            xrv_post.loadMoreComplete();
                        }
                    }
                });

    }


}
