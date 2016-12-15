package com.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.List;

/**
 * Created by 58 on 2016/11/25.
 */
public class AppInfoUtils {

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 获取渠道号
     * @param context
     * @return
     */
    public static String getChannelId(Context context){
        String channel= "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            channel=appInfo.metaData.getString("CHANNEL_ID");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channel == null ? "-" : channel;
    }

    /**
     * 判断网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 判断当前应用程序是否在前台
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得版本名
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        String packageName = context.getPackageName();
        return "" + (context.getPackageManager().getPackageInfo(packageName, 0).versionCode);
    }

    /**
     * 获得版本号
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getVersionName(Context context) throws PackageManager.NameNotFoundException {
        String packageName = context.getPackageName();
        return "" + (context.getPackageManager().getPackageInfo(packageName, 0).versionName);
    }

    /**
     * 获取设备号
     * @param context
     * @return
     */
    public static String getIMEI(Context context){
        String IMEINumber = "";
        TelephonyManager TelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        IMEINumber = TelephonyMgr.getDeviceId();
        return IMEINumber == null ? "-" :IMEINumber;
    }
}
