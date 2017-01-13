package com.utils;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by 58 on 2015/9/1.
 */
public class TaskUtil {

    /**
     * android 的AysncTask直接调用Execute会在在一个线程池里按调用的先后顺序依次执行。
     * 如果应用的所有网络获取都依赖这个来做，当有一个网络请求柱塞，就导致其它请求也柱塞了。
     * 在3.0 以后引入了新的方法。可以不在一个线程池里运行。
     * @param task
     * @param params
     * @param <Params>
     * @param <Progress>
     * @param <Result>
     */
    public static <Params, Progress, Result> void executeAsyncTask(
            AsyncTask<Params, Progress, Result> task, Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

    public static void cancel(AsyncTask task) {
        if (task != null) {
            task.cancel(true);
        }
    }
}
