package com.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Process;

import com.baidu.mapapi.SDKInitializer;
import com.merchantplatform.application.HyApplication;

import static com.tencent.open.utils.Global.getPackageName;

/**
 * Created by SongYongmeng on 2016/11/28.
 */
public class IMInitUtils {
    private final HyApplication application;

    public IMInitUtils(HyApplication application) {
        this.application = application;
        init();
    }

    private void init(){
        if (isUIProcess()){
            SDKInitializer.initialize(application);
        }
    }

    private boolean isUIProcess() {
        int pid = Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager)application. getSystemService(Context.ACTIVITY_SERVICE);
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
