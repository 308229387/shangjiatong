package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.MyAwardModel;

public class MyAwardActivity extends BaseActivity<MyAwardModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_award);
        initView();
        initData();
    }

    private void initView() {
        model.initView();
        model.setTitleBar();
        model.setXRecyclerView();
        model.setListener();
    }

    private void initData() {
        model.initAdapter();
        model.getNewData();
    }

    @Override
    public MyAwardModel createModel() {
        return new MyAwardModel(this);
    }
}
