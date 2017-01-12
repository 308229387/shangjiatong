package com.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.merchantplatform.R;

public class ScratchView extends View {

    private final static float DEFAULT_ERASER_SIZE = 50f;
    private final static int DEFAULT_MASKER_COLOR = 0xffcccccc;
    private final static int DEFAULT_PERCENT = 50;
    private Paint mMaskPaint;
    private Bitmap mMaskBitmap;
    private Canvas mMaskCanvas;
    private Paint mBitmapPaint;
    private Paint mMaskTextPaint;
    private Rect mMaskTextBound;
    private String maskText;
    private Paint mErasePaint;
    private Path mErasePath;
    private float mStartX;
    private float mStartY;
    private int mTouchSlop;
    private boolean mIsCompleted = false;
    private int mMaxPercent = DEFAULT_PERCENT;
    private int mPixels[];

    private EraseStatusListener mEraseStatusListener;

    public ScratchView(Context context) {
        super(context);
        TypedArray typedArray = context.obtainStyledAttributes(R.styleable.ScratchView);
        init(typedArray);
    }

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView);
        init(typedArray);
    }

    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, defStyleAttr, 0);
        init(typedArray);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScratchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScratchView, defStyleAttr, defStyleRes);
        init(typedArray);
    }

    private void init(TypedArray typedArray) {
        int maskColor = typedArray.getColor(R.styleable.ScratchView_maskColor, DEFAULT_MASKER_COLOR);
        maskText = typedArray.getString(R.styleable.ScratchView_maskText);
        float eraseSize = typedArray.getFloat(R.styleable.ScratchView_eraseSize, DEFAULT_ERASER_SIZE);
        mMaxPercent = typedArray.getInt(R.styleable.ScratchView_maxPercent, DEFAULT_PERCENT);
        typedArray.recycle();
        mMaskPaint = new Paint();
        mMaskPaint.setAntiAlias(true);
        mMaskPaint.setDither(true);
        setMaskColor(maskColor);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        setMaskPaint();
        mErasePaint = new Paint();
        mErasePaint.setAntiAlias(true);
        mErasePaint.setDither(true);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setStyle(Paint.Style.STROKE);
        mErasePaint.setStrokeCap(Paint.Cap.ROUND);
        setEraserSize(eraseSize);
        mErasePath = new Path();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        mTouchSlop = viewConfiguration.getScaledTouchSlop();
    }

    public void setEraserSize(float eraserSize) {
        mErasePaint.setStrokeWidth(eraserSize);
    }

    public void setMaskColor(int color) {
        mMaskPaint.setColor(color);
    }

    public void setMaskText(String maskText) {
        this.maskText = maskText;
    }

    public void setMaxPercent(int max) {
        if (max > 100 || max <= 0) {
            return;
        }
        this.mMaxPercent = max;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measureSize(widthMeasureSpec);
        int measuredHeight = measureSize(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mMaskBitmap, 0, 0, mBitmapPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startErase(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                erase(event.getX(), event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                stopErase();
                invalidate();
                return true;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMasker(w, h);
    }

    private void createMasker(int width, int height) {
        mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);
        Rect rect = new Rect(0, 0, width, height);
        mMaskCanvas.drawRect(rect, mMaskPaint);
        if (mMaskTextPaint != null && !TextUtils.isEmpty(maskText)) {
            mMaskTextPaint.getTextBounds(maskText, 0, maskText.length(), mMaskTextBound);
            mMaskCanvas.drawText(maskText, width / 2 - mMaskTextBound.width() / 2, height / 2 + mMaskTextBound.height() / 2, mMaskTextPaint);
        }
        mPixels = new int[width * height];
    }

    private int measureSize(int measureSpec) {
        int size = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            size = specSize;
        } else {
            if (specMode == MeasureSpec.AT_MOST) {
                size = Math.min(size, specSize);
            }
        }
        return size;
    }

    private void startErase(float x, float y) {
        mErasePath.reset();
        mErasePath.moveTo(x, y);
        this.mStartX = x;
        this.mStartY = y;
    }

    private void erase(float x, float y) {
        int dx = (int) Math.abs(x - mStartX);
        int dy = (int) Math.abs(y - mStartY);
        if (dx >= mTouchSlop || dy >= mTouchSlop) {
            this.mStartX = x;
            this.mStartY = y;
            mErasePath.lineTo(x, y);
            mMaskCanvas.drawPath(mErasePath, mErasePaint);
            mErasePath.reset();
            mErasePath.moveTo(mStartX, mStartY);
        }
    }

    private void updateErasePercent() {
        int width = getWidth();
        int height = getHeight();
        new AsyncTask<Integer, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                int width = params[0];
                int height = params[1];
                mMaskBitmap.getPixels(mPixels, 0, width, 0, 0, width, height);
                float erasePixelCount = 0;
                float totalPixelCount = width * height;
                for (int pos = 0; pos < totalPixelCount; pos++) {
                    if (mPixels[pos] == 0) {
                        erasePixelCount++;
                    }
                }
                int percent = 0;
                if (erasePixelCount >= 0 && totalPixelCount > 0) {
                    percent = Math.round(erasePixelCount * 100 / totalPixelCount);
                    publishProgress(percent);
                }
                return percent >= mMaxPercent;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result && !mIsCompleted) {
                    mIsCompleted = true;
                    if (mEraseStatusListener != null) {
                        mEraseStatusListener.onCompleted(ScratchView.this);
                    }
                }
            }
        }.execute(width, height);
    }

    private void stopErase() {
        this.mStartX = 0;
        this.mStartY = 0;
        mErasePath.reset();
        updateErasePercent();
    }

    public void setEraseStatusListener(EraseStatusListener listener) {
        this.mEraseStatusListener = listener;
    }

    private void setMaskPaint() {
        mMaskTextPaint = new Paint();
        mMaskTextBound = new Rect();
        mMaskTextPaint.setStyle(Paint.Style.FILL);
        mMaskTextPaint.setAntiAlias(true);
        mMaskTextPaint.setDither(true);
        mMaskTextPaint.setColor(Color.BLACK);
        mMaskTextPaint.setTextSize(30);
    }

    public void reset() {
        mIsCompleted = false;
        int width = getWidth();
        int height = getHeight();
        createMasker(width, height);
        invalidate();
        updateErasePercent();
    }

    public void clear() {
        int width = getWidth();
        int height = getHeight();
        mMaskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mMaskCanvas = new Canvas(mMaskBitmap);
        Rect rect = new Rect(0, 0, width, height);
        mMaskCanvas.drawRect(rect, mErasePaint);
        invalidate();
        updateErasePercent();
    }

    public interface EraseStatusListener {
        void onCompleted(View view);
    }
}