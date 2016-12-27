package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.model.GuideActivityModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 * 描    述：引导页
 */

public class GuideActivity extends BaseActivity<GuideActivityModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model.waitAndGo();
    }


    @Override
    public GuideActivityModel createModel() {
        return new GuideActivityModel(this);
    }

}