package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.DailyLotteryModel;

public class DailyLotteryActivity extends BaseActivity<DailyLotteryModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_lottery);
        initView();
        initData(getIntent());
    }

    private void initView() {
        model.initView();
        model.setTitleBar();
        model.setListener();
    }

    private void initData(Intent intent) {
        model.initData(intent);
    }

    @Override
    public DailyLotteryModel createModel() {
        return new DailyLotteryModel(this);
    }
}