package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.PersonalSettingActivityModel;

/**
 * Created by 58 on 2016/12/9.
 */

public class PersonalSettingActivity extends BaseActivity<PersonalSettingActivityModel>{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_setting);
    }

    @Override
    public PersonalSettingActivityModel createModel() {
        return new PersonalSettingActivityModel(this);
    }
}
