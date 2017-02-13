package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.merchantplatform.bean.LotteryResultResponse;
import com.merchantplatform.service.GetServiceTime;
import com.okhttputils.OkHttpUtils;
import com.ui.ScratchCountDownTimerView;
import com.ui.ScratchView;
import com.ui.SpaceItemDecoration;
import com.utils.DisplayUtils;
import com.utils.ToastUtils;
import com.utils.Urls;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

public class DailyLotteryModel extends BaseModel {

    private DailyLotteryActivity context;
    private TitleBar tb_daily_lottery_title;
    private RecyclerView rv_daily_lottery_award, rv_lottery_explain;
    private TextView tv_daily_lottery_grade, tv_daily_lottery_start, tv_daily_lottery_result, tv_detail_lottery_gotorecord;
    private ScratchCountDownTimerView sc_daily_lottery_count_down;
    private Button bt_daily_lottery_start, bt_daily_lottery_check, bt_daily_lottery_again;
    private ScratchView sv_daily_lottery;
    private RelativeLayout rl_daily_lottery_start, rl_daily_lottery_result;
    private ScrollView scrollview;
    private DailyAwardAdapter dailyAwardAdapter;
    private ArrayList<LotteryDetailResponse.award> dailyAward;
    private ExplainMessageAdapter explainAdapter;
    private ArrayList<String> explainMsg;
    private int score = 0;
    private int openTime = -1;
    private int endTime = -1;
    private int surplusTime;

    public DailyLotteryModel(DailyLotteryActivity context) {
        this.context = context;
    }

