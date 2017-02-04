package com.merchantplatform.model;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.adapter.GridDrawAdapter;
import com.merchantplatform.adapter.WelfareTaskAdapter;
import com.merchantplatform.fragment.WelfareFragment;
import com.ui.SpaceItemDecoration;

/**
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareModel extends BaseModel {
    private WelfareFragment context;
    private View view;
    private RecyclerView gridRecyclerView;
    private RecyclerView listRecyclerView;
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
        listRecyclerView = (RecyclerView) view.findViewById(R.id.welfare_recycler_view);
        mLayoutManager = new LinearLayoutManager(context.getActivity());
        mGridManager = new GridLayoutManager(context.getActivity(), 3);
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
    }

    public View getView() {
        return view;
    }

}
