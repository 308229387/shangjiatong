package com.merchantplatform.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.merchantplatform.application.HyApplication;
import com.merchantplatform.model.BaseModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 * 描    述：此界面为Base抽象类，会有MVC绑定、Activity管理等方法。
 */

public abstract class BaseActivity<T extends BaseModel> extends AppCompatActivity {
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
