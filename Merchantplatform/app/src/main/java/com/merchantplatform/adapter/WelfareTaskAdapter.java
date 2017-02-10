package com.merchantplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merchantplatform.R;
import com.merchantplatform.bean.GetTask;
import com.utils.ToastUtils;

import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/4.
 */

public class WelfareTaskAdapter extends RecyclerView.Adapter<WelfareTaskAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<GetTask.taskData> list;

    public WelfareTaskAdapter(Context context, ArrayList<GetTask.taskData> list) {
        this.context = context;
        this.list = list;
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
        holder.taskCount.setText("+"+list.get(position).getTask_score()+"");
        holder.title.setText(list.get(position).getTask_name());
        holder.context.setText(list.get(position).getTask_describe());
        Glide.with(context).load(list.get(position).getPic_url()).into(holder.image);

        if (position == 1) {
            holder.taskCount.setTextColor(context.getResources().getColor(R.color.light_grey));
            holder.button.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.welfare_button_back_gray));
        }


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setData(ArrayList<GetTask.taskData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView title;
        public TextView taskCount;
        public TextView context;
        public TextView button;
        public ImageView image;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;
            button = (TextView) view.findViewById(R.id.task_to_do);
            taskCount = (TextView) view.findViewById(R.id.task_count);
            title = (TextView) view.findViewById(R.id.welfare_list_title);
            context = (TextView) view.findViewById(R.id.welfare_context);
            image = (ImageView) view.findViewById(R.id.welfare_list_image);

        }
    }

}
