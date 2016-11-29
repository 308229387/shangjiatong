package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.CallRecordModel;

public class CallRecordFragment extends BaseFragment<CallRecordModel> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializationLayout(inflater, container);
        initAdapter();
        return model.getView();
    }

    private void initializationLayout(LayoutInflater inflater, ViewGroup container) {
        model.createView(inflater, container);
    }

    private void initAdapter() {
        model.initAdapter();
    }

    @Override
    protected CallRecordModel createModel() {
        return new CallRecordModel(this);
    }
}