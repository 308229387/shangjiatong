package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.bean.LotteryDetailResponse;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

public class DailyAwardAdapter extends BaseRecyclerViewAdapter<LotteryDetailResponse.award, DailyAwardAdapter.DailyAwardViewHolder> {

    public DailyAwardAdapter(Context context, ArrayList<LotteryDetailResponse.award> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(DailyAwardViewHolder dailyAwardViewHolder, int position) {
        dailyAwardViewHolder.setText(R.id.welfare_name, mList.get(position).getPrizeName());
    }

    @Override
    public DailyAwardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DailyAwardViewHolder(inflateItemView(parent, R.layout.item_daily_award));
    }

    public class DailyAwardViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {

        public DailyAwardViewHolder(View itemView) {
            super(itemView);
        }
    }
}