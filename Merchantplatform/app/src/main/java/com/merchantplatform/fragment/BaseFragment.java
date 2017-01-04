package com.merchantplatform.fragment;

import android.os.Bundle;

import com.merchantplatform.model.BaseModel;

/**
 * Created by SongYongmeng on 2016/11/24.
 *  描    述：此界面为Base抽象类，提供MVC绑定方法
 */

public abstract class BaseFragment<T extends BaseModel> extends android.support.v4.app.Fragment {
    protected T model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initModel();
    }

    private void initModel() {
        model = createModel();
    }

    protected abstract T createModel();

}
