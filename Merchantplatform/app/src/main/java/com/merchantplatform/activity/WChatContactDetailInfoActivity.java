package com.merchantplatform.activity;

import android.view.View;

import com.android.gmacs.activity.GmacsContactDetailInfoActivity;
import com.utils.ActivityStack;


/**
 * Created by YanQi on 16/8/23.
 */
public class WChatContactDetailInfoActivity extends GmacsContactDetailInfoActivity {

    protected void initView() {
        super.initView();
        ActivityStack.getInstance().addActivity(this);
        mTitleBar.mBackView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ActivityStack.getInstance().finishAll();
                return false;
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        ActivityStack.getInstance().removeActivity(this);
    }
}
