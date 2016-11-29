package com.android.gmacs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.common.gmacs.utils.GmacsEnvi;

/**
 * Created by YanQi on 16/9/14.
 */
public class FastScrollView extends ScrollView {

    private float scrollY;
    private int ratio = 2;

    public FastScrollView(Context context) {
        super(context);
    }

    public FastScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FastScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollY = ev.getY();
                break;
            default:
                if (Math.abs(scrollY - ev.getY()) >= GmacsEnvi.screenHeight * 0.3f) {
                    ratio = 8;
                } else {
                    ratio = 2;
                }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY * ratio);
    }

}
