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

    public static String getCurrentDateTime() {
        long currentMillis = System.currentTimeMillis();
        return formatMillisToDateTime(currentMillis);
    }

    public static String getCurrentDate() {
        long currentMillis = System.currentTimeMillis();
        return formatMillisToDate(currentMillis);
    }

    public static String formatMillisToDate(long timeInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(timeInMillis);
    }

    public static String formatMillisToDateTime(long timeInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeInMillis);
    }

    public static String formatDateTimeToDate(String dateTime) {
        return dateTime.split(" ")[0];
    }

    public static String formatDateTimeToTime(String dateTime) {
        return dateTime.split(" ")[1];
    }

    public static String formatTimeToDisplayTime(String time) {
        int startIndex = time.charAt(0) == '0' ? 1 : 0;
        return time.substring(startIndex, time.length() - 3);
    }

    public static String formatSecondsToDetailTime(long time) {
        if (time != 0) {
            String hour = (time / 3600) == 0 ? "" : (time / 3600) + "小时";
            String minute = (time / 60 % 60) == 0 ? (hour.equals("") ? "" : "0分") : (time / 60 % 60) + "分";
            String second = time % 60 + "秒";
            return hour + minute + second;
        } else {
            return "0秒";
        }
    }

    public static String getMonthAgo(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        return formatMillisToDate(calendar.getTimeInMillis());
    }

    public static String getDateAfterDate(String date, int afterDays) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date dateParser = null;
        try {
            dateParser = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateParser);
        calendar.add(Calendar.DAY_OF_MONTH, afterDays);
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
            String time = formatDateTimeToTime(dateTime);
            int index = time.charAt(0) == 0 ? 1 : 0;
            return time.substring(index, time.length() - 3);
        } else {
            String date = formatDateTimeToDate(dateTime);
            int index = date.indexOf("-") + 1;
            return date.substring(index, date.length()).replace("-", "/");
        }
    }

    public static boolean isEmptyAndNotToday(String dateTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (TextUtils.isEmpty(dateTime)) {
            return true;
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
            return false;
        } else {
            return true;
        }
    }
}