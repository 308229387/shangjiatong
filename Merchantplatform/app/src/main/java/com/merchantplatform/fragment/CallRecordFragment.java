package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.dao.CallList;
import com.merchantplatform.model.CallRecordModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CallRecordFragment extends BaseFragment<CallRecordModel> {

    private int tabIndex;
    private boolean isFirstVisible = true;
    private boolean isPrepared;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tabIndex = bundle.getInt("tabIndex");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPrepare();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize(inflater, container);
        setTabIndex(tabIndex);
        initAdapter();
        return model.getView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFirstVisible) {
            isFirstVisible = false;
            initPrepare();
        }
    }

    private synchronized void initPrepare() {
        if (isPrepared) {
            model.firstRefreshData();
        } else {
            isPrepared = true;
        }
    }

    private void initialize(LayoutInflater inflater, ViewGroup container) {
        model.initView(inflater, container);
        model.setListener();
        model.registPhoneBroadcast();
        model.registEventBus();
    }

    private void setTabIndex(int tabIndex) {
        model.setTabIndex(tabIndex);
    }

    private void initAdapter() {
        model.initAdapter();
    }

    public static CallRecordFragment newInstance(int tabIndex) {
        Bundle args = new Bundle();
        args.putInt("tabIndex", tabIndex);
        CallRecordFragment callRecordFragment = new CallRecordFragment();
        callRecordFragment.setArguments(args);
        return callRecordFragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deleteFromAdapterList(CallList clickCallList) {
        model.deleteFromAdapterList(clickCallList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.releasePhoneMonitor();
        model.unregistEventBus();
    }

    @Override
    protected CallRecordModel createModel() {
        return new CallRecordModel(this);
    }
}