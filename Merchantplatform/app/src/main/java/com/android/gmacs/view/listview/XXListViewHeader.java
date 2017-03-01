package com.android.gmacs.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.merchantplatform.R;

/**
 * 下拉ListView顶部加载更多
 */
public class XXListViewHeader extends LinearLayout {
    private RelativeLayout mContainer;

    public XXListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    public XXListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        mContainer = (RelativeLayout) LayoutInflater.from(context).inflate(
                R.layout.gmacs_xxlistview_header, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);
    }

    public void setVisibleHeight(int height) {
        if (height < 0)
            height = 0;
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

}
