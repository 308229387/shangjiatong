package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.PersonalCenterModel;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class PersonalCenterFragment extends BaseFragment<PersonalCenterModel> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializationLayout(inflater, container);
        return model.getView();
    }

    private void initializationLayout(LayoutInflater inflater, ViewGroup container) {
        model.createView(inflater, container);
        model.initView();
        model.setHeaderHeight();
        model.setListener();
    }

    @Override
    protected PersonalCenterModel createModel() {
        return new PersonalCenterModel(this);
    }
}
