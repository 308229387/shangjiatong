package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.DailyLotteryModel;

public class DailyLotteryActivity extends BaseActivity<DailyLotteryModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_lottery);
        initView();
        initData();
    }

    private void initView() {
        model.initView();
        model.setTitleBar();
        model.setLotteryAward();
        model.setLotteryExplain();
        model.setListener();
    }

    private void initData() {
        model.getServerTime();
        model.initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.destroyDialog();
    }

    @Override
    public DailyLotteryModel createModel() {
        return new DailyLotteryModel(this);
    }
}