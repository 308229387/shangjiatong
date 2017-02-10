package com.merchantplatform.activity;

import android.os.Bundle;

import com.merchantplatform.R;
import com.merchantplatform.model.TaskRecordModel;

/**
 * Created by songyongmeng on 2017/2/10.
 */

public class TaskRecordActivity extends BaseActivity<TaskRecordModel> {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_record_layout);
        model.init();

    }

    @Override
    public TaskRecordModel createModel() {
        return new TaskRecordModel(this);
    }
}
