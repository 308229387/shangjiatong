package com.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.common.gmacs.core.Gmacs;
import com.merchantplatform.BuildConfig;
import com.merchantplatform.application.HyApplication;


/**
 * Created by SongYongmeng on 2016/11/28.
 */
public class IMInitAppUtils {
    private final HyApplication application;

    public IMInitAppUtils(HyApplication application) {
        this.application = application;
        init();
    }

    private void init() {
        if (isUIProcess()) {
            SDKInitializer.initialize(application);//百度地图
            Gmacs.getInstance().initialize(application);//im环境配置
            configDebugLog();//配置DebugLog
        }
    }

    private void configDebugLog() {
        if (BuildConfig.DEBUG)
            Gmacs.getInstance().setLoggable(true);
        else
            Gmacs.getInstance().setLoggable(false);
    }

    private boolean isUIProcess() {
        int pid = Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        String packageName = getPackageName();
        return processName.equals(packageName);
    }
}
