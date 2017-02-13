package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.AwardHistoryModel;

public class AwardHistoryActivity extends BaseActivity<AwardHistoryModel> {

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
    public AwardHistoryModel createModel() {
        return new AwardHistoryModel(this);
    }
}