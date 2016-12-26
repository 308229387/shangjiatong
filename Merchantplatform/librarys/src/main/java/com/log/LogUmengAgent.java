package com.log;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

/**
 * Created by linyueyang on 16/12/16.
 * <p/>
 * 友盟自定义事件工具类
 */
public class LogUmengAgent {

    private static LogUmengAgent logUmengAgent;
    private static Context mContext;

    private LogUmengAgent() {

    }

    public static void init(Context context) {
        mContext = context;
    }

    public static LogUmengAgent ins() {

        if (mContext == null) {
            Log.e("LogUmengAgent", "LogUmengAgent未进行初始化操作");
            return null;
        }

        if (logUmengAgent == null) {
            synchronized (LogUmengAgent.class) {
                if (logUmengAgent == null) {
                    logUmengAgent = new LogUmengAgent();
                }
            }
        }

        return logUmengAgent;
    }

    public void log(LogUmengEnum umengEnum) {
        log(umengEnum.getEventid());
    }

    public void log(String eventId) {
        Log.i("LogUmengAgent", "触发埋点时间Id：" + eventId);
        MobclickAgent.onEvent(mContext, eventId);
    }

    public void log(String eventId, HashMap map) {
        MobclickAgent.onEvent(mContext, eventId, map);
    }

}
