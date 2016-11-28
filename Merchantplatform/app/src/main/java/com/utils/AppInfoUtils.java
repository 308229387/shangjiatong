package com.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

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
}
