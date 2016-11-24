package com.merchantplatform.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.fragment.Fragment1;
import com.merchantplatform.fragment.Fragment4;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class Fragment4Model extends BaseModel {
    private Fragment4 context;
    private View view;

    public Fragment4Model(Fragment4 context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment4_layout, container, false);
    }

    public View getView() {

        return view;
    }

}
