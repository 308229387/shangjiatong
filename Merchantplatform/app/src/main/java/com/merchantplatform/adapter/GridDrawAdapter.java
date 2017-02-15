package com.merchantplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.bean.GetTask;
import com.merchantplatform.bean.GetWelfareResponse;

import java.util.ArrayList;

/**
 * Created by songyongmeng on 2017/2/4.
 */

public class GridDrawAdapter extends RecyclerView.Adapter<GridDrawAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private ArrayList<GetWelfareResponse.prizeData> list;
    public GridDrawAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public GridDrawAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GridDrawAdapter.ViewHolder(inflater.inflate(R.layout.item_welfare_prize, parent, false));
    }

    @Override
    public void onBindViewHolder(GridDrawAdapter.ViewHolder holder, final int position) {
        holder.price.setText(list.get(position).getPrizeName());
        holder.name.setText(list.get(position).getPrizeLevel());
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        private TextView name;
        private TextView price;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView)view.findViewById(R.id.welfare_type);
            price = (TextView)view.findViewById(R.id.welfare_name);
        }
    }

    public void setData(ArrayList<GetWelfareResponse.prizeData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}

