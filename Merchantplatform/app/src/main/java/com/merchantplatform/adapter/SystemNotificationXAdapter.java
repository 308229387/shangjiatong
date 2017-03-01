package com.merchantplatform.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.android.gmacs.activity.GmacsWebViewActivity;
import com.common.gmacs.utils.ToastUtil;
import com.db.dao.SystemNotificationDetial;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationXAdapter extends BaseRecyclerViewAdapter<SystemNotificationDetial, SystemNotificationXAdapter.ViewHolder> {
    private ArrayList dataList;
    private SimpleDateFormat mSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

    public SystemNotificationXAdapter(Context context, ArrayList<SystemNotificationDetial> temp) {
        super(context, temp);
    }


    @Override
    protected void bindDataToItemView(ViewHolder viewHolder, int position) {
        SystemNotificationDetial systemNotification = getItem(position);
        viewHolder.setText(R.id.system_notification_title, systemNotification.getTitle())
                .setText(R.id.time, messageTimeFormat(systemNotification.getSortId()))
                .setVisible(R.id.system_notification_arrow, systemNotification.getContentType() == 1 ? false : true)
                .setVisible(R.id.system_notification_line, systemNotification.getContentType() == 1 ? false : true)
                .setText(R.id.system_notification_text, systemNotification.getContentType() == 1 ? systemNotification.getDescribe() : "查看详情");

        if (systemNotification.getContentType() == 2) {
            viewHolder.setOnClickListener(R.id.notification_click_layout, new SystemNotificationWebListener(systemNotification));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflateItemView(parent, R.layout.system_notification_item));
    }

    public void setData(ArrayList<SystemNotificationDetial> temp) {
        dataList = temp;
        notifyDataSetChanged();
    }

    public class ViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    protected String messageTimeFormat(long messageTime) {
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


    private class SystemNotificationWebListener implements View.OnClickListener {
        private final SystemNotificationDetial systemNotification;

        public SystemNotificationWebListener(SystemNotificationDetial systemNotification) {
            this.systemNotification = systemNotification;
        }

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(context, GmacsWebViewActivity.class);
            intent.putExtra(GmacsWebViewActivity.EXTRA_TITLE, systemNotification.getTitle());
            intent.putExtra(GmacsWebViewActivity.EXTRA_URL, systemNotification.getContent());
            context.startActivity(intent);
        }
    }
}
