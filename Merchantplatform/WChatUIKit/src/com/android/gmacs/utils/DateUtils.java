package com.android.gmacs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by linyueyang on 17/2/24.
 */

public class DateUtils {

    /**
     * Format time.<br>Format message time on your own rule by overriding this method.</br>
     *
     * @param messageTime
     * @return
     */
    public static String messageTimeFormat(long messageTime) {

        SimpleDateFormat mSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDate = calendar.get(Calendar.DATE);

        calendar.setTimeInMillis(messageTime);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        int messageYear = calendar.get(Calendar.YEAR);
        int messageMonth = calendar.get(Calendar.MONTH);
        int messageDate = calendar.get(Calendar.DATE);

        String formattedTime;
        if (currentYear == messageYear && currentMonth == messageMonth) {
            int delta = currentDate - messageDate;
            if (delta == 0) {
                mSimpleDateFormat.applyPattern("HH:mm");
                formattedTime = mSimpleDateFormat.format(calendar.getTime());
            } else if (delta > 0) {
                if (delta == 1) {
                    formattedTime = "昨天";
                } else if (delta < 7) {
                    String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                    int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    formattedTime = weekOfDays[week >= 0 ? week : 0];
                } else {
                    mSimpleDateFormat.applyPattern("MM-dd");
                    formattedTime = mSimpleDateFormat.format(calendar.getTime());
                }
            } else {
                mSimpleDateFormat.applyPattern("MM-dd");
                formattedTime = mSimpleDateFormat.format(calendar.getTime());
            }
        } else if (currentYear == messageYear) {
            mSimpleDateFormat.applyPattern("MM-dd");
            formattedTime = mSimpleDateFormat.format(calendar.getTime());
        } else {
            mSimpleDateFormat.applyPattern("yyyy-MM-dd");
            formattedTime = mSimpleDateFormat.format(calendar.getTime());
        }
        return formattedTime;
    }

}
