package com.merchantplatform.model;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.Utils.UserUtils;
import com.callback.JsonCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;
import com.merchantplatform.activity.TaskRecordActivity;
import com.merchantplatform.adapter.GridDrawAdapter;
import com.merchantplatform.adapter.WelfareTaskAdapter;
import com.merchantplatform.bean.GetTask;
import com.merchantplatform.bean.GetWelfareResponse;
import com.merchantplatform.fragment.WelfareFragment;
import com.merchantplatform.service.GetServiceTime;
import com.okhttputils.OkHttpUtils;
import com.ui.RushBuyCountDownTimerView;
import com.ui.SpaceItemDecoration;
import com.ui.dialog.NotIsVipDialog;
import com.utils.Urls;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

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
    private TextView title;
    private ImageView noPrizeImage;
    private RushBuyCountDownTimerView countDownText;
    private ArrayList<GetTask.taskData> list;
    private int[] a = new int[3];

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
        fraction = (TextView) view.findViewById(R.id.welfare_fraction);
        alredyAddCount = (TextView) view.findViewById(R.id.alredy_add_count);
        title = (TextView) view.findViewById(R.id.prize_layout_title);
        noPrizeImage = (ImageView) view.findViewById(R.id.no_prize_image);
    }

    public void setting() {
        setListener();
        creatManager();
        gridViewSetting();
        listViewSetting();
    }

    private void setListener() {
        luckDraw.setOnClickListener(this);
        fraction.setOnClickListener(this);
    }

    private void creatManager() {
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
        welfareTaskAdapter = new WelfareTaskAdapter(context.getActivity(), list);
        listRecyclerView.setAdapter(welfareTaskAdapter);
        headView = LayoutInflater.from(context.getActivity()).inflate(R.layout.welfare_list_header, listRecyclerView, false);
        countDownText = (RushBuyCountDownTimerView) headView.findViewById(R.id.timerView);
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
                if (UserUtils.getIsVip(context.getActivity()) == 1)
                    context.getActivity().startActivity(new Intent(context.getContext(), DailyLotteryActivity.class));
                else
                    new NotIsVipDialog(context.getActivity(), "只有VIP用户可以参与抽奖");
                break;
            case R.id.welfare_fraction:
                context.getActivity().startActivity(new Intent(context.getContext(), TaskRecordActivity.class));
                break;
        }
    }

    public void getTask() {
        OkHttpUtils.get(Urls.GET_TASK)
                .execute(new Task());
    }

    public void getWelfare() {
        OkHttpUtils.get(Urls.GET_WELFARE)
                .execute(new WelfareData());
    }

    public int dealWithTimeToSecond(String[] a) {
        int hour = Integer.parseInt(a[0]) * 3600;
        int minute = Integer.parseInt(a[1]) * 60;
        int second = Integer.parseInt(a[2]);
        int testAll = hour + minute + second;
        return testAll;
    }

    public void dealWithTimeToResult() {
        if (taskTime != -1 && GetServiceTime.systemTimeSecond != -1)
            getTime();
        calculateResult();
        setTextToCountDown();
    }

    public void getTime() {
        if (taskTime - GetServiceTime.systemTimeSecond > 0)
            surplusTime = taskTime - GetServiceTime.systemTimeSecond;
        else
            surplusTime = taskTime + (86400 - GetServiceTime.systemTimeSecond);
    }

    public void calculateResult() {
        a[0] = surplusTime / 3600;
        a[1] = (surplusTime % 3600) / 60;
        a[2] = (surplusTime % 3600) % 60;
    }

    public void setTextToCountDown() {
        countDownText.setTime(a[0], a[1], a[2]);
        countDownText.start();
    }

    public void registEventBus() {
        EventBus.getDefault().register(context);
    }

    public void shareSuccess() {
        OkHttpUtils.get(Urls.TASK_SUCCESS)
                .params("module_code", welfareTaskAdapter.getShareTaskInfo().getModule_code())
                .params("process_code", welfareTaskAdapter.getShareTaskInfo().getProcess_code())
                .execute(new WelfareEmptyData());

    }

    public void topSuccess() {
        OkHttpUtils.get(Urls.TASK_SUCCESS)
                .params("module_code", welfareTaskAdapter.getTopTaskInfo().getModule_code())
                .params("process_code", welfareTaskAdapter.getTopTaskInfo().getProcess_code())
                .execute(new WelfareData());
        getTask();
    }

    public void precisionSuccess() {
        OkHttpUtils.get(Urls.TASK_SUCCESS)
                .params("module_code", welfareTaskAdapter.getPrecisionTaskInfo().getModule_code())
                .params("process_code", welfareTaskAdapter.getPrecisionTaskInfo().getProcess_code())
                .execute(new WelfareData());
    }

    public void noPrizeSetting() {
        noPrizeImage.setVisibility(View.VISIBLE);
    }

    public void todayNoPrizeSetting() {
        gridRecyclerView.setVisibility(View.VISIBLE);
        luckDraw.setVisibility(View.VISIBLE);
        luckDraw.setBackgroundResource(R.drawable.welfare_gray_button_back);
        luckDraw.setEnabled(false);
    }

    public void todayHasPrize() {
        gridRecyclerView.setVisibility(View.VISIBLE);
        luckDraw.setVisibility(View.VISIBLE);
    }

    private void setPrizeLayout(GetWelfareResponse s) {
        if (s.getData().getPrizeListType() == 3)
            noPrizeSetting();
        else if (s.getData().getPrizeListType() == 2)
            todayNoPrizeSetting();
        else if (s.getData().getPrizeListType() == 1)
            todayHasPrize();
    }

    private void dealWithData(GetTask s) {
        String a = s.getData().getOpentime();
        String[] b = a.split(":");
        taskTime = dealWithTimeToSecond(b);
        alredyAddCount.setText(String.format(context.getString(R.string.alredy_add_count), s.getData().getGainscore()));
        fraction.setText(s.getData().getScore() + "");
        list = s.getData().getTasklist();
        welfareTaskAdapter.setData(list);
    }

    private class Task extends JsonCallback<GetTask> {
        @Override
        public void onResponse(boolean isFromCache, GetTask s, Request request, @Nullable Response response) {
            dealWithData(s);
            dealWithTimeToResult();
        }
    }

    private class WelfareData extends JsonCallback<GetWelfareResponse> {

        @Override
        public void onResponse(boolean isFromCache, GetWelfareResponse s, Request request, @Nullable Response response) {
            mAdapter.setData(s.getData().getPrizeList());
            title.setText(s.getData().getPrizeTitle());
            setPrizeLayout(s);
        }
    }


    private class WelfareEmptyData extends JsonCallback<String> {
        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
            getTask();
        }
    }
}
