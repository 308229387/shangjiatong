package com.merchantplatform.model;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.MobileBindChangeActivity;
import com.merchantplatform.activity.PersonalSettingActivity;
import com.ui.TitleBar;
import com.utils.Constant;
import com.utils.PageSwitchUtils;
import com.utils.UserUtils;

import java.util.HashMap;

/**
 * Created by 58 on 2016/12/9.
 */

public class PersonalSettingActivityModel extends BaseModel implements View.OnClickListener{

    private PersonalSettingActivity context;

    private TitleBar tb_binding_title;
    private RelativeLayout rl_binding_change_mobile;
    private TextView tv_binding_mobile,tv_now_binding_mobile;
    private RelativeLayout rl_setting_exit;

    private String mobile;

    public PersonalSettingActivityModel(PersonalSettingActivity context){
        this.context = context;
    }

    public  void initView(){
        tb_binding_title = (TitleBar) context.findViewById(R.id.tb_binding_title);
        rl_binding_change_mobile = (RelativeLayout) context.findViewById(R.id.rl_binding_change_mobile);
        tv_binding_mobile = (TextView) context.findViewById(R.id.tv_binding_mobile);
        tv_now_binding_mobile = (TextView) context.findViewById(R.id.tv_now_binding_mobile);
        rl_setting_exit = (RelativeLayout) context.findViewById(R.id.rl_setting_exit);
    }


    public void initData(){
        initTitleData();
        initMobileData();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_binding_title.setImmersive(true);
        //设置左侧文案
        tb_binding_title.setLeftText("返回");
        //设置标题
        tb_binding_title.setTitle("消息推送");
        //设置左侧点击事件
        tb_binding_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }

    private void initMobileData(){
        String mobile = UserUtils.getMobile();
        tv_now_binding_mobile.setText("已绑定" + mobile);
    }

    public void setListener(){
        rl_binding_change_mobile.setOnClickListener(this);
        rl_setting_exit.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_binding_change_mobile:
                goToMobileChangePage();
                break;
            case R.id.rl_setting_exit:
                break;
        }
    }

    private void goToMobileChangePage(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Constant.MOBILE, mobile);
        PageSwitchUtils.goToActivityWithString(context, MobileBindChangeActivity.class, map);

    }
}
