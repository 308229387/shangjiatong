package com.merchantplatform.activity;

import android.app.Activity;
import android.os.Bundle;

import com.merchantplatform.application.HyApplication;
import com.merchantplatform.model.BaseModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 */

public abstract class BaseActivity<T extends BaseModel> extends Activity{
    protected T model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        insert(this);
        initModel();
    }

    private void insert(Activity context) {
        HyApplication.getInstance().addActivity(context);
    }

    private void initModel() {
        model = createModel();
    }

    public abstract T createModel();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        remove(this);
    }

    private void remove(Activity context) {
        HyApplication.getInstance().removeActivity(context);
    }

}
