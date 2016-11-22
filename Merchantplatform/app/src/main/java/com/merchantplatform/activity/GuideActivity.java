package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.GuideActivityModel;


public class GuideActivity extends BaseActivity<GuideActivityModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        model.showToast();
    }

    @Override
    public GuideActivityModel createModel() {
        return new GuideActivityModel(this);
    }

}
