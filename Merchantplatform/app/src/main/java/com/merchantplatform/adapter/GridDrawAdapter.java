package com.merchantplatform.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;

/**
 * Created by songyongmeng on 2017/2/4.
 */

public class GridDrawAdapter extends RecyclerView.Adapter<GridDrawAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;

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
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }


}

