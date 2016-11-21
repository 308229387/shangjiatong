package com.merchantplatform;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.callback.DialogCallback;
import com.okhttputils.OkHttpUtils;
import com.okhttputils.callback.AbsCallback;
import com.utils.Urls;

import okhttp3.Request;
import okhttp3.Response;


public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        OkHttpUtils.get(Urls.URL_METHOD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new MethodCallBack(this));

    }

    private class MethodCallBack extends DialogCallback<String> {
        public MethodCallBack(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
            Toast.makeText(GuideActivity.this, s, Toast.LENGTH_LONG).show();
        }
    }
}
