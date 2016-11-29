package com.android.gmacs.view.listview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.android.gmacs.R;

/**
 * 自定义ListView
 */
public class XXListView extends ListView implements OnScrollListener {

    private Scroller mHeaderScroller;
    private OnScrollListener mScrollListener;
    private int finalHeight = getResources().getDimensionPixelOffset(R.dimen.max_loading_progress_bar_height);
    private XXListViewListener mListViewListener;

    public XXListViewHeader mHeaderView;
    private RelativeLayout mHeaderViewContent;
    private final static int SCROLL_DURATION = 300;

    public XXListView(Context context) {
        super(context);
        initWithContext(context);
    }

    public XXListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWithContext(context);
    }

    public XXListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mHeaderScroller = new Scroller(context, new DecelerateInterpolator());
        super.setOnScrollListener(this);

        mHeaderView = new XXListViewHeader(context);
        mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xxlistview_header_content);
        addHeaderView(mHeaderView);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    public void setPullRefreshEnable(boolean enable) {
        if (!enable) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    public void stopLoadMore() {
        int height = mHeaderView.getVisibleHeight();
        if (height == 0) {
            mHeaderViewContent.setVisibility(GONE);
            return;
        }
        mHeaderScroller.startScroll(0, height, 0, -height, SCROLL_DURATION);
        invalidate();
    }

    public void startLoadMore() {
        mHeaderViewContent.setVisibility(VISIBLE);
        int height = mHeaderView.getVisibleHeight();
        mHeaderScroller.startScroll(0, height, 0, finalHeight - height, SCROLL_DURATION);
        invalidate();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void computeScroll() {
        if (mHeaderScroller.computeScrollOffset()) {
            mHeaderView.setVisibleHeight(mHeaderScroller.getCurrY());
            if (mHeaderScroller.getCurrY() == finalHeight) {
                mListViewListener.onLoadMore();
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setXXListViewListener(XXListViewListener l) {
        mListViewListener = l;
    }


    public interface XXListViewListener {
        void onLoadMore();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setTranscriptMode(TRANSCRIPT_MODE_DISABLED);
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

}
