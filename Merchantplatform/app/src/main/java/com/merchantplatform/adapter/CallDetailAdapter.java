package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.bean.CallDetailListBean;
import com.utils.DateUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/12/17.
 */

public class CallDetailAdapter extends BaseRecyclerViewAdapter<CallDetailListBean, CallDetailAdapter.CallDetailViewHolder> {

    public CallDetailAdapter(Context context, ArrayList<CallDetailListBean> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(CallDetailViewHolder callDetailViewHolder, int position) {
        String callTime = mList.get(position).getTime();
        callDetailViewHolder.setText(R.id.item_call_detail_time, DateUtils.formatTimeToDisplayTime(DateUtils.formatDateTimeToTime(callTime)))
                .setText(R.id.item_call_detail_type, mList.get(position).getType() == 1 ? "呼入电话" : "呼出电话")
                .setText(R.id.item_call_detail_duration, mList.get(position).getDuration() + "秒钟");
    }

    @Override
    public CallDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CallDetailViewHolder(inflateItemView(parent, R.layout.item_call_detail));
    }

    public class CallDetailViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {

        public CallDetailViewHolder(View itemView) {
            super(itemView);
        }
    }
}