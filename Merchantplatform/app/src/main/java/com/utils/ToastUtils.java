package com.utils;

import android.widget.Toast;

import com.merchantplatform.application.HyApplication;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class ToastUtils {
    public static void showToast(int message) {
        Toast.makeText(HyApplication.getApplication(), message + "", Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String message) {
        Toast.makeText(HyApplication.getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
