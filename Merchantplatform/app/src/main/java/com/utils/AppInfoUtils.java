package com.utils;

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
}
