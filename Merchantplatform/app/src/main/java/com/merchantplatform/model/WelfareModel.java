package com.merchantplatform.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;

/**
 * Created by songyongmeng on 2017/2/3.
 */

public class WelfareModel extends BaseModel {
    private View view;

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_welfare_layout, container, false);
    }

    public View getView() {
        return view;
    }
}
