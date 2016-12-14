package com.merchantplatform.model;

import android.view.View;

import com.merchantplatform.activity.MobileValidateActivity;

/**
 * Created by 58 on 2016/12/14.
 */

public class MobileValidateActivityModel extends BaseModel implements View.OnClickListener{

   private MobileValidateActivity context;

    public MobileValidateActivityModel(MobileValidateActivity context){
        this.context = context;
    }

    @Override
    public void onClick(View v) {

    }
}
