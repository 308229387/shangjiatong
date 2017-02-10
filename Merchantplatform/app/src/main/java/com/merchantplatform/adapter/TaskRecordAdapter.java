package com.merchantplatform.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.TaskRecordActivity;

/**
 * Created by songyongmeng on 2017/2/10.
 */

public class TaskRecordAdapter extends RecyclerView.Adapter<TaskRecordAdapter.ViewHolder> {
    private TaskRecordActivity context;
    private LayoutInflater inflater;

    public TaskRecordAdapter(TaskRecordActivity context, LayoutInflater inflater) {
        this.inflater = inflater;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  new ViewHolder(inflater.inflate(R.layout.welfare_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView taskCount;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;
            taskCount = (TextView) view.findViewById(R.id.task_count);
        }
    }
}
