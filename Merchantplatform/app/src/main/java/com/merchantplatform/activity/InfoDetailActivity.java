package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.InfoDetailModel;
import com.utils.eventbus.EventAction;
import com.utils.eventbus.EventType;

import org.greenrobot.eventbus.Subscribe;

public class InfoDetailActivity extends BaseActivity<InfoDetailModel> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_detail);
        model.initView();
        model.initData();
        model.registerEventBus();
    }

    @Override
    public InfoDetailModel createModel() {
        return new InfoDetailModel(this);
    }

    @Subscribe
    public void refreshData(EventAction action) {
        if (action.getType() == EventType.PRECISION_PROMOTE_SUCCESS)
            model.initData();
    }

    @Override
    public void onDestroy() {
        model.unRegisterEventBus();
        super.onDestroy();
    }

}
