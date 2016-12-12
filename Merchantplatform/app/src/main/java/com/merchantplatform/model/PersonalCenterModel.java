package com.merchantplatform.model;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.ui.TitleBar;
import com.utils.PageSwitchUtils;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class PersonalCenterModel extends BaseModel implements View.OnClickListener{
    private PersonalCenterFragment context;
    private View view;
    private TitleBar tb_personal_title;
    private RelativeLayout rl_personal_funding, rl_personal_setting;


    public PersonalCenterModel(PersonalCenterFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_personal_center, container, false);
    }

    public void initView(){
        tb_personal_title = (TitleBar) view.findViewById(R.id.tb_personal_title);
        rl_personal_funding = (RelativeLayout) view.findViewById(R.id.rl_personal_funding);
        rl_personal_setting = (RelativeLayout) view.findViewById(R.id.rl_personal_setting);
    }

    public void initData(){
        //设置透明状态栏
        tb_personal_title.setImmersive(true);
        //设置背景颜色
        tb_personal_title.setBackgroundColor(Color.WHITE);
        //设置标题
        tb_personal_title.setTitle("我");
        //设置标题颜色
        tb_personal_title.setTitleColor(Color.BLACK);
    }

    public void setListener(){
        rl_personal_funding.setOnClickListener(this);
        rl_personal_setting.setOnClickListener(this);
    }

    public View getView() {

        return view;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rl_personal_funding:
                goToFundingActivity();
                break;
            case R.id.rl_personal_setting:
                goToSettingActivity();
                break;
        }
    }

    private void goToFundingActivity(){
        PageSwitchUtils.goToActivity(context.getActivity(),FundingManageActivity.class);
    }

    private void goToSettingActivity(){
        PageSwitchUtils.goToActivity(context.getActivity(),SettingActivity.class);
    }

}
