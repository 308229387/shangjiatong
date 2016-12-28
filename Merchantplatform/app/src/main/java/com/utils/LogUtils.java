package com.utils;

import com.merchantplatform.BuildConfig;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class LogUtils {

    private static boolean isShowLog = true;

    public static void isShowLog(boolean isShowLog) {
        LogUtils.isShowLog = isShowLog;
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).v(msg);
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).d(msg);
    }

    public static void list(String tag, List list) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).d(list);
    }

    public static void map(String tag, Map map) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).d(map);
    }

    public static void set(String tag, Set set) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).d(set);
    }

    public static void stringArray(String tag, String[] strings) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).d(strings);
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).i(msg);
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).w(msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).e(msg);
    }

    public static void a(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).wtf(msg);
    }

    public static void json(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).json(msg);
    }

    public static void xml(String tag, String msg) {
        if (BuildConfig.DEBUG && isShowLog)
            Logger.t(tag).xml(msg);
    }
}