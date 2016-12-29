package com.utils;

import android.content.Context;
import android.content.Intent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 58 on 2016/12/8.
 */

public class PageSwitchUtils {

    public static <T> void goToActivity(Context context, Class<T> clazz) {
        if (context == null || clazz == null) {
            throw new RuntimeException("参数不能为空");
        }
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
    }

    public static <T> void goToActivityWithString(Context context, Class<T> clazz, Map<String, String> maps) {
        if (context == null || clazz == null) {
            throw new RuntimeException("参数不能为空");
        }
        Intent intent = new Intent(context, clazz);
        Set<Map.Entry<String, String>> entrySet = maps.entrySet();
        Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            intent.putExtra(key, value);
        }
        context.startActivity(intent);

    }

}
