package com.merchantplatform.model;

import android.graphics.Color;
import android.view.View;

import com.merchantplatform.R;
import com.merchantplatform.activity.SoftwareProtocolActivity;
import com.ui.TitleBar;

/**
 * Created by 58 on 2016/12/10.
 */

public class SoftwareProtocolActivityModel extends BaseModel {
    private SoftwareProtocolActivity context;

    private TitleBar tb_protocol_title;

    public SoftwareProtocolActivityModel(SoftwareProtocolActivity context){
        this.context = context;
    }

    public void initView(){
        tb_protocol_title = (TitleBar) context.findViewById(R.id.tb_protocol_title);
    }

    public void initData(){
        initTitleData();
    }

    private void initTitleData(){
        //设置透明状态栏
        tb_protocol_title.setImmersive(true);
        //设置背景颜色
        tb_protocol_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_protocol_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_protocol_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        tb_protocol_title.setTitle("软件协议");
        //设置标题颜色
        tb_protocol_title.setTitleColor(Color.BLACK);
    }
}
