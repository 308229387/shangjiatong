package com.merchantplatform.application;

import android.app.Activity;

import com.okhttputils.OkHttpUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/21.
 */
public class Application extends android.app.Application {
    private static Application instance;
    private List<Activity> activityList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        initOkHttp();
    }

    private void initOkHttp() {
        OkHttpUtils.init(this);
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public static Application getInstance() {
        if (null == instance)
            instance = new Application();
        return instance;
    }

    public void removeActivity(Activity context) {
        if (activityList.contains(context))
            activityList.remove(context);
    }
}
