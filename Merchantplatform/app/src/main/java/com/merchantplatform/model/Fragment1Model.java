package com.merchantplatform.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.fragment.Fragment1;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class Fragment1Model extends BaseModel {
    private Fragment1 context;
    private View view;

    public Fragment1Model(Fragment1 context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment1_layout, container, false);
    }

    public View getView() {

        return view;
    }

}
