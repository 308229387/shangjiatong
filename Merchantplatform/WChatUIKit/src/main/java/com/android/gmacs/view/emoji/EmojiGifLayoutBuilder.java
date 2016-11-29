package com.android.gmacs.view.emoji;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wchatuikit.R;
import com.android.gmacs.adapter.ViewPagerAdapter;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUtils;

import java.util.ArrayList;
import java.util.List;

public class EmojiGifLayoutBuilder implements OnItemClickListener {

    /**
     * 显示表情页的viewpager
     */
    private ViewPager vpFace;

    /**
     * 表情页界面集合
     */
    private ArrayList<View> pageViews;

    /**
     * 游标显示布局
     */
    private LinearLayout layoutPoint;

    /**
     * 游标点集合
     */
    private ArrayList<ImageView> pointViews;

    /**
     * 表情集合
     */
    private GifGroup gifGroup;

    /**
     * 表情数据填充器
     */
    private List<GifAdapter> GifAdapters;

    /**
     * 当前表情页
     */
    private int current = 0;

    private View emojiGifLayout;
    private OnGifClickListener onGifClickListener;

    private ArrayList<ArrayList<GifEmoji>> emojis;

    public EmojiGifLayoutBuilder(GifGroup gifGroup) {
        emojiGifLayout = LayoutInflater.from(GmacsEnvi.appContext).inflate(R.layout.gmacs_layout_emoji_gif, null);
        this.gifGroup = gifGroup;
        emojis = gifGroup.emojiLists;
        initView();
        initViewPager();
        initPoint();
        initData();
    }

    public View getEmojiLayout() {
        return emojiGifLayout;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        vpFace = (ViewPager) emojiGifLayout.findViewById(R.id.vp_contains);
        layoutPoint = (LinearLayout) emojiGifLayout.findViewById(R.id.iv_image);
        vpFace.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        vpFace.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        vpFace.requestDisallowInterceptTouchEvent(false);
                        break;

                }
                return false;
            }
        });
    }

    /**
     * 初始化显示表情的viewpager
     */
    private void initViewPager() {
        pageViews = new ArrayList<View>();
        // 左侧添加空页
        View nullView1 = new View(GmacsEnvi.appContext);
        // 设置透明背景
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView1);

        // 中间添加表情页

        GifAdapters = new ArrayList<GifAdapter>();
        for (int i = 0; i < emojis.size(); i++) {
            GridView view = new GridView(GmacsEnvi.appContext);
            GifAdapter adapter = new GifAdapter(GmacsEnvi.appContext, emojis.get(i), this);
            view.setAdapter(adapter);
            GifAdapters.add(adapter);
            view.setOnItemClickListener(this);
            view.setNumColumns(4);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(GmacsUtils.dipToPixel(3));
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(0, GmacsUtils.dipToPixel(6), 0, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            view.setGravity(Gravity.CENTER);
            pageViews.add(view);
        }

        // 右侧添加空页面
        View nullView2 = new View(GmacsEnvi.appContext);
        // 设置透明背景
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        pageViews.add(nullView2);
    }

    /**
     * 初始化游标
     */
    private void initPoint() {
        pointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(GmacsEnvi.appContext);
            imageView.setBackgroundResource(R.drawable.gmacs_d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = GmacsUtils.dipToPixel(5);
            layoutParams.rightMargin = GmacsUtils.dipToPixel(5);
            layoutParams.width = GmacsUtils.dipToPixel(5);
            layoutParams.height = GmacsUtils.dipToPixel(5);
            layoutPoint.addView(imageView, layoutParams);
            if (i == 0 || i == pageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.gmacs_d2);
            }
            pointViews.add(imageView);
        }
    }

    /**
     * 填充数据
     */
    private void initData() {
        vpFace.setAdapter(new ViewPagerAdapter(pageViews));

        vpFace.setCurrentItem(1);
        current = 0;
        vpFace.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                current = arg0 - 1;
                // 描绘分页点
                drawPoint(arg0);
                // 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
                if (arg0 == pointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        vpFace.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
                        pointViews.get(1).setBackgroundResource(R.drawable.gmacs_d2);
                    } else {
                        vpFace.setCurrentItem(arg0 - 1);// 倒数第二屏
                        pointViews.get(arg0 - 1).setBackgroundResource(R.drawable.gmacs_d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    /**
     * 绘制游标背景
     */
    public void drawPoint(int index) {
        for (int i = 1; i < pointViews.size(); i++) {
            if (index == i) {
                pointViews.get(i).setBackgroundResource(R.drawable.gmacs_d2);
            } else {
                pointViews.get(i).setBackgroundResource(R.drawable.gmacs_d1);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        GifEmoji emoji = (GifEmoji) GifAdapters.get(current).getItem(arg2);
        if (onGifClickListener != null) {
            onGifClickListener.onGifClick(emoji);
        }
    }


    public interface OnGifClickListener {
        void onGifClick(GifEmoji gifEmoji);
    }


    public OnGifClickListener getOnGifClickListener() {
        return onGifClickListener;
    }

    public void setOnGifClickListener(OnGifClickListener onGifClickListener) {
        this.onGifClickListener = onGifClickListener;
    }

}
