package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.bean.CallRecordBean;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordAdapter extends BaseRecyclerViewAdapter<CallRecordBean, CallRecordAdapter.CallRecordViewHolder> {

    public CallRecordAdapter(Context context, ArrayList<CallRecordBean> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(final CallRecordViewHolder callRecordViewHolder, final int position) {
        callRecordViewHolder.setImageResource(R.id.iv_phoneState, position % 2 == 0 ? android.R.drawable.sym_call_incoming : android.R.drawable.sym_call_outgoing)
                .setText(R.id.tv_phoneNum, getItem(position).getPhoneNum())
                .setText(R.id.tv_call_count, getItem(position).getCallCount() + "")
                .setText(R.id.tv_phone_city, getItem(position).getPhoneNumCity())
                .setText(R.id.tv_call_type, getItem(position).getCallType())
                .setText(R.id.tv_call_time, getItem(position).getCallTime())
                .setTag(R.id.ll_callrecord_content, position)
                .setOnLongClickListener(R.id.ll_callrecord_content, new OnContentLongClickListener())
                .setTag(R.id.iv_call_detail, position)
                .setOnClickListener(R.id.iv_call_detail, new OnDetailClickListener())
                .setTag(R.id.tv_delete, R.id.delete_tag_vh, callRecordViewHolder)
                .setTag(R.id.tv_delete, R.id.delete_tag_position, position)
                .setOnClickListener(R.id.tv_delete, new OnDeleteItemClickListener());
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

    private class OnContentLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(context, "position" + v.getTag(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private class OnDetailClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "position" + v.getTag(), Toast.LENGTH_SHORT).show();
        }
    }

    private class OnDeleteItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            deleteItem((CallRecordViewHolder) v.getTag(R.id.delete_tag_vh), (Integer) v.getTag(R.id.delete_tag_position));
        }
    }
}
