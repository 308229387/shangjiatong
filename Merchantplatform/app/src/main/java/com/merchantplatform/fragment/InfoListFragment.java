package com.merchantplatform.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Utils.eventbus.BaseActionEvent;
import com.Utils.eventbus.PrecisionPromoteSuccessEvent;
import com.Utils.eventbus.UpPromoteSuccessEvent;
import com.merchantplatform.model.infoListModel;
import com.utils.StringUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 帖子列表Fragment
 */
public class InfoListFragment extends BaseFragment<infoListModel> {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = model.initView(inflater, container);//初始化布局
        //model.setHeaderHeight();//设置布局高度（沉浸式状态栏）
        model.getData(true);//第一次初始化数据
        model.registerEventBus();
        return view;
    }

    @Override
    protected infoListModel createModel() {
        return new infoListModel(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(PrecisionPromoteSuccessEvent event) {
        refresh(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(UpPromoteSuccessEvent event) {
        refresh(event);
    }

    private void refresh(BaseActionEvent event) {
        String infoId = null;
        try {
            infoId = (String) event.getData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtil.isNotEmpty(infoId))
            model.refreshItem(infoId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.unRegisterEventBus();
    }


}
