package com.merchantplatform.model;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;

import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.activity.PersonalSettingActivity;
import com.merchantplatform.activity.SettingFeedbackActivity;
import com.merchantplatform.activity.SettingPushActivity;
import com.ui.TitleBar;
import com.utils.PageSwitchUtils;

/**
 * Created by 58 on 2016/12/8.
 */

public class SettingActivityModel  extends BaseModel implements View.OnClickListener{

    private SettingActivity context;
    private TitleBar tb_setting_title;
    private RelativeLayout rl_setting_push,rl_setting_feedback,rl_setting_binding,rl_setting_about;

    public SettingActivityModel(SettingActivity context){
        this.context = context;
    }

    public void initView(){
        tb_setting_title = (TitleBar) context.findViewById(R.id.tb_setting_title);
        rl_setting_push = (RelativeLayout) context.findViewById(R.id.rl_setting_push);
        rl_setting_feedback = (RelativeLayout) context.findViewById(R.id.rl_setting_feedback);
        rl_setting_binding = (RelativeLayout) context.findViewById(R.id.rl_setting_binding);
        rl_setting_about = (RelativeLayout) context.findViewById(R.id.rl_setting_about);

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
}
