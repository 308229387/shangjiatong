package com.merchantplatform.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.TaskRecordActivity;
import com.merchantplatform.bean.IntegralRecordResponse;

import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/10.
 */

public class TaskRecordAdapter extends RecyclerView.Adapter<TaskRecordAdapter.ViewHolder> {
    private TaskRecordActivity context;
    private LayoutInflater inflater;
    private ArrayList<IntegralRecordResponse.dataInfo> list;

    public TaskRecordAdapter(TaskRecordActivity context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.integral_record_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(list.get(position).getDescription());
        holder.time.setText(list.get(position).getInfo_complete_time());
        holder.count.setText(list.get(position).getScore());
    }

    public void setData(ArrayList<IntegralRecordResponse.dataInfo> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView time;
        public TextView count;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;
            title = (TextView) view.findViewById(R.id.integral_title);
            time = (TextView) view.findViewById(R.id.integral_time);
            count = (TextView) view.findViewById(R.id.integral_count);
        }
    }
}
