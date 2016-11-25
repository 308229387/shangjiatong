package com.merchantplatform.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.merchantplatform.application.HyApplication;
import com.merchantplatform.model.BaseModel;
import com.utils.AppInfoUtils;

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
        initNoTitle();
        immersionStatusBar();
    }

    private void insert(Activity context) {
        HyApplication.getInstance().addActivity(context);
    }

    private void initModel() {
        model = createModel();
    }

    private void initNoTitle(){
        //继承Activity 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //继承AppCompatActivity 隐藏标题栏
        getSupportActionBar().hide();
}

    private void immersionStatusBar(){
        if (AppInfoUtils.hasKitKat() && !AppInfoUtils.hasLollipop()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (AppInfoUtils.hasLollipop()) {
            moreThanLollipopStatusBar();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void moreThanLollipopStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
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
