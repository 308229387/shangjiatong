package com.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.merchantplatform.R;
import com.merchantplatform.bean.SettingToggleEntry;


public class SettingToggleGroup extends LinearLayout {
    public SettingToggleGroup(Context paramContext) {
        super(paramContext);
        initView();
    }

    public SettingToggleGroup(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initView();
    }

    public SettingToggleGroup(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView();
    }

    private void addDivider() {
        View localView = new View(getContext());
        localView.setBackgroundColor(getResources().getColor(R.color.line_inner_color));
        int i = (int) getResources().getDimension(R.dimen.personal_center_entry_padding_left_right);
        LayoutParams localLayoutParams = new LayoutParams(-1, 1);
        localLayoutParams.leftMargin = i;
        localLayoutParams.rightMargin = i;
        addView(localView, localLayoutParams);
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
    }

    public SettingToggleEntry addEntry() {
        if (getChildCount() > 0)
            addDivider();
        SettingToggleEntry localCommonEntry = new SettingToggleEntry(getContext());
        addView(localCommonEntry);
        return localCommonEntry;
    }

    public SettingToggleEntry entry(int paramInt) {
        int i = 2 * (paramInt - 1);
        if ((i < 0) || (i > -1 + getChildCount()))
            return null;
        View localView = getChildAt(i);
        if (!(localView instanceof SettingToggleEntry))
            return null;
        return (SettingToggleEntry) localView;
    }
}