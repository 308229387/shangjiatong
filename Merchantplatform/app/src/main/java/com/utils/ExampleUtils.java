package com.utils;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.callback.DialogCallback;
import com.okhttputils.OkHttpUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：用作方法演示
 */

public class ExampleUtils extends Activity {
    
    //get请求
    public void get() {
        OkHttpUtils.get(Urls.URL_METHOD)
                .tag(this)
                .headers("header1", "headerValue1")
                .params("param1", "paramValue1")
                .execute(new TestMethodCallBack(this));
    }

    //请求响应体
    private class TestMethodCallBack extends DialogCallback<String> {
        public TestMethodCallBack(ExampleUtils exampleUtils) {
            super(ExampleUtils.this);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {

        }
    }
}
