package com.merchantplatform.model;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.ui.TitleBar;
import com.utils.ToastUtils;

/**
 * Created by 58 on 2016/11/25.
 */
public class AboutActivityModel extends BaseModel{
    private AboutActivity context;

    private ImageView mEditView;

    public  AboutActivityModel(AboutActivity context){
        this.context = context;
    }

    public void initTitleBar(){
        TitleBar titleBar = (TitleBar) context.findViewById(R.id.title_bar);
        //设置透明状态栏
        titleBar.setImmersive(true);
        //设置背景颜色
        titleBar.setBackgroundColor(Color.parseColor("#64b4ff"));
        //设置左侧图标
        titleBar.setLeftImageResource(R.mipmap.title_back);
        //设置左侧文案
        titleBar.setLeftText("返回");
        //设置左侧文案颜色
        titleBar.setLeftTextColor(Color.WHITE);
        //设置左侧点击事件
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        titleBar.setTitle("关于\n软件协议");
        //设置主标题颜色
        titleBar.setTitleColor(Color.WHITE);
        //设置子标题颜色
        titleBar.setSubTitleColor(Color.WHITE);
        //设置标题栏下划线的颜色
        titleBar.setDividerColor(Color.GRAY);
        //设置标题栏下划线的高度
        titleBar.setDividerHeight(1);
        //设置右侧文案颜色
        titleBar.setActionTextColor(Color.WHITE);
        //设置右侧图标
        mEditView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(R.mipmap.title_edit) {
            @Override
            public void performAction(View view) {
                ToastUtils.showToast( "点击了编辑图标");
            }
        });
         //设置右侧文案
        titleBar.addAction(new TitleBar.TextAction("编辑") {
            @Override
            public void performAction(View view) {
                ToastUtils.showToast("点击了编辑文案");
            }
        });
    }

}
