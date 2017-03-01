package com.android.gmacs.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.merchantplatform.R;

import java.util.ArrayList;
import java.util.List;

public class TagFlowLayout extends ViewGroup {

    public static final String GRAVITY_LEFT = "left";
    public static final String GRAVITY_CENTER = "center";
    public static final String GRAVITY_RIGHT = "right";

    protected List<List<View>> mAllViews = new ArrayList<>();
    protected List<Integer> mLineHeightList = new ArrayList<>();

    private String mGravity;
    private int lineSpace;
    private int itemSpace;
    private int maxLineCount;

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TagFlowLayout);
        mGravity = typedArray.getString(R.styleable.TagFlowLayout_gmacs_gravity);
        if (mGravity == null) {
            mGravity = GRAVITY_LEFT;
        }

        maxLineCount = typedArray.getInteger(R.styleable.TagFlowLayout_gmacs_maxLineCount, Integer.MAX_VALUE);
        lineSpace = typedArray.getDimensionPixelOffset(R.styleable.TagFlowLayout_gmacs_lineSpace, 0);
        itemSpace = typedArray.getDimensionPixelOffset(R.styleable.TagFlowLayout_gmacs_itemSpace, 0);

        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // wrap_content
        int width = 0;
        int height = 0;

        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        int lineCount = 1;

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                if (i == childCount - 1) {
                    width = Math.max(lineWidth, width);
                    height += lineHeight;
                }
                continue;
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            if (lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()) {
                lineCount ++;
                width = Math.max(width, lineWidth - itemSpace);
                height += lineHeight + lineSpace;
                if (lineCount > maxLineCount){
                    height -= lineSpace;
                    break;
                }

                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                lineWidth += childWidth + itemSpace;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            if (i == childCount - 1) {
                width = Math.max(lineWidth, width);
                height += lineHeight;
            }
        }

        setMeasuredDimension(
                modeWidth == MeasureSpec.EXACTLY ? sizeWidth : width + getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY ? sizeHeight : height + getPaddingTop() + getPaddingBottom()
        );
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeightList.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<>();

        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()) {
                mLineHeightList.add(lineHeight);
                mAllViews.add(lineViews);

                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                lineViews = new ArrayList<>();
            }
            lineWidth += childWidth + lp.leftMargin + lp.rightMargin + itemSpace;
            lineHeight = Math.max(lineHeight, childHeight + lp.topMargin + lp.bottomMargin);
            lineViews.add(child);
        }
        mLineHeightList.add(lineHeight);
        mAllViews.add(lineViews);

        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeightList.get(i);

            left = getStartLeft(lineViews);
            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child
                        .getLayoutParams();

                int leftChild = left + lp.leftMargin;
                int topChild = top + lp.topMargin;
                int rightChild = leftChild + child.getMeasuredWidth();
                int bottomChild = topChild + child.getMeasuredHeight();

                child.layout(leftChild, topChild, rightChild, bottomChild);

                left += lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin + itemSpace;
            }
            top += lineHeight + lineSpace;
        }
    }

    private int getStartLeft(List<View> lineViews) {
        int left = getPaddingLeft();

        int needWidth = 0;
        for (int j = 0; j < lineViews.size(); j++) {
            View child = lineViews.get(j);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            needWidth += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin + itemSpace;
        }

        needWidth += getPaddingLeft() + getPaddingRight() - itemSpace;
        if (mGravity.equals(GRAVITY_CENTER)) {
            if (getMeasuredWidth() > needWidth) {
                left += (getMeasuredWidth() - needWidth) / 2;
            }
        } else if (mGravity.equals(GRAVITY_RIGHT)) {
            if (getMeasuredWidth() > needWidth) {
                left += getMeasuredWidth() - needWidth;
            }
        }
        return left;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    public String getmGravity() {
        return mGravity;
    }

    public void setmGravity(String mGravity) {
        this.mGravity = mGravity;
        requestLayout();
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
        requestLayout();
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
        requestLayout();
    }

    public int getMaxLineCount() {
        return maxLineCount;
    }

    public void setMaxLineCount(int maxLineCount) {
        this.maxLineCount = maxLineCount;
        requestLayout();
    }
}
