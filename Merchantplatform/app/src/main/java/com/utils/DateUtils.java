package com.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 58 on 2016/12/14.
 */

public class DateUtils {

    public static String formatMillisToDate(long timeInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(timeInMillis);
    }

    public static String formatMillisToDateTime(long timeInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeInMillis);
    }

    public static String formatDateTimeToDate(String dateTime) {
        return dateTime.split(" ")[0];
    }

    public static String getMonthAgo(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        return formatMillisToDate(calendar.getTimeInMillis());
    }

    public static String displayByDateTime(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (TextUtils.isEmpty(dateTime)) {
            return "";
        }
        Date dateParser = null;
        try {
            dateParser = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();    //今天
        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        current.setTime(dateParser);
        if (current.after(today)) {
            String time = dateTime.split(" ")[1];
            int index = time.charAt(0) == 0 ? 1 : 0;
            return time.substring(index, time.length() - 3);
        } else {
            String date = dateTime.split(" ")[0];
            int index = date.indexOf("-") + 1;
            return date.substring(index, date.length()).replace("-", "/");
        }
    }
}