    public void initView() {
        scrollview = (ScrollView) context.findViewById(R.id.scrollview);
        tb_daily_lottery_title = (TitleBar) context.findViewById(R.id.tb_daily_lottery_title);
        rv_daily_lottery_award = (RecyclerView) context.findViewById(R.id.rv_daily_lottery_award);
        tv_daily_lottery_grade = (TextView) context.findViewById(R.id.tv_daily_lottery_grade);
        tv_daily_lottery_start = (TextView) context.findViewById(R.id.tv_daily_lottery_start);
        sc_daily_lottery_count_down = (ScratchCountDownTimerView) context.findViewById(R.id.sc_daily_lottery_count_down);
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
        sc_daily_lottery_count_down.setOnCountDownListener(new OnCountDownFinish());
        sv_daily_lottery.setEraseStatusListener(new OnEraserStatusListener());
        sv_daily_lottery.setOnTouchListener(new OnLotteryTouchListener());
        bt_daily_lottery_check.setOnClickListener(new OnCheckLottery());
        bt_daily_lottery_again.setOnClickListener(new OnStartLottery());
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

    private class OnCountDownFinish implements ScratchCountDownTimerView.OnCountDownListener {

        @Override
        public void onFinish() {
            //倒计时结束
            //刮奖开始倒计时结束之后，倒计时重置为刮奖结束倒计时，然后按钮变为可点击
            //刮奖结束倒计时结束后，倒计时重置为刮奖开始倒计时，按钮置灰并不可点击
        }
    }

    private class getDailyLottery extends DialogCallback<LotteryDetailResponse> {

        public getDailyLottery(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, LotteryDetailResponse lotteryDetailResponse, Request request, @Nullable Response response) {
            if (lotteryDetailResponse != null && lotteryDetailResponse.getData() != null) {
                if (lotteryDetailResponse.getData().getIsVip() == 1) {
                    setGrade(lotteryDetailResponse);
                    setDailyAward(lotteryDetailResponse);
                    setTimeSection(lotteryDetailResponse);
                    setExplainMessage(lotteryDetailResponse);
                    setIfCanStartScratch();
                } else {
                    ToastUtils.showToast("只有VIP客户可以参与刮奖");
                    context.finish();
                }
            }
        }
    }

    private void setGrade(LotteryDetailResponse lotteryDetailResponse) {
        score = lotteryDetailResponse.getData().getScore();
        tv_daily_lottery_grade.setText(score + "");
    }

    private void setDailyAward(LotteryDetailResponse lotteryDetailResponse) {
        if (lotteryDetailResponse.getData().getPrizeList() != null) {
            dailyAward.addAll(lotteryDetailResponse.getData().getPrizeList());
            dailyAwardAdapter.notifyDataSetChanged();
        }
    }

    private void setTimeSection(LotteryDetailResponse lotteryDetailResponse) {
        String openTimes = lotteryDetailResponse.getData().getOpenTime();
        String endTimes = lotteryDetailResponse.getData().getEndTime();
        openTime = dealWithTimeToSecond(openTimes.split(":"));
        endTime = dealWithTimeToSecond(endTimes.split(":"));
        if (isInTimeSection()) {
            surplusTime = endTime - GetServiceTime.systemTimeSecond;
        } else {
            if (GetServiceTime.systemTimeSecond < 86400)
                surplusTime = 86400 - GetServiceTime.systemTimeSecond + openTime;
            else
                surplusTime = openTime - GetServiceTime.systemTimeSecond;
        }
    }

    private int dealWithTimeToSecond(String[] a) {
        int hour = Integer.parseInt(a[0]) * 3600;
        int minute = Integer.parseInt(a[1]) * 60;
        int second = Integer.parseInt(a[2]);
        int testAll = hour + minute + second;
        return testAll;
    }

    private void setExplainMessage(LotteryDetailResponse lotteryDetailResponse) {
        explainMsg.addAll(lotteryDetailResponse.getData().getDescription());
        explainAdapter.notifyDataSetChanged();
    }

    private void setIfCanStartScratch() {
        if (isInTimeSection() && score > 0) {
            canScratchLottery();
        } else if (score <= 0) {
            noMoreScore();
        } else {
            notInTimeSection();
        }
    }

    private boolean isInTimeSection() {
        return GetServiceTime.systemTimeSecond >= openTime && GetServiceTime.systemTimeSecond <= endTime;
    }

    private void canScratchLottery() {
        tv_daily_lottery_start.setText("刮奖结束倒计时");
        sc_daily_lottery_count_down.setVisibility(View.VISIBLE);
        sc_daily_lottery_count_down.setTime(calculateResult()[0], calculateResult()[1], calculateResult()[2]);
        sc_daily_lottery_count_down.start();
        bt_daily_lottery_start.setOnClickListener(new OnStartLottery());
        bt_daily_lottery_start.setText("点我刮奖");
        bt_daily_lottery_start.setBackgroundColor(context.getResources().getColor(R.color.home_bottom_color));
    }

    private void noMoreScore() {
        tv_daily_lottery_start.setText("积分已耗尽，无法参与抽奖~");
        sc_daily_lottery_count_down.setVisibility(View.GONE);
        bt_daily_lottery_start.setOnClickListener(new OnGoToWelfare());
        bt_daily_lottery_start.setText("去做任务攒积分");
        bt_daily_lottery_start.setClickable(true);
        bt_daily_lottery_start.setBackgroundColor(context.getResources().getColor(R.color.home_bottom_color));
    }

    private void notInTimeSection() {
        tv_daily_lottery_start.setText("刮奖开始倒计时");
        sc_daily_lottery_count_down.setVisibility(View.VISIBLE);
        sc_daily_lottery_count_down.setTime(calculateResult()[0], calculateResult()[1], calculateResult()[2]);
        sc_daily_lottery_count_down.start();
        bt_daily_lottery_start.setClickable(false);
        bt_daily_lottery_start.setText("点我刮奖");
        bt_daily_lottery_start.setBackgroundColor(context.getResources().getColor(R.color.a_grade_pressed));
    }

    private int[] calculateResult() {
        int[] a = new int[3];
        a[0] = surplusTime / 3600;
        a[1] = (surplusTime % 3600) / 60;
        a[2] = (surplusTime % 3600) % 60;
        return a;
    }

    private class OnStartLottery implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            requestLotteryResult();
        }
    }

    private void requestLotteryResult() {
        if (score > 0) {
            if (isInTimeSection()) {
                getLotteryResult();
            } else {
                notInTimeSection();
            }
        } else {
            noMoreScore();
        }
    }

    private void getLotteryResult() {
        OkHttpUtils.get(Urls.GET_LOTTERY)
                .execute(new getLotteryResult(context, false));
    }

    private class getLotteryResult extends DialogCallback<LotteryResultResponse> {

        public getLotteryResult(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onResponse(boolean isFromCache, LotteryResultResponse lotteryResultResponse, Request request, @Nullable Response response) {
            if (lotteryResultResponse != null && lotteryResultResponse.getData() != null) {
                updateNewestMessage(lotteryResultResponse);
                setScratchUI();
                setLotteryResult(lotteryResultResponse);
            }
        }
    }

    private void updateNewestMessage(LotteryResultResponse lotteryResultResponse) {
        score = lotteryResultResponse.getData().getScore();
        String openTimes = lotteryResultResponse.getData().getOpenTime();
        String endTimes = lotteryResultResponse.getData().getEndTime();
        openTime = dealWithTimeToSecond(openTimes.split(":"));
        endTime = dealWithTimeToSecond(endTimes.split(":"));
    }

    private void setScratchUI() {
        tv_daily_lottery_grade.setText(score + "");
        rl_daily_lottery_start.setVisibility(View.GONE);
        sv_daily_lottery.reset();
        sv_daily_lottery.setVisibility(View.VISIBLE);
        rl_daily_lottery_result.setVisibility(View.VISIBLE);
    }

    private void setLotteryResult(LotteryResultResponse lotteryResultResponse) {
        if (lotteryResultResponse.getData().getPrizeDrawState() == 0) {
            if (!TextUtils.isEmpty(lotteryResultResponse.getData().getMsg())) {
                Toast.makeText(context, lotteryResultResponse.getData().getMsg(), Toast.LENGTH_SHORT).show();
                context.finish();
            }
        } else if (lotteryResultResponse.getData().getPrizeDrawState() == 1) {
            tv_daily_lottery_result.setText("很遗憾，没刮中，请再接再厉！");
            bt_daily_lottery_check.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParams = bt_daily_lottery_again.getLayoutParams();
            layoutParams.width = layoutParams.width + DisplayUtils.dpToPx(60, context);
            bt_daily_lottery_again.setLayoutParams(layoutParams);
        } else if (lotteryResultResponse.getData().getPrizeDrawState() == 2) {
            if (!TextUtils.isEmpty(lotteryResultResponse.getData().getPrizeName())) {
                tv_daily_lottery_result.setText("恭喜您！抽中" + lotteryResultResponse.getData().getPrizeName());
            }
            bt_daily_lottery_check.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = bt_daily_lottery_again.getLayoutParams();
            layoutParams.width = layoutParams.width - DisplayUtils.dpToPx(60, context);
            bt_daily_lottery_again.setLayoutParams(layoutParams);
        }
    }

    private class OnEraserStatusListener implements ScratchView.EraseStatusListener {
        @Override
        public void onCompleted(View view) {
            sv_daily_lottery.clear();
            sv_daily_lottery.setVisibility(View.GONE);
        }
    }

    private class OnGoToWelfare implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            context.finish();
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

    private class OnGoToRecord implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(context, "查看所有人中奖记录", Toast.LENGTH_SHORT).show();
        }
    }
}