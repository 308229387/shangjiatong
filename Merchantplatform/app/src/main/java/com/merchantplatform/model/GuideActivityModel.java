package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.db.dao.CallDetail;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.DbManager;
import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.activity.MobileValidateActivity;
import com.merchantplatform.activity.PushActivity;
import com.merchantplatform.activity.SystemMessageActivity;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;
    private TextView text;
    private Button button;
    private Button button_bugly_test;
    private Button button_message;
    private ImageView imageView_glide_test;
    private Button button_push;

    public GuideActivityModel(Activity context) {
        this.context = context;
    }

    public void initLayout() {
        text = (TextView) context.findViewById(R.id.text_click);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LoginActivity.class));
            }
        });

        button = (Button) context.findViewById(R.id.button_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MobileValidateActivity.class));
            }
        });
        button_bugly_test = (Button) context.findViewById(R.id.button_bugly_test);
        button_bugly_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.testJavaCrash();
            }
        });
        button_message =  (Button) context.findViewById(R.id.button_message);
        button_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SystemMessageActivity.class));
            }
        });
        imageView_glide_test = (ImageView) context.findViewById(R.id.imageView_glide_test);
        Glide.with(context).load("http://img.58cdn.com.cn/logo/58/252_84/logo-o.png?v=2").into(imageView_glide_test);
        button_push = (Button) context.findViewById(R.id.button_push);
        button_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PushActivity.class));
            }
        });
    }

    public void showToast() {
        Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
    }
}