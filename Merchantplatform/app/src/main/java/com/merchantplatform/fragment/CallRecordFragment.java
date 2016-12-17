package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.CallRecordModel;

public class CallRecordFragment extends BaseFragment<CallRecordModel> {

    private int tabIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tabIndex = bundle.getInt("tabIndex");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initialize(inflater, container);
        setTabIndex(tabIndex);
        initAdapter();
        return model.getView();
    }

    private void initialize(LayoutInflater inflater, ViewGroup container) {
        model.initView(inflater, container);
        model.setListener();
        model.registPhoneBroadcast();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        model.destroyFragment();
    }

    @Override
    protected CallRecordModel createModel() {
        return new CallRecordModel(this);
    }
}