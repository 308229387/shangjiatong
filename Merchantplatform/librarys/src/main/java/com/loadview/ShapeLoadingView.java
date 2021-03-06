package com.loadview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.librarys.R;


/**
 * Created by zzz40500 on 15/4/4.
 */
public class ShapeLoadingView extends View {

    private float redis = 15f;
    private int color = R.color.load_red;

    public ShapeLoadingView(Context context) {
        super(context);
        init();
    }

    public ShapeLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShapeLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        setBackgroundColor(getResources().getColor(R.color.view_bg));
    }

    private Paint mPaint;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getVisibility() == GONE) {
            return;
        }
        mPaint.setColor(getResources().getColor(color));

        Path path = new Path();
        path.addCircle(dip2px(redis), dip2px(redis), dip2px(redis), Path.Direction.CW);
        path.close();
        canvas.drawPath(path, mPaint);

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == VISIBLE) {
            invalidate();
        }
    }

    public float getRedis() {
        return redis;
    }

    public void setRedis(float redis) {
        this.redis = redis;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
