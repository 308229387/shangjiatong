package com.merchantplatform.model;

import android.support.v7.widget.LinearLayoutManager;

import com.merchantplatform.R;
import com.merchantplatform.activity.TaskRecordActivity;
import com.xrecyclerview.XRecyclerView;

/**
 * Created by songyongmeng on 2017/2/10.
 */
public class TaskRecordModel extends BaseModel{
    private TaskRecordActivity context;
    private XRecyclerView listRecyclerView;
    private LinearLayoutManager mLayoutManager;

    public TaskRecordModel(TaskRecordActivity context) {
        this.context = context;
    }

    public void init() {
        listRecyclerView = (XRecyclerView)context.findViewById(R.id.task_record_recycler);
        mLayoutManager = new LinearLayoutManager(context);
        listRecyclerView.setLayoutManager(mLayoutManager);
        listRecyclerView.setHasFixedSize(true);
    }
}
