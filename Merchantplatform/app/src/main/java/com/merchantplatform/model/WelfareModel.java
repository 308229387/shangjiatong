package com.merchantplatform.model;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.callback.JsonCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;
import com.merchantplatform.adapter.GridDrawAdapter;
import com.merchantplatform.adapter.WelfareTaskAdapter;
import com.merchantplatform.bean.GetTask;
import com.merchantplatform.fragment.WelfareFragment;
import com.merchantplatform.service.GetServiceTime;
import com.okhttputils.OkHttpUtils;
import com.ui.RushBuyCountDownTimerView;
import com.ui.SpaceItemDecoration;
import com.utils.ToastUtils;
import com.utils.Urls;
import com.xrecyclerview.XRecyclerView;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareModel extends BaseModel implements View.OnClickListener {
    private WelfareFragment context;
    private View view;
    private View headView;
    private RecyclerView gridRecyclerView;
    private XRecyclerView listRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GridLayoutManager mGridManager;
    private GridDrawAdapter mAdapter;
    private WelfareTaskAdapter welfareTaskAdapter;
    private TextView luckDraw;
    private TextView fraction;
    private TextView alredyAddCount;
    private RushBuyCountDownTimerView countDownText;

    private int taskTime = -1;
    private int surplusTime;

    public WelfareModel(WelfareFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_welfare_layout, container, false);
        gridRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        listRecyclerView = (XRecyclerView) view.findViewById(R.id.welfare_recycler_view);
        luckDraw = (TextView) view.findViewById(R.id.luck_draw);
        fraction = (TextView)view.findViewById(R.id.welfare_fraction) ;
        alredyAddCount = (TextView)view.findViewById(R.id.alredy_add_count);
        luckDraw.setOnClickListener(this);
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
        fraction.setText("9999");

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
        headView = LayoutInflater.from(context.getActivity()).inflate(R.layout.welfare_list_header, listRecyclerView, false);
        countDownText =(RushBuyCountDownTimerView) headView.findViewById(R.id.timerView);
        listRecyclerView.addHeaderView(headView);
        listRecyclerView.setPullRefreshEnabled(false);
        listRecyclerView.setLoadingMoreEnabled(false);
    }

    public View getView() {
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.luck_draw:
                context.getActivity().startActivity(new Intent(context.getContext(), DailyLotteryActivity.class));
        }
    }

    public void getTask() {
        OkHttpUtils.get(Urls.GET_TASK)
                .execute(new Task());
    }

    private class Task extends JsonCallback<GetTask> {
        @Override
        public void onResponse(boolean isFromCache, GetTask s, Request request, @Nullable Response response) {
            try {
                dealWithData(s);
            } catch (Exception e) {
                ToastUtils.showToast("数据解析错误");
            }
        }

        private void dealWithData(GetTask s) {
            String a = s.getData().getOpentime();
            String[] b = a.split(":");
            taskTime = dealWithTimeToSecond(b);
            alredyAddCount.setText(String.format(context.getString(R.string.alredy_add_count),s.getData().getGainscore()));


        }
    }

    public int dealWithTimeToSecond(String[] a) {
        int hour = Integer.parseInt(a[0]) * 3600;
        int minute = Integer.parseInt(a[1]) * 60;
        int second = Integer.parseInt(a[2]);
        int testAll = hour + minute + second;
        return testAll;
    }

    public int dealWithTimeToResult() {
        if (taskTime - GetServiceTime.systemTimeSecond > 0)
            surplusTime = taskTime - GetServiceTime.systemTimeSecond;
        else
            surplusTime = taskTime + (86400 - GetServiceTime.systemTimeSecond);
        return surplusTime;
    }

    public int[] calculateResult() {
        int[] a = new int[3];
        a[0] = surplusTime / 3600;
        a[1] = (surplusTime % 3600) / 60;
        a[2] = (surplusTime % 3600) % 60;
        return a;
    }

    public void setTextToCountDown(){
        countDownText.setTime(calculateResult()[0],calculateResult()[1],calculateResult()[2]);
        countDownText.start();
    }
}
