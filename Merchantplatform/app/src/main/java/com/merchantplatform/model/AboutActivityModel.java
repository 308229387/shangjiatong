package com.merchantplatform.model;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.SoftwareProtocolActivity;
import com.Utils.TitleBar;
import com.utils.AppInfoUtils;
import com.utils.PageSwitchUtils;

/**
 * Created by 58 on 2016/11/25.
 */
public class AboutActivityModel extends BaseModel{
    private AboutActivity context;

    private TitleBar titleBar;
    private TextView tv_version;
    private TextView tv_protocol;

    private String name;

    public  AboutActivityModel(AboutActivity context){
        this.context = context;
    }

    public void initView(){
        titleBar = (TitleBar) context.findViewById(R.id.title_bar);
        tv_version = (TextView) context.findViewById(R.id.tv_version);
        tv_protocol = (TextView) context.findViewById(R.id.tv_protocol);
    }

    public void initTitleBar(){
        //设置透明状态栏
        titleBar.setImmersive(true);
        //设置背景颜色
        titleBar.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        titleBar.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        titleBar.setTitle("关于");
        //设置主标题颜色
        titleBar.setTitleColor(Color.BLACK);
        //设置标题栏下划线
        titleBar.setDividerColor(Color.parseColor("#DFE0E1"));
    }

    public void setVersionName() {
        try {
            name = "v" + AppInfoUtils.getVersionName(context);
        } catch (PackageManager.NameNotFoundException e) {
            name = "v 1.0.0";
            e.printStackTrace();
        }
        tv_version.setText("版本号:" + name);
    }

    public void setSoftwareUsage(){
        tv_protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_SHEZHIXQY_YHXY);
                oToSoftWareUsagePage();
            }
        });
    }

    private void oToSoftWareUsagePage(){
        PageSwitchUtils.goToActivity(context,SoftwareProtocolActivity.class);
    }

}
