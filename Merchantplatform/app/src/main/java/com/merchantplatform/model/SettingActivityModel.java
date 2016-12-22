package com.merchantplatform.model;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.activity.PersonalSettingActivity;
import com.merchantplatform.activity.SettingFeedbackActivity;
import com.merchantplatform.activity.SettingPushActivity;
import com.merchantplatform.bean.TempResponse;
import com.okhttputils.OkHttpUtils;
import com.ui.sheet.NormalActionSheet;
import com.Utils.TitleBar;
import com.utils.PageSwitchUtils;
import com.utils.Urls;
import com.utils.UserUtils;
import com.wuba.loginsdk.external.LoginClient;
import com.wuba.wbpush.Push;

import java.util.Arrays;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/12/8.
 */

public class SettingActivityModel  extends BaseModel implements View.OnClickListener{

    private SettingActivity context;
    private TitleBar tb_setting_title;
    private RelativeLayout rl_setting_push,rl_setting_feedback,rl_setting_binding,rl_setting_about,rl_setting_exit;

    private String[] otherLables = new String[]{"退出登录"};

    public SettingActivityModel(SettingActivity context){
        this.context = context;
    }

    public void initView(){
        tb_setting_title = (TitleBar) context.findViewById(R.id.tb_setting_title);
        rl_setting_push = (RelativeLayout) context.findViewById(R.id.rl_setting_push);
        rl_setting_feedback = (RelativeLayout) context.findViewById(R.id.rl_setting_feedback);
        rl_setting_binding = (RelativeLayout) context.findViewById(R.id.rl_setting_binding);
        rl_setting_about = (RelativeLayout) context.findViewById(R.id.rl_setting_about);
        rl_setting_exit = (RelativeLayout) context.findViewById(R.id.rl_setting_exit);
    }

    public void initData(){
        //设置透明状态栏
        tb_setting_title.setImmersive(true);
        //设置背景颜色
        tb_setting_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_setting_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_setting_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        tb_setting_title.setTitle("设置");
        //设置主标题颜色
        tb_setting_title.setTitleColor(Color.BLACK);
    }

    public void setListener(){
        rl_setting_push.setOnClickListener(this);
        rl_setting_feedback.setOnClickListener(this);
        rl_setting_binding.setOnClickListener(this);
        rl_setting_about.setOnClickListener(this);
        rl_setting_exit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rl_setting_push:
                goToSettingPushActivity();
                break;
            case R.id.rl_setting_feedback:
                goToSettingFeedbackActivity();
                break;
            case R.id.rl_setting_binding:
                goToSettingBindingActivity();
                break;
            case R.id.rl_setting_about:
                goToSettingAboutActivity();
                break;
            case R.id.rl_setting_exit:
                exit();
                break;
        }
    }


    private void goToSettingPushActivity(){
        PageSwitchUtils.goToActivity(context,SettingPushActivity.class);
    }

    private void goToSettingFeedbackActivity(){
        PageSwitchUtils.goToActivity(context,SettingFeedbackActivity.class);
    }

    private void goToSettingBindingActivity(){
        PageSwitchUtils.goToActivity(context,PersonalSettingActivity.class);
    }

    private void goToSettingAboutActivity(){
        PageSwitchUtils.goToActivity(context,AboutActivity.class);
    }

    private void exit() {
        NormalActionSheet as = new NormalActionSheet(context);
        as.builder().setTitle("是否确定退出登录").setItems(Arrays.asList(otherLables)).
                setListener(new NormalActionSheet.OnNormalItemClickListener() {
                    @Override
                    public void onClick(String value) {
                        if (value.equals("退出登录")) {
                            logoutOperate();
                        }
                    }
                }).show();
    }

    private void logoutOperate() {
        logout();
        setLogoutStateToPPU();
        setWPushBindsAlias();
        clearUserInfo();
        goToLogin();
    }


    private void logout(){
        OkHttpUtils.get(Urls.LOGOUT)
                .execute(new logoutCallback(context));
    }

    private void setLogoutStateToPPU() {
        LoginClient.doLogoutOperate(context);
    }

    private void setWPushBindsAlias(){
        Push.getInstance().binderAlias("");
    }

    private void clearUserInfo(){
        UserUtils.clearUserInfo(context);
    }

    private void goToLogin() {
        PageSwitchUtils.goToActivity(context, LoginActivity.class);
    }

    private class logoutCallback extends DialogCallback<TempResponse> {

        public logoutCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, TempResponse tempResponse, Request request, @Nullable Response response) {

        }
    }

}
