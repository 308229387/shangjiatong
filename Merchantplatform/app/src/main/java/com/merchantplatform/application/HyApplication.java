package com.merchantplatform.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.merchantplatform.BuildConfig;
import com.okhttputils.OkHttpUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.utils.LoginRegisterUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：黄页商店平台Application,此界面会注册框架、全局上下文、单例对象等，请注意维护此界面整洁！
 */

public class HyApplication extends Application {
    private static HyApplication instance;
    private static HyApplication application;
    private List<Activity> activityList = new LinkedList<>();
    private static final String BUGLY_APP_ID = "900060310";

    @Override
    public void onCreate() {
        super.onCreate();
        setApplicationContext();
        initOkHttp();
        initLogin();
        initBugly();
    }

    private void setApplicationContext() {
        application = this;
    }

    private void initOkHttp() {
        OkHttpUtils.init(application);
    }

    public void initLogin() {
        new LoginRegisterUtils(this);
    }

    private void initBugly() {
        if (BuildConfig.isRelease) {
            CrashReport.initCrashReport(getApplicationContext(), BUGLY_APP_ID, false);
        }
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

    public static Context getApplication() {
        return application;
    }
}