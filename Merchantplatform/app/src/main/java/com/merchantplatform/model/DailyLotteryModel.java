package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils.TitleBar;
import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;
import com.merchantplatform.adapter.DailyAwardAdapter;
import com.merchantplatform.adapter.ExplainMessageAdapter;
import com.merchantplatform.bean.LotteryDetailResponse;
import com.merchantplatform.service.GetServiceTime;
import com.okhttputils.OkHttpUtils;
import com.ui.ScratchView;
import com.ui.SpaceItemDecoration;
import com.utils.DisplayUtils;
import com.utils.Urls;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

public class DailyLotteryModel extends BaseModel {

    private DailyLotteryActivity context;
    private TitleBar tb_daily_lottery_title;
    private RecyclerView rv_daily_lottery_award, rv_lottery_explain;
    private TextView tv_daily_lottery_grade, tv_daily_lottery_start, tv_daily_lottery_count_down, tv_daily_lottery_result, tv_detail_lottery_gotorecord;
    private Button bt_daily_lottery_start, bt_daily_lottery_check, bt_daily_lottery_again;
    private ScratchView sv_daily_lottery;
    private RelativeLayout rl_daily_lottery_start, rl_daily_lottery_result;
    private ScrollView scrollview;
    private DailyAwardAdapter dailyAwardAdapter;
    private ArrayList<LotteryDetailResponse.award> dailyAward;
    private ExplainMessageAdapter explainAdapter;
    private ArrayList<String> explainMsg;
    private int openTime = -1;
    private int endTime = -1;

    public DailyLotteryModel(DailyLotteryActivity context) {
        this.context = context;
    }

    public void initView() {
        scrollview = (ScrollView) context.findViewById(R.id.scrollview);
        tb_daily_lottery_title = (TitleBar) context.findViewById(R.id.tb_daily_lottery_title);
        rv_daily_lottery_award = (RecyclerView) context.findViewById(R.id.rv_daily_lottery_award);
        tv_daily_lottery_grade = (TextView) context.findViewById(R.id.tv_daily_lottery_grade);
        tv_daily_lottery_start = (TextView) context.findViewById(R.id.tv_daily_lottery_start);
        tv_daily_lottery_count_down = (TextView) context.findViewById(R.id.tv_daily_lottery_count_down);
        tv_daily_lottery_result = (TextView) context.findViewById(R.id.tv_daily_lottery_result);
        tv_detail_lottery_gotorecord = (TextView) context.findViewById(R.id.tv_detail_lottery_gotorecord);
        bt_daily_lottery_start = (Button) context.findViewById(R.id.bt_daily_lottery_start);
        bt_daily_lottery_check = (Button) context.findViewById(R.id.bt_daily_lottery_check);
        bt_daily_lottery_again = (Button) context.findViewById(R.id.bt_daily_lottery_again);
        sv_daily_lottery = (ScratchView) context.findViewById(R.id.sv_daily_lottery);
        rl_daily_lottery_start = (RelativeLayout) context.findViewById(R.id.rl_daily_lottery_start);
        rl_daily_lottery_result = (RelativeLayout) context.findViewById(R.id.rl_daily_lottery_result);
        rv_lottery_explain = (RecyclerView) context.findViewById(R.id.rv_lottery_explain);
    }

    public void setTitleBar() {
        tb_daily_lottery_title.setImmersive(true);
        tb_daily_lottery_title.setBackgroundColor(Color.WHITE);
        tb_daily_lottery_title.setLeftImageResource(R.mipmap.title_back);
        tb_daily_lottery_title.setTitle("每日抽奖");
        tb_daily_lottery_title.setTitleColor(Color.BLACK);
    }

    public void setLotteryAward() {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_daily_lottery_award.setLayoutManager(layoutManager);
        rv_daily_lottery_award.setHasFixedSize(true);
        dailyAward = new ArrayList<>();
        dailyAwardAdapter = new DailyAwardAdapter(context, dailyAward);
        rv_daily_lottery_award.setAdapter(dailyAwardAdapter);
        rv_daily_lottery_award.addItemDecoration(new SpaceItemDecoration(15));
    }

