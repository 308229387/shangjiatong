package com.merchantplatform.model;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utils.TitleBar;
import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;
import com.merchantplatform.adapter.GridDrawAdapter;
import com.ui.ScratchView;
import com.ui.SpaceItemDecoration;
import com.utils.DisplayUtils;

public class DailyLotteryModel extends BaseModel {

    private DailyLotteryActivity context;
    private TitleBar tb_daily_lottery_title;
    private RecyclerView rv_daily_lottery_award;
    private TextView tv_daily_lottery_grade, tv_daily_lottery_start, tv_daily_lottery_count_down, tv_daily_lottery_result, tv_detail_lottery_gotorecord;
    private Button bt_daily_lottery_start, bt_daily_lottery_check, bt_daily_lottery_again;
    private ScratchView sv_daily_lottery;
    private RelativeLayout rl_daily_lottery_start, rl_daily_lottery_result;
    private GridDrawAdapter mAdapter;

    public DailyLotteryModel(DailyLotteryActivity context) {
        this.context = context;
    }

    public void initView() {
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
    }

    public void setRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(context, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rv_daily_lottery_award.setLayoutManager(layoutManager);
        rv_daily_lottery_award.setHasFixedSize(true);
        mAdapter = new GridDrawAdapter(context);
        rv_daily_lottery_award.setAdapter(mAdapter);
        rv_daily_lottery_award.addItemDecoration(new SpaceItemDecoration(15));
    }

    public void setTitleBar() {
        tb_daily_lottery_title.setImmersive(true);
        tb_daily_lottery_title.setBackgroundColor(Color.WHITE);
        tb_daily_lottery_title.setLeftImageResource(R.mipmap.title_back);
        tb_daily_lottery_title.setTitle("每日抽奖");
        tb_daily_lottery_title.setTitleColor(Color.BLACK);
    }

    public void setListener() {
        tb_daily_lottery_title.setLeftClickListener(new OnBackPressed());
        bt_daily_lottery_start.setOnClickListener(new OnStartLottery());
        sv_daily_lottery.setEraseStatusListener(new OnEraserStatusListener());
        bt_daily_lottery_again.setOnClickListener(new OnLotteryAgain());
    }

    public void initData(Intent intent) {

    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    }

    private class OnStartLottery implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            rl_daily_lottery_start.setVisibility(View.GONE);
        }
    }

    private class OnEraserStatusListener implements ScratchView.EraseStatusListener {
        @Override
        public void onCompleted(View view) {
            sv_daily_lottery.clear();
            sv_daily_lottery.setVisibility(View.GONE);
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
}