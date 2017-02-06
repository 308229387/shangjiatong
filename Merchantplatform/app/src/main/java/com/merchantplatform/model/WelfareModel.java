package com.merchantplatform.model;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;
import com.merchantplatform.adapter.GridDrawAdapter;
import com.merchantplatform.adapter.WelfareTaskAdapter;
import com.merchantplatform.fragment.WelfareFragment;
import com.ui.SpaceItemDecoration;
import com.xrecyclerview.XRecyclerView;

/**
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareModel extends BaseModel {
    private WelfareFragment context;
    private View view;
    private RecyclerView gridRecyclerView;
    private XRecyclerView listRecyclerView;
    private TextView tv_welfare_lottery;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridManager;
    private GridDrawAdapter mAdapter;
    private WelfareTaskAdapter welfareTaskAdapter;


    public WelfareModel(WelfareFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_welfare_layout, container, false);
        gridRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        listRecyclerView = (XRecyclerView) view.findViewById(R.id.welfare_recycler_view);
        tv_welfare_lottery = (TextView) view.findViewById(R.id.tv_welfare_lottery);
        tv_welfare_lottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context.getContext(), DailyLotteryActivity.class);
                context.startActivity(intent);
            }
        });
        mLayoutManager = new LinearLayoutManager(context.getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mGridManager = new GridLayoutManager(context.getActivity(), 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        gridViewSetting();
        listViewSetting();
    }

    private void gridViewSetting() {
        gridRecyclerView.setLayoutManager(mGridManager);
        gridRecyclerView.setHasFixedSize(true);
        mAdapter = new GridDrawAdapter(context.getActivity());
        gridRecyclerView.setAdapter(mAdapter);
        gridRecyclerView.addItemDecoration(new SpaceItemDecoration(15));
    }

    private void listViewSetting() {
        listRecyclerView.setLayoutManager(mLayoutManager);
        listRecyclerView.setHasFixedSize(true);
        welfareTaskAdapter = new WelfareTaskAdapter(context.getActivity());
        listRecyclerView.setAdapter(welfareTaskAdapter);
        listRecyclerView.addHeaderView(LayoutInflater.from(context.getActivity()).inflate(R.layout.welfare_list_header, listRecyclerView, false));
        listRecyclerView.setPullRefreshEnabled(false);
        listRecyclerView.setLoadingMoreEnabled(false);
    }

    public View getView() {
        return view;
    }

}
