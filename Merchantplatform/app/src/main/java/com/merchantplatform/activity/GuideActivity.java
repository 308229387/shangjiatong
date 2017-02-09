package com.merchantplatform.activity;

import android.support.annotation.NonNull;

import com.merchantplatform.model.GuideActivityModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 * 描    述：引导页
 */

public class GuideActivity extends BaseActivity<GuideActivityModel> {


    @Override
    protected void onStart() {
        super.onRestart();
        model.getPhoneStatePermission();
        model.getServiceTime();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
       model.requestPermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.destoryDialog();
    }

    @Override
    public GuideActivityModel createModel() {
        return new GuideActivityModel(this);
    }

}