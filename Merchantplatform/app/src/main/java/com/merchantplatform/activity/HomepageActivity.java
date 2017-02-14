package com.merchantplatform.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.Utils.JumpExtendAction;
import com.Utils.JumpSystemNotificationAction;
import com.Utils.ShowRedDotSystemNotificationAction;
import com.Utils.SystemGetNotificationInfoAction;
import com.Utils.eventbus.IMKickoffEvent;
import com.Utils.eventbus.IMReconnectEvent;
import com.db.helper.SystemNotificationOperate;
import com.merchantplatform.R;
import com.merchantplatform.model.HomepageModel;
import com.utils.IMLoginUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    public void onResume(){
        super.onResume();
        model.showImKickoffDialog();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.unregister();
    }

    @Subscribe
    public void onEvent(JumpSystemNotificationAction action) {
        startActivity(new Intent(HomepageActivity.this, SystemNotificationActivity.class));
        EventBus.getDefault().post(new ShowRedDotSystemNotificationAction("dismiss"));
        new Thread() {
            public void run() {
                SystemNotificationOperate.updateDataRedDot(HomepageActivity.this);
            }
        }.start();

    }

    @Subscribe
    public void onEvent(JumpExtendAction action) {
        startActivity(new Intent(HomepageActivity.this, PromoteMessageActivity.class));
    }

    @Subscribe
    public void onEvent(SystemGetNotificationInfoAction action) {
        model.getSystemNotificationInfo();
    }

    @Subscribe
    public void onEvent(IMReconnectEvent action) {
        new IMLoginUtils(this);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMKickoffEvent action) {
        model.showImKickoffDialog();
    }


}