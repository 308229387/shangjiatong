package com.merchantplatform.model;

import android.app.Activity;
import android.widget.Toast;

import com.merchantplatform.model.BaseModel;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;

    public GuideActivityModel(Activity context) {
        this.context = context;
    }

    public void showToast() {
        Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
    }
}
