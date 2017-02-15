package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.bean.MyAwardResponse;
import com.utils.DateUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by MengZhiYuan on 2017/2/13.
 */

public class MyAwardAdapter extends BaseRecyclerViewAdapter<MyAwardResponse.award, MyAwardAdapter.MyAwardViewHolder> {

    public MyAwardAdapter(Context context, ArrayList<MyAwardResponse.award> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(MyAwardViewHolder myAwardViewHolder, int position) {
        MyAwardResponse.award award = mList.get(position);
        String winTime = DateUtils.formatDateTimeToDate(award.getWin_time());
        myAwardViewHolder.setText(R.id.tv_prize_name, award.getPrize_name())
                .setText(R.id.tv_win_time, winTime)
                .setText(R.id.tv_my_award_validity, winTime + "至" + DateUtils.getDateAfterDate(winTime, 7));
    }

    @Override
    public MyAwardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyAwardViewHolder(inflateItemView(parent, R.layout.item_my_award));
    }

    public class MyAwardViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {

        public MyAwardViewHolder(View itemView) {
            super(itemView);
        }
    }
}