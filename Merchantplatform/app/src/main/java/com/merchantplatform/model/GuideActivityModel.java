package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.activity.SystemMessageActivity;
import com.merchantplatform.application.HyApplication;
import com.utils.PageSwitchUtils;
import com.utils.UserUtils;
import com.wuba.loginsdk.external.LoginClient;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;
    private ImageView imageView_glide_test;

    private static final long DELAYED_TIMES = 3 * 1000;
    private Handler handler = new Handler();

    public GuideActivityModel(Activity context) {
        this.context = context;
    }

    public void initLayout() {
        imageView_glide_test = (ImageView) context.findViewById(R.id.imageView_glide_test);
        Glide.with(context).load("http://img.58cdn.com.cn/logo/58/252_84/logo-o.png?v=2").into(imageView_glide_test);
    }

    public void waitAndGo() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToWhere();
            }
        }, DELAYED_TIMES);
    }

    private void goToWhere(){
        judgeHowManyComeAfterGo();
        finish();
    }

    private void judgeHowManyComeAfterGo() {
        if (neverCome()) {
            PageSwitchUtils.goToActivity(context, LoginActivity.class);
        } else {
            PageSwitchUtils.goToActivity(context, HomepageActivity.class);
        }
    }

    private boolean neverCome(){
        return TextUtils.isEmpty(UserUtils.getUserId())
                || !UserUtils.isValidate(context)
                || TextUtils.isEmpty(LoginClient.doGetPPUOperate(HyApplication.getApplication()));
    }

    private void finish() {
        context.finish();
    }
}