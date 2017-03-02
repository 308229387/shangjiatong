package com.android.gmacs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by zhangxiaoshuang on 2015/11/24.
 */
public class PinnedHeaderListView extends ListView {
    /**
     * Adapter interface. The list adapter must implement this interface.
     */
    public interface PinnedHeaderListAdapter {

        /**
         * Pinned header state: don't show the header.
         */
        int PINNED_HEADER_GONE = 0;

        /**
         * Pinned header state: show the header at the top of the list.
         */
        int PINNED_HEADER_VISIBLE = 1;

        /**
         * Pinned header state: show the header. If the header extends beyond the bottom of the first shown element, push it up and clip.
         */
        int PINNED_HEADER_PUSHED_UP = 2;

        /**
         * Computes the desired state of the pinned header for the given position of the first visible list item. Allowed return values are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or {@link #PINNED_HEADER_PUSHED_UP}.
         */
        int getPinnedHeaderState(int position);

        /**
         * Configures the pinned header view to match the first visible list item.
         *
         * @param header   pinned header view.
         * @param position position of the first visible list item.
         * @param alpha    fading of the header view, between 0 and 255, when at setEnabledAlphaEffect(enabled), if enabled = true, alpha == 255.
         */
        void configurePinnedHeader(View header, int position, int alpha);
    }

    private final int fMaxAlpha = 255;
    private PinnedHeaderListAdapter mAdapter;
    private View mHeaderView;
    private boolean mEnabledDynamicAlphaEffect = true;
    private boolean mHeaderViewVisible;
    private int mHeaderViewWidth;
    private int mHeaderViewHeight;
    private boolean mFirstLayout;
    private boolean mIsTouchDown;
    private boolean forceHeaderViewVisible = true; // 有外界 强制控制悬浮框的显示

    public PinnedHeaderListView(Context context) {
        super(context);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPinnedHeaderView(View view) {
        // Android 2.2及其 以下版本禁用悬浮条，getExpandableListPosition()不准确、onScroll也不精确；
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mHeaderView = view;
            if (mHeaderView != null) {
                setFadingEdgeLength(0);
            }
            requestLayout();
        }
    }

    /**
     * 设置在滚动时是否启用别针头条目控件动态透明特效
     *
     * @param enabled
     */
    public void setEnabledPinnedHeaderDynamicAlphaEffect(boolean enabled) {
        mEnabledDynamicAlphaEffect = enabled;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = (PinnedHeaderListAdapter) adapter;
    }

    public void setSearchAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            super.onLayout(changed, left, top, right, bottom);
        } catch (IllegalStateException e) {// 对java.lang.IllegalStateException:
            // couldn't move cursor to position
            // 0此异常进行保护，防止应用程序崩溃
            /*
			 * 此处抛的异常： at android.widget.CursorAdapter.getView(CursorAdapter.java:242) at android .widget.HeaderViewListAdapter.getView(HeaderViewListAdapter .java:220) at android.widget.AbsListView.obtainView(AbsListView.java:2396) at android.widget.ListView.makeAndAddView(ListView.java:1781) at android.widget.ListView.fillDown(ListView.java:679) at android.widget.ListView.fillSpecific(ListView.java:1337) at android.widget.ListView.layoutChildren(ListView.java:1610) at
			 * android.widget.AbsListView.onLayout(AbsListView.java:2247) at cn.com.fetion.view.pinnedheader.PinnedHeaderListView.onLayout( PinnedHeaderListView.java:124)
			 */
        }

        if (mHeaderView != null) {
            if (changed) { // 只有在状态改变时才layout，这点相当重要，不然可能导致视图不断的刷新
                if (!mFirstLayout) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                    mFirstLayout = true;
                }
                int position = getFirstVisiblePosition();
                configureHeaderView(position);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mHeaderViewVisible && ev.getY() <= mHeaderViewHeight) { // 屏蔽mHeaderView区域的点击事件
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mIsTouchDown = true;
                    return true;
                case MotionEvent.ACTION_UP:
                    if (mIsTouchDown) {
                        mIsTouchDown = false;
                        return true;
                    }
                    break;
                default:
                    mIsTouchDown = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    public void configureHeaderView(int position) {
        if (null != mHeaderView) {
            if (mAdapter != null) {
                int state;
                if (getChildAt(0) == null || getChildAt(0).getTag() == null) {
                    state = PinnedHeaderListAdapter.PINNED_HEADER_GONE;
                } else {
                    state = mAdapter.getPinnedHeaderState(position);
                }
                switch (state) {
                    case PinnedHeaderListAdapter.PINNED_HEADER_GONE: {
                        mHeaderViewVisible = false;
                        break;
                    }

                    case PinnedHeaderListAdapter.PINNED_HEADER_VISIBLE: {
                        mAdapter.configurePinnedHeader(mHeaderView, position, fMaxAlpha);
                        if (mHeaderView.getTop() != 0) {
                            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                        }
                        mHeaderViewVisible = true;
                        break;
                    }

                    case PinnedHeaderListAdapter.PINNED_HEADER_PUSHED_UP: {
                        View firstView = getChildAt(0);
                        if (firstView != null) {
                            int bottom = firstView.getBottom();
                            int headerHeight = mHeaderView.getHeight();
                            int y;
                            int alpha;
                            if (bottom < headerHeight) {
                                y = (bottom - headerHeight);
                                alpha = fMaxAlpha * (headerHeight + y) / headerHeight;
                            } else {
                                y = 0;
                                alpha = fMaxAlpha;
                            }
                            mAdapter.configurePinnedHeader(mHeaderView, position, mEnabledDynamicAlphaEffect ? alpha : fMaxAlpha);
                            if (mHeaderView.getTop() != y) {
                                mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                            }
                            mHeaderViewVisible = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible && forceHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    // private final long st = 0;
    // private final int zp = 0;

    // public void draw(Canvas canvas) {
    // zp++;
    // long nst = System.currentTimeMillis();
    // if(st==0){
    // st = nst;
    // }else{
    // if(nst-st>=1000){
    // LogF.e("==>", "帧频："+zp);
    // st = nst;
    // zp = 0;
    // }
    // }
    // super.draw(canvas);
    // long net = System.currentTimeMillis();
    // LogF.e("==>", "绘制耗时："+(net-nst));
    //
    // }

    /**
     * 滚动事件
     *
     * @param firstVisibleItem
     */
    public void onPinnedHeaderScroll(int firstVisibleItem) {
        if (mHeaderView != null) {
            configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void setSelection(int position) {
        smoothScrollBy(0, 0);// 停止滚动
        super.setSelection(position);
    }

    /**
     * 有外界 强制控制悬浮框的显示
     *
     * @param forceHeaderViewVisible
     */
    public void setForceHeaderViewVisible(boolean forceHeaderViewVisible) {
        this.forceHeaderViewVisible = forceHeaderViewVisible;
    }


}
