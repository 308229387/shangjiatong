package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.GuideActivityModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 * 描    述：引导页
 */

public class GuideActivity extends BaseActivity<GuideActivityModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        model.showToast();
        model.initLayout();
    }

    @Override
    public GuideActivityModel createModel() {
        return new GuideActivityModel(this);
    }

}
