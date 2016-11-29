package com.android.gmacs.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.gmacs.R;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.utils.ToastUtil;

/**
 * Created by zhangxiaoshuang on 2015/12/12.
 */
public class GmacsWebViewActivity extends BaseActivity {
    /**
     * 要加载的 url，必传。
     */
    public static final String EXTRA_URL = "extra_url";
    /**
     * Webview标题栏文本
     */
    public static final String EXTRA_TITLE = "extra_title";

    @Override
    protected void initView() {
        WebView webView = (WebView) findViewById(R.id.wv_gmacs_webview);
        String url = getIntent().getStringExtra(EXTRA_URL);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        setTitle(title);
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("tel:")) {
                        checkPermission(Uri.parse(url));
                    } else if (url.startsWith("wtai:")) {
                        checkPermission(Uri.parse(url.replace("wtai://wp/mc;", "tel:")));
                    } else {
                        view.loadUrl(url);
                    }
                    return true;
                }
            });
        }
    }

    private void checkPermission(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean findMethod = true;
            try {
                ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                findMethod = false;
            }
            if (findMethod && ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE}, GmacsConstant.REQUEST_CODE_CALL_PHONE);
            } else {
                startActivity(new Intent(Intent.ACTION_CALL, uri));
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_webview);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (requestCode == GmacsConstant.REQUEST_CODE_CALL_PHONE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                try {
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(url.replace("wtai://wp/mc;", "tel:"))));
                } catch (SecurityException e) {}
            } else {
                ToastUtil.showToast("您禁止了拨打电话权限");
            }
        }
    }

}
