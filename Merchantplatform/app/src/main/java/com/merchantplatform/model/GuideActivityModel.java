package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.LoginActivity;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;
    private TextView text;
    private Button button;
    private Button button_bugly_test;

    public GuideActivityModel(Activity context) {
        this.context = context;
    }

    public void initLayout() {
        text = (TextView) context.findViewById(R.id.text_click);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HomepageActivity.class));
            }
        });

        button = (Button) context.findViewById(R.id.button_click);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, AboutActivity.class));
            }
        });
        button_bugly_test = (Button) context.findViewById(R.id.button_bugly_test);
        button_bugly_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashReport.testJavaCrash();
            }
        });
    }

    public void showToast() {
        Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
    }
}
