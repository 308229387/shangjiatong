package com.merchantplatform.application;

import android.app.Activity;
import android.app.Application;

import com.okhttputils.OkHttpUtils;
import com.utils.LoginRegisterUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/21.
 */
public class HyApplication extends Application {
    private static HyApplication instance;
    private List<Activity> activityList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initOkHttp();
        initLogin();
    }

    private void initOkHttp() {
        OkHttpUtils.init(this);
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public static HyApplication getInstance() {
        if (null == instance)
            instance = new HyApplication();
        return instance;
    }

    public void removeActivity(Activity context) {
        if (activityList.contains(context))
            activityList.remove(context);
    }

    public void initLogin() {
        new LoginRegisterUtils(this);
    }
}