    public void setLotteryExplain() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_lottery_explain.setLayoutManager(layoutManager);
        rv_lottery_explain.setHasFixedSize(true);
        explainMsg = new ArrayList<>();
        explainAdapter = new ExplainMessageAdapter(context, explainMsg);
        rv_lottery_explain.setAdapter(explainAdapter);
    }

    public void setListener() {
        tb_daily_lottery_title.setLeftClickListener(new OnBackPressed());
        bt_daily_lottery_start.setOnClickListener(new OnStartLottery());
        sv_daily_lottery.setEraseStatusListener(new OnEraserStatusListener());
        sv_daily_lottery.setOnTouchListener(new OnLotteryTouchListener());
        bt_daily_lottery_check.setOnClickListener(new OnCheckLottery());
        bt_daily_lottery_again.setOnClickListener(new OnLotteryAgain());
        tv_detail_lottery_gotorecord.setOnClickListener(new OnGoToRecord());
    }

    public void getServerTime() {
        Intent startIntent = new Intent(context, GetServiceTime.class);
        context.startService(startIntent);
    }

    public void initData() {
        OkHttpUtils.get(Urls.DAILY_LOTTERY)
                .execute(new getDailyLottery(context, false));
    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    }

    private class getDailyLottery extends DialogCallback<LotteryDetailResponse> {

        public getDailyLottery(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, LotteryDetailResponse lotteryDetailResponse, Request request, @Nullable Response response) {
            if (lotteryDetailResponse != null && lotteryDetailResponse.getData() != null) {
                setGrade(lotteryDetailResponse);
                setDailyAward(lotteryDetailResponse);
                setTimeSection(lotteryDetailResponse);
                setExplainMessage(lotteryDetailResponse);
            }
        }
    }

    private void setGrade(LotteryDetailResponse lotteryDetailResponse) {
        tv_daily_lottery_grade.setText(lotteryDetailResponse.getData().getScore() + "");
    }

    private void setDailyAward(LotteryDetailResponse lotteryDetailResponse) {
        dailyAward.addAll(lotteryDetailResponse.getData().getPrizeList());
        dailyAwardAdapter.notifyDataSetChanged();
    }

    private void setTimeSection(LotteryDetailResponse lotteryDetailResponse) {
        String openTimes = lotteryDetailResponse.getData().getOpenTime();
        String endTimes = lotteryDetailResponse.getData().getEndTime();
        openTime = WelfareModel.dealWithTimeToSecond(openTimes.split(":"));
        endTime = WelfareModel.dealWithTimeToSecond(endTimes.split(":"));
    }

    private void setExplainMessage(LotteryDetailResponse lotteryDetailResponse) {
        explainMsg.addAll(lotteryDetailResponse.getData().getDescription());
        explainAdapter.notifyDataSetChanged();
    }

    private class OnStartLottery implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            rl_daily_lottery_start.setVisibility(View.GONE);
            sv_daily_lottery.setVisibility(View.VISIBLE);
            rl_daily_lottery_result.setVisibility(View.VISIBLE);
        }
    }

    private class OnEraserStatusListener implements ScratchView.EraseStatusListener {
        @Override
        public void onCompleted(View view) {
            sv_daily_lottery.clear();
            sv_daily_lottery.setVisibility(View.GONE);
        }
    }

    private class OnCheckLottery implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "查看自己的中奖记录", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnLotteryTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP)
                scrollview.requestDisallowInterceptTouchEvent(false);
            else
                scrollview.requestDisallowInterceptTouchEvent(true);
            return false;
        }
    }

    private class OnLotteryAgain implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            sv_daily_lottery.reset();
            sv_daily_lottery.setVisibility(View.VISIBLE);
            if (bt_daily_lottery_check.getVisibility() == View.VISIBLE) {
                tv_daily_lottery_result.setText("很遗憾，没刮中，请再接再厉！");
                bt_daily_lottery_check.setVisibility(View.GONE);
                ViewGroup.LayoutParams layoutParams = bt_daily_lottery_again.getLayoutParams();
                layoutParams.width = layoutParams.width + DisplayUtils.dpToPx(60, context);
                bt_daily_lottery_again.setLayoutParams(layoutParams);
            } else {
                tv_daily_lottery_result.setText("恭喜您！抽中全额商机100元");
                bt_daily_lottery_check.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = bt_daily_lottery_again.getLayoutParams();
                layoutParams.width = layoutParams.width - DisplayUtils.dpToPx(60, context);
                bt_daily_lottery_again.setLayoutParams(layoutParams);
            }
        }
    }

    private class OnGoToRecord implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "查看所有人中奖记录", Toast.LENGTH_SHORT).show();
        }
    }
}