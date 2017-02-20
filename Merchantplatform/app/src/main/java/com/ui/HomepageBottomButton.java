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
 * 描    述：带红点的底部导航按钮
 */

public class HomepageBottomButton extends FrameLayout {
    private View view;
    private ImageView redHot;
    private ImageView imageLayout;
    private TextView textLayout;
    private TextView redNum;
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
        imageLayout = (ImageView) view.findViewById(R.id.tab_drawable);
        textLayout = (TextView) view.findViewById(R.id.tab_menu_setting);
        redHot = (ImageView) view.findViewById(R.id.tab_menu_red_hot);
        redNum = (TextView) view.findViewById(R.id.tab_menu_red_num);
    }

    public void dismissRedHot() {
        redHot.setVisibility(GONE);
        redNum.setVisibility(GONE);
    }

    public void registerState() {
        imageLayout.setSelected(false);
        textLayout.setTextColor(getResources().getColor(R.color.home_bottom_not_color));
    }

    public void setNum(int num) {
        redNum.setText(num + "");
        redNum.setVisibility(VISIBLE);
    }

    public void setSelectedState() {
        imageLayout.setSelected(true);
        if (imageLayout.isSelected())
            textLayout.setTextColor(getResources().getColor(R.color.home_bottom_color));

    }

    public void setShowRedHot() {
        redHot.setVisibility(VISIBLE);
    }

    public void setTextInfo(String text) {
        textLayout.setText(text);
    }

    public void setDrawableInfo(int drawable) {
        imageLayout.setBackgroundResource(drawable);
    }
}
