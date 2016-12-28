package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.Utils.JumpSystemNotificationAction;
import com.Utils.SystemGetNotificationInfoAction;
import com.merchantplatform.R;
import com.merchantplatform.model.HomepageModel;

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
        model.createFragmentManager();
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