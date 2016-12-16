package com.merchantplatform.activity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.merchantplatform.R;
import com.merchantplatform.model.GuideActivityModel;
import com.orhanobut.logger.Logger;
import com.utils.AppInfoUtils;
import com.utils.PermissionUtils;

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
        PermissionUtils.getIMEIPermission(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtils.CODE_READ_PHONE_STATE){
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                AppInfoUtils.getIMEI(this);
            }else{
                finish();
            }
        }
    }

    @Override
    public GuideActivityModel createModel() {
        return new GuideActivityModel(this);
    }

}
