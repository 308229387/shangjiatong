package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.bean.AwardHistoryResponse;
import com.utils.DateUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

public class AwardHistoryAdapter extends BaseRecyclerViewAdapter<AwardHistoryResponse.AwardHistory, AwardHistoryAdapter.AwardHistoryViewHolder> {

    public AwardHistoryAdapter(Context context, ArrayList<AwardHistoryResponse.AwardHistory> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(AwardHistoryViewHolder awardHistoryViewHolder, int position) {
        AwardHistoryResponse.AwardHistory awardHistory = mList.get(position);
        awardHistoryViewHolder.setText(R.id.tv_award_history_time, DateUtils.formatDateTimeToDate(awardHistory.getWin_time()))
                .setText(R.id.tv_award_history_username, awardHistory.getUser_name())
                .setText(R.id.tv_award_history_name, awardHistory.getPrize_name());
    }

    @Override
    public AwardHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AwardHistoryViewHolder(inflateItemView(parent, R.layout.item_award_history));
    }

    public class AwardHistoryViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {

        public AwardHistoryViewHolder(View itemView) {
            super(itemView);
        }
    }
}