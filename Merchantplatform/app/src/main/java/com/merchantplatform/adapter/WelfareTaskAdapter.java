package com.merchantplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.utils.ToastUtils;

/**
 * Created by songyongmeng on 2017/2/4.
 */

public class WelfareTaskAdapter extends RecyclerView.Adapter<WelfareTaskAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;

    public WelfareTaskAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WelfareTaskAdapter.ViewHolder(inflater.inflate(R.layout.welfare_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showToast(position);
            }
        });

        if (position == 1) {
            holder.taskCount.setTextColor(context.getResources().getColor(R.color.light_grey));
            holder.button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.welfare_button_back_gray));
        }


    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView button;
        public TextView taskCount;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;
            button = (TextView) view.findViewById(R.id.task_to_do);
            taskCount = (TextView) view.findViewById(R.id.task_count);
        }
    }

}
