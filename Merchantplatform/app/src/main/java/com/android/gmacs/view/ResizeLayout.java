package com.android.gmacs.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.merchantplatform.R;
import com.android.gmacs.observer.OnInputSoftListener;

public class ResizeLayout extends LinearLayout {

    public boolean isSoftInputHidden;

    // 定义默认的软键盘最小高度，这是为了避免onSizeChanged在某些下特殊情况下出现的问题
    private final int SOFTKEYPAD_MIN_HEIGHT = getResources().getDimensionPixelOffset(R.dimen.navigation_bar_height);

    private OnInputSoftListener onInputSoftListener;
    //    private XXListView xxListView;
    private Handler uiHandler = new Handler();

    public ResizeLayout(Context context) {
        super(context);
    }

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setInputSoftListener(OnInputSoftListener listener) {
        onInputSoftListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                isSoftInputHidden = false;
                if (oldh - h > SOFTKEYPAD_MIN_HEIGHT) {
                    onInputSoftListener.onShow();
                } else if (h - oldh > SOFTKEYPAD_MIN_HEIGHT) {
                    // oldh === 0 when inflates
                    if (oldh != 0) {
                        isSoftInputHidden = true;
                    }
                    onInputSoftListener.onHide();
                }
            }
        });
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return xxListView.isAnimationGoing();
//    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        xxListView = (XXListView) findViewById(R.id.listview_chat);
//    }
}
