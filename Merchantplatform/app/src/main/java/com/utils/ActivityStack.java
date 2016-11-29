package com.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by YanQi on 16/8/31.
 */
public class ActivityStack {

    private static ActivityStack activityStack;

    private static ArrayList<Activity> activities;

    private ActivityStack() {
    }

    public static ActivityStack getInstance() {
        if (activityStack == null) {
            activityStack = new ActivityStack();
            activities = new ArrayList<>();
        }
        return activityStack;
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    public void finishAll() {
        for (Activity i : activities) {
            i.finish();
        }
        activities.clear();
    }

}
