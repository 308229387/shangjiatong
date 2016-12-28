package com.utils;

import com.merchantplatform.BuildConfig;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoggerUtils {

    private static boolean isShowLog = true;

    public static void isShowLog(boolean isShowLog) {
        LoggerUtils.isShowLog = isShowLog;
    }

    public static void v(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.v(msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.d(msg);
    }

    public static void list(List list) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.d(list);
    }

    public static void map(Map map) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.d(map);
    }

    public static void set(Set set) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.d(set);
    }

    public static void stringArray(String[] strings) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.d(strings);
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.i(msg);
    }

    public static void w(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.w(msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.e(msg);
    }

    public static void a(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.wtf(msg);
    }

    public static void json(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.json(msg);
    }

    public static void xml(String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.xml(msg);
    }
}