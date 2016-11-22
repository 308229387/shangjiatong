package com.merchantplatform.model;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.merchantplatform.R;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private Activity context;
    private TextView text;

    public GuideActivityModel(Activity context) {
        this.context = context;
    }

    public void initLayout() {
        text = (TextView) context.findViewById(R.id.text_click);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "click", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showToast() {
        Toast.makeText(context, "test", Toast.LENGTH_LONG).show();
    }
}
