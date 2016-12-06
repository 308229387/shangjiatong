package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.db.dao.CallHistory;
import com.db.helper.DaoHelper;
import com.db.helper.DbManager;
import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.PushActivity;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;
    private TextView text;
    private Button button;
    private Button button_bugly_test;
    private ImageView imageView_glide_test;
    private Button button_push;
    private Button button_db_create;
    private Button button_db_insert;

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
        imageView_glide_test = (ImageView) context.findViewById(R.id.imageView_glide_test);
        Glide.with(context).load("http://img.58cdn.com.cn/logo/58/252_84/logo-o.png?v=2").into(imageView_glide_test);
        button_push = (Button) context.findViewById(R.id.button_push);
        button_push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PushActivity.class));
            }
        });
        button_db_create = (Button) context.findViewById(R.id.button_db_create);
        button_db_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDataBase();
            }
        });
        button_db_insert = (Button) context.findViewById(R.id.button_db_insert);
        button_db_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDbData();
            }
        });
    }

    public void showToast() {
        Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
    }

    private void createDataBase() {
        DbManager.getInstance(context);
    }

    private void insertDbData() {
        CallHistory callHistory = new CallHistory();
        callHistory.setId(System.currentTimeMillis());
        callHistory.setTimeStamp(System.currentTimeMillis());
        callHistory.setCallType("呼入");
        callHistory.setPhoneNum("18888888888");
        callHistory.setCallCity("北京");
        callHistory.setBusinessType("推广");
        callHistory.setCallDate(System.currentTimeMillis());
        callHistory.setCallDuration(System.currentTimeMillis());
        DaoHelper.insertData(context, callHistory);
    }
}