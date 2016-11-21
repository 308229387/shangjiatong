package com.merchantplatform;

import com.okhttputils.OkHttpUtils;

/**
 * Created by SongYongmeng on 2016/11/21.
 */

public class Application extends android.app.Application{
    @Override
    public void onCreate() {
        super.onCreate();
        initOkHttp();

    }

    private void initOkHttp() {
        OkHttpUtils.init(this);
    }
}
