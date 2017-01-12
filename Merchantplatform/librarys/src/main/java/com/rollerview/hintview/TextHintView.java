package com.rollerview.hintview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.librarys.R;
import com.rollerview.HintView;
import com.rollerview.Util;


public class TextHintView extends TextView implements HintView {
    private int length;

    private Context context;

    public TextHintView(Context context) {
        super(context);
        this.context = context;
    }

    public TextHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initView(int length, int gravity) {
        this.length = length;
        setTextColor(Color.WHITE);
//		switch (gravity) {
//		case 0:
//			setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
//			break;
//		case 1:
//			setGravity(Gravity.CENTER);
//			break;
//		case 2:
//			break;
//		}
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.setMargins(0, 0, Util.dip2px(context, 10), Util.dip2px(context, 10));
        setLayoutParams(lp);
        setCurrent(0);
        setBackgroundResource(R.drawable.roller_hint_bg);
    }

    @Override
    public void setCurrent(int current) {
        setText(current + 1 + "/" + length);
    }
}
