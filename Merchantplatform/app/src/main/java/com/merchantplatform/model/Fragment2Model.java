package com.merchantplatform.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;
import com.merchantplatform.fragment.Fragment1;
import com.merchantplatform.fragment.Fragment2;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class Fragment2Model extends BaseModel {
    private Fragment2 context;
    private View view;

    public Fragment2Model(Fragment2 context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment2_layout, container, false);
    }

    public View getView() {

        return view;
    }

}
