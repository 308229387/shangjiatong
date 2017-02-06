package com.merchantplatform.model;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.Utils.TitleBar;
import com.merchantplatform.R;
import com.merchantplatform.activity.DailyLotteryActivity;

public class DailyLotteryModel extends BaseModel {

    private DailyLotteryActivity context;
    private TitleBar tb_daily_lottery_title;

    public DailyLotteryModel(DailyLotteryActivity context) {
        this.context = context;
    }

    public void initView() {
        tb_daily_lottery_title = (TitleBar) context.findViewById(R.id.tb_daily_lottery_title);
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
    }

    public void initData(Intent intent) {

    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    }
}
