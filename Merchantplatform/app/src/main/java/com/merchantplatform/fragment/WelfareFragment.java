package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.Utils.eventbus.ShareWechatCircleSuccessEvent;
import com.Utils.eventbus.UpPromoteFirstSuccessEvent;
import com.merchantplatform.model.WelfareModel;
import com.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/*
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareFragment extends BaseFragment<WelfareModel> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializationLayout(inflater, container);
        getTask();
        return model.getView();
    }

    private void getTask() {
        model.getTask();
        model.getWelfare();
    }

    private void initializationLayout(LayoutInflater inflater, ViewGroup container) {
        model.createView(inflater, container);
        model.setting();
        model.registEventBus();
    }

    @Subscribe
    public void onEvent(ShareWechatCircleSuccessEvent action) {
        model.shareSuccess();
    }

    @Subscribe
    public void onEvent(UpPromoteFirstSuccessEvent action) {
        model.shareSuccess();
    }

    @Override
    protected WelfareModel createModel() {
        return new WelfareModel(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
