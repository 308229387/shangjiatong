package com.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;


/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class HomepageBottomButton extends FrameLayout {
    private View view;
    private ImageView redHot;
    private TextView imageLayout;
    private HomepageActivity context;

    public HomepageBottomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = (HomepageActivity) context;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.homepage_bottom_button, this);
        initLayout();
    }

    private void initLayout() {
        imageLayout = (TextView) view.findViewById(R.id.tab_menu_setting);
        redHot = (ImageView) view.findViewById(R.id.tab_menu_red_hot);
    }

    public void dismissRedHot() {
        redHot.setVisibility(GONE);
    }

    public void registerState() {
        imageLayout.setSelected(false);
    }

    public void setSelectedState() {
        imageLayout.setSelected(true);
    }
}
