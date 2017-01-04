package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.MobileValidateActivityModel;

/**
 * Created by 58 on 2016/12/14.
 */

public class MobileValidateActivity extends BaseActivity<MobileValidateActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_validate);
        init();
    }

    private void init(){
        model.initView();
        model.initData();
        model.setListener();
    }

    @Override
    public void onBackPressed() {
        model.confirmBack();
    }

    @Override
    public MobileValidateActivityModel createModel() {
        return new MobileValidateActivityModel(this);
    }
}
