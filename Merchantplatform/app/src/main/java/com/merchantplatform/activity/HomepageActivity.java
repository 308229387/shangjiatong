package com.merchantplatform.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.common.gmacs.utils.ToastUtil;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.merchantplatform.R;
import com.merchantplatform.model.HomepageModel;
import com.Utils.SystemNotification;
import com.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/11/24.
 * 描    述：首页
 */

public class HomepageActivity extends BaseActivity<HomepageModel> {

    @Override
    @Subscribe
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
        if (keyCode == KeyEvent.KEYCODE_BACK)
            moveTaskToBack(false);
        return false;
    }

    @Override
    public HomepageModel createModel() {
        return new HomepageModel(this);
    }


}
