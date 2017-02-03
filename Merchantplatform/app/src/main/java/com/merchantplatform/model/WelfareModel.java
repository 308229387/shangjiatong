package com.merchantplatform.model;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.fragment.WelfareFragment;

/**
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareModel extends BaseModel {
    private WelfareFragment context;
    private View view;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridManager;
    private WelfareAdapter mAdapter;
    private LayoutInflater inflater;


    public WelfareModel(WelfareFragment context) {
        this.context = context;
        inflater = LayoutInflater.from(context.getActivity());
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_welfare_layout, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(context.getActivity());
        mGridManager = new GridLayoutManager(context.getActivity(), 3);
        mRecyclerView.setLayoutManager(mGridManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new WelfareAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(15));
    }

    public View getView() {
        return view;
    }

    private class WelfareAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.item_welfare_prize, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
        }
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;


        }
    }
}
