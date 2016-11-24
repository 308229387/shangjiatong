package com.merchantplatform.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.model.Fragment1Model;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class Fragment1 extends BaseFragment<Fragment1Model> {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initializationLayout(inflater, container);
        return model.getView();
    }

    private void initializationLayout(LayoutInflater inflater, ViewGroup container) {
        model.createView(inflater, container);
    }



    @Override
    protected Fragment1Model createModel() {
        return new Fragment1Model(this);
    }
}
