package com.merchantplatform.model;


import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;

import com.dataStore.SettingPushPreferUtil;
import com.merchantplatform.R;
import com.merchantplatform.activity.SettingPushActivity;
import com.ui.SettingToggleGroup;
import com.Utils.TitleBar;

/**
 * Created by 58 on 2016/12/9.
 */

public class SettingPushActivityModel extends BaseModel{
    private SettingPushActivity context;

    private TitleBar tb_push_title;
    private SettingToggleGroup sg_entry_group_items;

    public SettingPushActivityModel(SettingPushActivity context){
        this.context = context;
    }

    public void initView(){
        tb_push_title = (TitleBar) context.findViewById(R.id.tb_push_title);
        sg_entry_group_items = (SettingToggleGroup) context.findViewById(R.id.sg_entry_group_items);
    }

    public void initData(){
        initTitleData();
        initPushData();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_push_title.setImmersive(true);
        //设置背景颜色
        tb_push_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_push_title.setLeftImageResource(R.mipmap.title_back);
        //设置主标题颜色
        tb_push_title.setTitleColor(Color.BLACK);
        //设置标题
        tb_push_title.setTitle("消息推送");
        //设置左侧点击事件
        tb_push_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }

    private void initPushData(){
       boolean soundState = SettingPushPreferUtil.getInstance(context).isPushSoundAlertOpened();
        sg_entry_group_items.addEntry().title("声音提醒").toggleChecked(soundState)
                .toggleChangeClick(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SettingPushPreferUtil.getInstance(context).savePushSoundAlertState(buttonView.isChecked());
                    }
        });

        boolean vibrateState = SettingPushPreferUtil.getInstance(context).isPushVibrateAlertOpened();
        sg_entry_group_items.addEntry().title("震动提醒").toggleChecked(vibrateState)
                .toggleChangeClick(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SettingPushPreferUtil.getInstance(context).savePushVibrateAlertState(buttonView.isChecked());
                    }
        });
    }


}
