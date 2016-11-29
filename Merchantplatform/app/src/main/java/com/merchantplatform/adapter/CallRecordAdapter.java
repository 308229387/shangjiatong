package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.bean.CallRecordBean;

import java.util.List;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordAdapter extends BaseRecyclerViewAdapter<CallRecordBean, CallRecordAdapter.CallRecordViewHolder> {

    public CallRecordAdapter(Context context, List<CallRecordBean> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(CallRecordViewHolder callRecordViewHolder, CallRecordBean item) {
        callRecordViewHolder.setImageResource(R.id.iv_phoneState, item.getCallRecordId() % 2 == 0 ? android.R.drawable.sym_call_incoming : android.R.drawable.sym_call_outgoing)
                .setText(R.id.tv_phoneNum, item.getPhoneNum())
                .setText(R.id.tv_call_count, item.getCallCount() + "")
                .setText(R.id.tv_phone_city, item.getPhoneNumCity())
                .setText(R.id.tv_call_type, item.getCallType())
                .setText(R.id.tv_call_time, item.getCallTime())
                .setTag(R.id.iv_call_detail, item.getCallRecordId())
                .setOnClickListener(R.id.iv_call_detail, new OnDetailClickListener());
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
            Toast.makeText(context, "查看通话详情" + v.getTag(), Toast.LENGTH_SHORT).show();
        }
    }
}
