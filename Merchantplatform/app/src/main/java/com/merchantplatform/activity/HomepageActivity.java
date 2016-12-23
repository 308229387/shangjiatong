package com.merchantplatform.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.merchantplatform.R;
import com.Utils.JumpSystemNotificationAction;
import com.merchantplatform.model.HomepageModel;
import com.utils.PermissionUtils;

import org.greenrobot.eventbus.Subscribe;

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
        model.getGlobalParams();


    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public HomepageModel createModel() {
        return new HomepageModel(this);
    }

    @Subscribe
    public void onEvent(JumpSystemNotificationAction action) {
        startActivity(new Intent(HomepageActivity.this, SystemNotificationActivity.class));
    }
}