package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.InfoDetailModel;

public class InfoDetailActivity extends BaseActivity<InfoDetailModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        model.initView();
        model.initData();
    }

    @Override
    public InfoDetailModel createModel() {
        return new InfoDetailModel(this);
    }

}
