package com.android.gmacs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.android.gmacs.R;


/**
 * Created by zhangxiaoshuang on 2015/11/19.
 */
public class FastLetterIndexView extends View {
    private NinePatchDrawable mScrollerBg;
    private Drawable mSearch = new ColorDrawable(Color.TRANSPARENT);
    private Paint mPaint;
    private Rect mRectScrollerBg, mRectSearch;
    private int mColorLetterNormal;
    private int mColorLetterPressed;
    private boolean mIsTouchDown;
    private OnTouchLetterListener mOnTouchLetterListener;
    private String[] mLetters;
    private int mLettersLength;
    private final float mSpaceScale = 1F / 3; // 字母与字母之间的高度为字母高度的 1/5
    private final float mSpaceFirstIndexPaddingTop = 4; // 第一个定位符与阴影背景最上面的间距
    private final float mSpaceLastIndexPaddingBottom = 4; // 最后一个定位符与阴影背景最下面的间距
    private float mSearchY;
    private float mLastIndexY;
    private boolean isShowIcon;
    private final int mNoSearchHeight = 0;

    public FastLetterIndexView(Context context) {
        super(context);
        init(context);
    }

    public FastLetterIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FastLetterIndexView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        setLetter(context, R.array.fast_letter_index_letters);
        mScrollerBg = (NinePatchDrawable) context.getResources().getDrawable(
                R.drawable.gmacs_ic_scroller_bg);//滚动时显示的字母的阴影
        mColorLetterNormal = context.getResources().getColor(R.color.gray_medium);//字母颜色
        mColorLetterPressed = context.getResources().getColor(R.color.gray_medium);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColorLetterNormal);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mRectScrollerBg = new Rect();
        mRectSearch = new Rect();
    }

    public void setLetter(Context context, int res) {
        mLetters = context.getResources().getStringArray(res);
        mLettersLength = mLetters.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isShowIcon) {
            if (mIsTouchDown) {
                mRectScrollerBg.set(getPaddingLeft(), getPaddingTop(),
                        getWidth() - getPaddingRight(), getHeight()
                                - getPaddingBottom());
                mScrollerBg.setBounds(mRectScrollerBg);
                mScrollerBg.draw(canvas);
                invalidate(mRectScrollerBg);
                mSearch.setState(PRESSED_ENABLED_STATE_SET);
                mPaint.setColor(mColorLetterPressed);
            } else {
                mSearch.setState(EMPTY_STATE_SET);
                mPaint.setColor(mColorLetterNormal);
            }

            int searchHeight;
            if (isShowIcon) {
                searchHeight = mSearch.getIntrinsicHeight();
            } else {
                searchHeight = mNoSearchHeight;
            }

            float finalHeight = getHeight() - getPaddingTop()
                    - getPaddingBottom();
            float textSize = (finalHeight - searchHeight
                    - mSpaceFirstIndexPaddingTop - mSpaceLastIndexPaddingBottom)
                    / ((mLettersLength - 1) + ((mLettersLength + 1) * mSpaceScale));
            float spacingHeight = textSize * mSpaceScale;
            if (textSize > (getWidth() - getPaddingRight() - getPaddingLeft())) { // 当计算得出的字体大小大于阴影背景的宽度时，设置字体大小等于阴影背景宽度
                textSize = getWidth() - getPaddingRight() - getPaddingLeft();
                spacingHeight = (finalHeight - searchHeight
                        - mSpaceFirstIndexPaddingTop
                        - mSpaceLastIndexPaddingBottom - (textSize * (mLettersLength - 1)))
                        / (mLettersLength + 1);
            }
            float textX = getWidth() / 2;
            float textY;
            mPaint.setTextSize(textSize);
            mRectSearch.left = (getWidth() - mSearch.getIntrinsicWidth()) / 2;
            mRectSearch.top = (int) (getPaddingTop() + spacingHeight + mSpaceFirstIndexPaddingTop);
            mRectSearch.right = (getWidth() + mSearch.getIntrinsicWidth()) / 2;
            mRectSearch.bottom = (mRectSearch.top + searchHeight);
            mSearch.setBounds(mRectSearch);
            mSearch.draw(canvas);
            mSearchY = mRectSearch.bottom + (spacingHeight / 2); // 搜索小图标的点击区域y值
            for (int i = 1; i < mLettersLength; i++) {
                if (1 == i) {
                    textY = mRectSearch.bottom + spacingHeight + textSize;
                } else {
                    textY = (mRectSearch.bottom + spacingHeight + textSize)
                            + (i - 1) * (textSize + spacingHeight);
                }
                if (mLettersLength - 2 == i) {
                    mLastIndexY = textY + (spacingHeight / 2); // #号字符的点击区域y值
                }
                canvas.drawText(mLetters[i], textX, textY, mPaint);
            }
            mRectScrollerBg.setEmpty();
            mRectSearch.setEmpty();
        } else {
            if (mIsTouchDown) {
                mRectScrollerBg.set(getPaddingLeft(), getPaddingTop(),
                        getWidth() - getPaddingRight(), getHeight()
                                - getPaddingBottom());
                mScrollerBg.setBounds(mRectScrollerBg);
                mScrollerBg.draw(canvas);
                invalidate(mRectScrollerBg);
                mPaint.setColor(mColorLetterPressed);
            } else {
                mPaint.setColor(mColorLetterNormal);
            }

            float finalHeight = getHeight() - getPaddingTop()
                    - getPaddingBottom();
            float textSize = (finalHeight - mSpaceFirstIndexPaddingTop - mSpaceLastIndexPaddingBottom)
                    / (mLettersLength + ((mLettersLength + 1) * mSpaceScale));
            float spacingHeight = textSize * mSpaceScale;
            if (textSize > (getWidth() - getPaddingRight() - getPaddingLeft())) { // 当计算得出的字体大小大于阴影背景的宽度时，设置字体大小等于阴影背景宽度
                textSize = getWidth() - getPaddingRight() - getPaddingLeft();
                spacingHeight = (finalHeight - mSpaceFirstIndexPaddingTop
                        - mSpaceLastIndexPaddingBottom - (textSize * mLettersLength))
                        / (mLettersLength + 1);
            }
            float textX = getWidth() / 2;
            float textY;
            mPaint.setTextSize(textSize);

            mSearchY = (getPaddingTop() + spacingHeight + mSpaceFirstIndexPaddingTop)
                    + textSize + (spacingHeight / 2); // 搜索小图标的点击区域y值
            for (int i = 0; i < mLettersLength; i++) {
                textY = (getPaddingTop() + mSpaceFirstIndexPaddingTop)
                        + (spacingHeight + textSize) * (i + 1);
                if (mLettersLength - 2 == i) {
                    mLastIndexY = textY + (spacingHeight / 2); // #号字符的点击区域y值
                }
                canvas.drawText(mLetters[i], textX, textY, mPaint);
            }
            mRectScrollerBg.setEmpty();
            mRectSearch.setEmpty();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouchDown = true;
                break;
            case MotionEvent.ACTION_UP:
                mIsTouchDown = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                mIsTouchDown = false;
                break;
        }
        if (isShowIcon) {
            if (null != mOnTouchLetterListener) {
                float y = event.getY();
                int pos = 0;
                if (y <= mSearchY) { // 搜索小图标点击区域
                    pos = 0;
                } else if (y >= mLastIndexY) { // “#”符号点击区域
                    pos = mLettersLength - 1;
                } else { // A-Z字母点击区域
                    float dis = (getHeight() - mSearchY - (getHeight() - mLastIndexY))
                            / (mLettersLength - 2);
                    pos = (int) ((y - mSearchY) / dis);
                    pos = pos + 1;
                    if (pos <= 1) {
                        pos = 1;
                    } else if (pos >= mLettersLength - 1) {
                        pos = mLettersLength - 2;
                    }
                }
                mOnTouchLetterListener.onTouchLetter(event, pos, mLetters[pos]);
            }

        } else {
            if (null != mOnTouchLetterListener) {
                float y = event.getY();
                int pos = 0;
                if (y <= mSearchY) { // 搜索小图标点击区域
                    pos = 0;
                } else if (y >= mLastIndexY) { // “#”符号点击区域
                    pos = mLettersLength - 1;
                } else { // A-Z字母点击区域
                    float dis = (getHeight() - mSearchY - (getHeight() - mLastIndexY))
                            / (mLettersLength - 2);
                    pos = (int) ((y - mSearchY) / dis);
                    pos = pos + 1;
                    if (pos <= 1) {
                        pos = 1;
                    } else if (pos >= mLettersLength - 1) {
                        pos = mLettersLength - 2;
                    }
                }
                mOnTouchLetterListener.onTouchLetter(event, pos, mLetters[pos]);
            }
        }
        invalidate();
        return true;
    }

    public void setOnTouchLetterListener(OnTouchLetterListener onTouchLetterListener) {
        mOnTouchLetterListener = onTouchLetterListener;
    }

    public interface OnTouchLetterListener {
        void onTouchLetter(MotionEvent event, int index, String letterIndex);
    }

}
