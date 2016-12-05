package com.merchantplatform.application;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.merchantplatform.BuildConfig;
import com.okhttputils.OkHttpUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.utils.Constant;
import com.utils.GrowingIoInitUtils;
import com.utils.IMInitAppUtils;
import com.utils.LoginRegisterUtils;
import com.utils.WPushInitUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：黄页商店平台Application,此界面会注册框架、全局上下文、单例对象等，请注意维护此界面整洁！
 */

public class HyApplication extends MultiDexApplication {

    private static HyApplication instance;
    private static HyApplication application;
    private List<Activity> activityList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        setApplicationContext();
        initWPush();
        initOkHttp();
        initLogin();
        initIM();
        initGrowingIo();
        initBugly();
        initStetho();
    }

    private void setApplicationContext() {
        application = this;
    }

    private void initWPush() {
        new WPushInitUtils(application);
    }

    private void initOkHttp() {
        OkHttpUtils.init(application);
    }

    public void initLogin() {
        new LoginRegisterUtils(application);
    }

    public void initIM() {
        new IMInitAppUtils(application);
    }

    private void initGrowingIo() {
        new GrowingIoInitUtils(application);
    }

    private void initBugly() {
        if (!BuildConfig.DEBUG)
            CrashReport.initCrashReport(application, Constant.BUGLY_APP_ID, false);
    }

    private void initStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(application)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
                        .build());
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(application);
    }

}