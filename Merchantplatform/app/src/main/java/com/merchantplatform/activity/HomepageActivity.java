package com.merchantplatform.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.merchantplatform.R;
import com.merchantplatform.model.HomepageModel;
import com.ui.HomepageBottomButton;

/**
 * Created by SongYongmeng on 2016/11/24.
 * 描    述：首页
 */

public class HomepageActivity extends BaseActivity<HomepageModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        model.init();

        model.createFragment();
        model.createFragmentManagerAndShow();
    }

    @Override
    public HomepageModel createModel() {
        return new HomepageModel(this);
    }
}
