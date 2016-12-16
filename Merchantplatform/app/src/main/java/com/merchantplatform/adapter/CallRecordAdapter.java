package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.db.dao.CallList;
import com.merchantplatform.R;
import com.utils.DateUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordAdapter extends BaseRecyclerViewAdapter<CallList, CallRecordAdapter.CallRecordViewHolder> {

    private int tabIndex;

    public CallRecordAdapter(Context context, ArrayList<CallList> mList, int tabIndex) {
        super(context, mList);
        this.tabIndex = tabIndex;
    }

    @Override
    protected void bindDataToItemView(final CallRecordViewHolder callRecordViewHolder, final int position) {
        CallList callList = getItem(position);
        callRecordViewHolder
                .setBackgroundResource(R.id.item_swipelayout, (callList.getCallResult() == 20 && tabIndex == 1) ? R.color.item_missed_call_bg : R.color.item_call_bg)
                .setText(R.id.tv_phoneNum, callList.getPhone())
                .setTextColor(R.id.tv_phoneNum, callList.getCallResult() == 10 ? context.getResources().getColor(R.color.item_call_phone) : context.getResources().getColor(R.color.item_call_delete))
                .setText(R.id.tv_phone_city, callList.getLocal())
                .setText(R.id.tv_call_cate, callList.getCate())
                .setText(R.id.tv_call_time, DateUtils.displayByDateTime(callList.getCallTime()))
                .setTag(R.id.iv_call_detail, position)
                .setOnClickListener(R.id.iv_call_detail, new OnDetailClickListener())
                .setTag(R.id.tv_delete, R.id.delete_tag_vh, callRecordViewHolder)
                .setTag(R.id.tv_delete, R.id.delete_tag_position, position)
                .setOnClickListener(R.id.tv_delete, new OnDeleteItemClickListener());
        if (callList.getPhoneCount() > 1) {
            callRecordViewHolder
                    .setText(R.id.tv_call_count, "(" + getItem(position).getPhoneCount() + ")")
                    .setTextColor(R.id.tv_call_count, callList.getCallResult() == 10 ? context.getResources().getColor(R.color.item_call_phone) : context.getResources().getColor(R.color.item_call_delete))
                    .setVisible(R.id.tv_call_count, true);
        } else {
            callRecordViewHolder.setVisible(R.id.tv_call_count, false);
        }
        if (callList.getCallResult() == 10) {
            callRecordViewHolder
                    .setVisible(R.id.iv_phoneState, true)
                    .setImageResource(R.id.iv_phoneState, callList.getType() == 1 ? R.mipmap.item_call_in : R.mipmap.item_call_out);
        } else {
            callRecordViewHolder.setVisible(R.id.iv_phoneState, false);
        }
    }

    @Override
    public CallRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CallRecordViewHolder(inflateItemView(parent, R.layout.item_callrecord));
    }

    public class CallRecordViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {
        public CallRecordViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class OnDetailClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "查看详情position" + v.getTag(), Toast.LENGTH_SHORT).show();
        }
    }

    private class OnDeleteItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            deleteItem((CallRecordViewHolder) v.getTag(R.id.delete_tag_vh), (Integer) v.getTag(R.id.delete_tag_position));
        }
    }
}