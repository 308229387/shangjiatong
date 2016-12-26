package com.merchantplatform.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.fragment.Fragment3;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class Fragment3Model extends BaseModel {
    private Fragment3 context;
    private View view;

    public Fragment3Model(Fragment3 context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment3_layout, container, false);
    }

    public View getView() {

        return view;
    }

}
