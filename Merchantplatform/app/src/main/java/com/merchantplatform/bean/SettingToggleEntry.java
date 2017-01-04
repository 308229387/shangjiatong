package com.merchantplatform.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.merchantplatform.R;


public class SettingToggleEntry extends LinearLayout {

    protected ImageView mIcon;

    protected TextView mSubTitle;

    protected TextView mTitle;

    protected ToggleButton mToggleBtn;

    protected View ll_toggle_btn;

    public SettingToggleEntry(Context paramContext) {
        super(paramContext);
        initView(paramContext);
    }

    public SettingToggleEntry(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initView(paramContext);
    }

    public SettingToggleEntry(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initView(paramContext);
    }

    public SettingToggleEntry click(OnClickListener paramOnClickListener) {
        setOnClickListener(paramOnClickListener);
        return this;
    }

    public SettingToggleEntry toggleChecked(boolean state) {
        if (this.mToggleBtn != null) {
            this.mToggleBtn.setChecked(state);
        }
        return this;
    }

    public SettingToggleEntry toggleChangeClick(CompoundButton.OnCheckedChangeListener listener) {
        if (this.mToggleBtn != null) {
            this.mToggleBtn.setOnCheckedChangeListener(listener);
        }
        if (this.ll_toggle_btn != null) {
            ll_toggle_btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mToggleBtn.performClick();
                }
            });
        }
        return this;
    }

    public SettingToggleEntry icon(int paramInt) {
        this.mIcon.setImageResource(paramInt);
        return this;
    }

    protected void initView(Context paramContext) {
        LayoutInflater.from(paramContext).inflate(R.layout.setting_toggle_entry, this);
        mIcon = (ImageView) findViewById(R.id.iv_entry_icon);
        mSubTitle = (TextView) findViewById(R.id.tv_entry_sub_title);
        mTitle = (TextView) findViewById(R.id.tv_entry_title);
        mToggleBtn = (ToggleButton) findViewById(R.id.btn_toggle);
        ll_toggle_btn = findViewById(R.id.ll_toggle_btn);
    }

    public SettingToggleEntry subTitle(String paramString) {
        this.mSubTitle.setText(paramString);
        return this;
    }

    public SettingToggleEntry title(String paramString) {
        this.mTitle.setText(paramString);
        return this;
    }

    public void setOnToggleButtonClick(View.OnClickListener listener) {
        this.mToggleBtn.setOnClickListener(listener);
    }
}