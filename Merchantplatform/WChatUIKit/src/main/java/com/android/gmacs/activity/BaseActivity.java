package com.android.gmacs.activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.wchatuikit.R;
import com.android.gmacs.dragback.ParallaxBackActivityHelper;
import com.android.gmacs.view.TitleBar;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUtils;
import com.xxganji.gmacs.Client;

public abstract class BaseActivity extends AppCompatActivity {
    protected String TAG = this.getClass().getSimpleName();
    protected TitleBar mTitleBar;
    protected View mStatusBar;
    protected FrameLayout mContentContainer;
    private boolean mIsNoTitle;
    private ParallaxBackActivityHelper mHelper;

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new ParallaxBackActivityHelper(this);
    }

    /**
     * 兼容AppCompatActivity的bug，子类在onCreate方法里执行super后调用
     *
     * @param layoutResID
     */
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.gmacs_window_base);
        mContentContainer = (FrameLayout) findViewById(R.id.content_container);
        getLayoutInflater().inflate(layoutResID, mContentContainer); // 将子布局填充到父布局中
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            mStatusBar = findViewById(R.id.status_bar_delegate);
            mStatusBar.setVisibility(View.VISIBLE);
            if (GmacsEnvi.statusBarHeight != 0) {
                mStatusBar.getLayoutParams().height = GmacsEnvi.statusBarHeight;
            }
        }
        mTitleBar = (TitleBar) findViewById(R.id.layout_widget_title_bar);
        mTitleBar.setVisibility(mIsNoTitle ? View.GONE : View.VISIBLE);
        setTitle(getTitle());
        mTitleBar.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                manager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        });
        initView();
        initData();
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        if (mTitleBar != null) {
            mTitleBar.setTitle(title);
        }
    }

    protected void requestWindowNoTitle(boolean isNoTitle) {
        mIsNoTitle = isNoTitle;
        if (mTitleBar != null) {
            mTitleBar.setVisibility(isNoTitle ? View.GONE : View.VISIBLE);
        }
        if (mStatusBar != null) {
            mStatusBar.setVisibility(isNoTitle ? View.GONE : View.VISIBLE);
        }
        if (isNoTitle) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mContentContainer.getLayoutParams();
            layoutParams.topMargin = 0;
            mContentContainer.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mContentContainer.setFitsSystemWindows(false);
            }
        } else {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mContentContainer.getLayoutParams();
            layoutParams.topMargin = getResources().getDimensionPixelOffset(R.dimen.titlebar_height);
            mContentContainer.setLayoutParams(layoutParams);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mContentContainer.setFitsSystemWindows(true);
            }
        }
    }

    protected void requestPermissionOnNeed(String permission, int requestCode) {
        boolean findMethod = true;
        try {
            ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            findMethod = false;
        }
        if (findMethod && ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            return;
        }
        if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
            updateData();
        }
    }

    protected void updateData() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Client.getInstance().setNetworkStatus(GmacsUtils.getNetworkStatus(GmacsEnvi.appContext));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
    }

    @Override
    @NonNull
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHelper.onActivityDestroy();
    }
    public void setBackEnable(boolean enable) {
        mHelper.setBackEnable(enable);
    }

    public void scrollToFinishActivity() {
        mHelper.scrollToFinishActivity();
    }
    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            scrollToFinishActivity();
        }
    }

    protected void hideTitleBar() {
        if (mTitleBar != null) {
            mTitleBar.setVisibility(View.GONE);
        }
        if (mStatusBar != null) {
            mStatusBar.setVisibility(View.GONE);
        }
    }
    protected void showTitleBar() {
        if (mTitleBar != null) {
            mTitleBar.setVisibility(View.VISIBLE);
        }
        if (mStatusBar != null) {
            mStatusBar.setVisibility(View.VISIBLE);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void requestNotFitSystemWindow() {
        if (mContentContainer != null) {
            mContentContainer.setFitsSystemWindows(false);
        }
    }

    protected void resetContentContainerMargin() {
        if (mContentContainer != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mContentContainer.getLayoutParams();
            layoutParams.topMargin = 0;
            mContentContainer.setLayoutParams(layoutParams);
        }
    }
}
