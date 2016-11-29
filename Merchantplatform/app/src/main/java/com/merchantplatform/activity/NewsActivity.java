package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.NewsActivityModel;

/**
 * Created by 58 on 2016/11/29.
 */

public class NewsActivity extends BaseActivity<NewsActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initData();
    }

    private void initData(){
        model.initData();
    }

    @Override
    public NewsActivityModel createModel() {
        return new NewsActivityModel(this);
    }
}
