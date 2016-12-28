package com.merchantplatform.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.Utils.SystemGetNotificationInfoAction;
import com.Utils.SystemNotificationInfoAction;
import com.common.gmacs.utils.ToastUtil;
import com.merchantplatform.R;
import com.Utils.JumpSystemNotificationAction;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.model.HomepageModel;
import com.ui.dialog.LogoutDialog;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/24.
 * 描    述：首页
 */

public class HomepageActivity extends BaseActivity<HomepageModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        model.saveBundle(savedInstanceState);
        model.createFragmentManager();
        model.init();
        model.createFragment();
        model.createFragmentManagerAndShow();
        model.getGlobalParams();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
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

    @Subscribe
    public void onEvent(SystemGetNotificationInfoAction action) {
        model.getSystemNotificationInfo();
    }

}