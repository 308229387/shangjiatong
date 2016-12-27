package com.merchantplatform.application;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.db.helper.DbManager;
import com.facebook.stetho.Stetho;
import com.merchantplatform.BuildConfig;
import com.okhttputils.OkHttpUtils;
import com.tencent.bugly.crashreport.CrashReport;
import com.log.LogUmengAgent;
import com.utils.Constant;
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

    private boolean isStartDownService; // 是否正在进行版本更新

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
        initBugly();
        initStetho();
        initUmeng();
        initGreenDao();
    }

    private void initGreenDao() {
        DbManager.getInstance(application);
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

    private void initBugly() {
        if (!BuildConfig.DEBUG) {
            CrashReport.initCrashReport(application, Constant.BUGLY_APP_ID, false);
        }
    }

    private void initStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initialize(
                    Stetho.newInitializerBuilder(application)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(application))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(application))
                            .build());
        }
    }

    private void initUmeng() {
        LogUmengAgent.init(application);
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

    public void removeOtherActivity(Activity context) {
        try {
            for (Activity activity : activityList) {
                if (activity != context) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Activity> getActivityList(){
        return activityList;
    }

    public static Context getApplication() {
        return application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(application);
    }

    public boolean isStartDownService() {
        return isStartDownService;
    }

    public void setStartDownService(boolean isStartDownService) {
        this.isStartDownService = isStartDownService;
    }


}