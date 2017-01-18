package com.merchantplatform.model;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.adapter.InfoListAdapter;
import com.merchantplatform.bean.InfoDetailBean;
import com.merchantplatform.bean.InfoDetailResponse;
import com.merchantplatform.bean.InfoListBean;
import com.merchantplatform.bean.InfoListResponse;
import com.okhttputils.OkHttpUtils;
import com.utils.Constant;
import com.utils.Urls;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linyueyang on 17/1/4.
 */

public class infoListModel extends BaseModel {

    private Fragment fragment;

    InfoListAdapter infoListAdapter;
    XRecyclerView xrv_post;
    ArrayList<InfoListBean> postBeanList = new ArrayList();
    //View title_bar;

    private final static int PAGE_SIZE = 10;

    public infoListModel(Fragment fragment) {
        this.fragment = fragment;
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

        //title_bar = view.findViewById(R.id.title_bar);
        xrv_post = (XRecyclerView) view.findViewById(R.id.xrv_info);

        //recyclerView设置布局样式
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_post.setLayoutManager(layoutManager);

        infoListAdapter = new InfoListAdapter(fragment.getActivity(), postBeanList);
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
     * 获取数据
     */
    public void getData(final boolean isRefresh) {

        //参数中sortId默认为0，正常读取最后一个item的sortId
        String sortId = "0";
        if (isRefresh) {
            postBeanList.clear();
        } else {
            if (null != postBeanList && postBeanList.size() > 0)
                sortId = postBeanList.get(postBeanList.size() - 1).getSortId();
        }

        OkHttpUtils.get(Urls.POST_LIST).params(Constant.SORTID, sortId).execute(
                new DialogCallback<InfoListResponse>(fragment.getActivity()) {
                    @Override
                    public void onResponse(boolean isFromCache, InfoListResponse infoListResponse, Request request, @Nullable Response response) {
                        if (infoListResponse != null && infoListResponse.getData() != null && infoListResponse.getData().size() > 0) {
                            postBeanList.addAll(infoListResponse.getData());
                            infoListAdapter.notifyDataSetChanged();

                            if (infoListResponse.getData().size() < PAGE_SIZE) {
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

    public void refreshItem(String infoId) {
        OkHttpUtils.get(Urls.POST_DETAIL).params(Constant.INFOID, infoId).execute(
                new DialogCallback<InfoDetailResponse>(fragment.getActivity()) {
                    @Override
                    public void onResponse(boolean isFromCache, InfoDetailResponse infoDetailResponse, Request request, @Nullable Response response) {
                        InfoDetailBean infoDetailBean = infoDetailResponse.getData();
                        if (infoDetailBean.getJzShow() == 1)//1为显示精准 0为不显示
                            infoListAdapter.getClickViewHolder().setVisible(R.id.tv_info_list_isaccurate, true);
                        else
                            infoListAdapter.getClickViewHolder().setVisible(R.id.tv_info_list_isaccurate, false);
                        if (infoDetailBean.getTopShow() == 1)//1为显示置顶 0为不显示
                            infoListAdapter.getClickViewHolder().setVisible(R.id.tv_info_list_istop, true);
                        else
                            infoListAdapter.getClickViewHolder().setVisible(R.id.tv_info_list_istop, false);
                    }
                });
    }

    public void registerEventBus() {
        EventBus.getDefault().register(fragment);
    }

    public void unRegisterEventBus() {
        EventBus.getDefault().unregister(fragment);
    }


}
