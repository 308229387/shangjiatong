package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.HomepageModel;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class Homepage extends BaseActivity<HomepageModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
    }

    @Override
    public HomepageModel createModel() {
        return new HomepageModel(this);
    }
}
