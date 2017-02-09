package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.WelfareModel;

/**
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
        model.dealWithTimeToResult();
        model.setTextToCountDown();
    }

    private void initializationLayout(LayoutInflater inflater, ViewGroup container) {
        model.createView(inflater, container);
    }

    @Override
    protected WelfareModel createModel() {
        return new WelfareModel(this);
    }
}
