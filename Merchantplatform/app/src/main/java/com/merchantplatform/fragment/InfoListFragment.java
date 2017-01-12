package com.merchantplatform.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.infoListModel;

/**
 * 帖子列表Fragment
 */
public class InfoListFragment extends BaseFragment<infoListModel> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = model.initView(inflater, container);//初始化布局
        model.setHeaderHeight();//设置布局高度（沉浸式状态栏）
        model.getData(true);//第一次初始化数据
        return view;
    }

    @Override
    protected infoListModel createModel() {
        return new infoListModel(getActivity());
    }

}
