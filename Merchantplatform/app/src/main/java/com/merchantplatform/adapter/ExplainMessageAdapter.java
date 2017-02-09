package com.merchantplatform.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;

public class ExplainMessageAdapter extends BaseRecyclerViewAdapter<String, ExplainMessageAdapter.ExplainMessageViewHolder> {

    public ExplainMessageAdapter(Context context, ArrayList<String> mList) {
        super(context, mList);
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    protected void bindDataToItemView(ExplainMessageViewHolder explainMessageViewHolder, int position) {
        if (position == 0) {
            explainMessageViewHolder.setVisible(R.id.tv_explain_index, false)
                    .setText(R.id.tv_explain_content, "抽奖说明:");
        } else {
            explainMessageViewHolder.setText(R.id.tv_explain_index, (position) + ".")
                    .setText(R.id.tv_explain_content, mList.get(position - 1));
        }
    }

    @Override
    public ExplainMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExplainMessageViewHolder(inflateItemView(parent, R.layout.item_lottery_explain));
    }

    public class ExplainMessageViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {
        public ExplainMessageViewHolder(View itemView) {
            super(itemView);
        }
    }
}