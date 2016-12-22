package com.merchantplatform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gmacs.R;
import com.db.dao.SystemNotificationDetial;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/12/20.
 */

public class SystemNotificationAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<SystemNotificationDetial> data;

    public SystemNotificationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.system_notification_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.system_notification_title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.text = (TextView) convertView.findViewById(R.id.system_notification_text);
            holder.arrow = (ImageView) convertView.findViewById(R.id.system_notification_arrow);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (data.size() > 0) {
            holder.title.setText(data.get(position).getTitle());
            holder.text.setText(data.get(position).getDescribe());
        }
        return convertView;
    }

    public void setData(ArrayList<com.db.dao.SystemNotificationDetial> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        private TextView time;
        private TextView title;
        private TextView text;
        private ImageView arrow;
    }
}
