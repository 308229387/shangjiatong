package com.merchantplatform.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.merchantplatform.R;
import com.merchantplatform.activity.SoftwareProtocolActivity;
import com.Utils.TitleBar;
import com.utils.AppInfoUtils;

/**
 * Created by 58 on 2016/12/10.
 */

public class SoftwareProtocolActivityModel extends BaseModel {
    private SoftwareProtocolActivity context;

    private TitleBar tb_protocol_title;
    private ProgressBar pb_progress;
    private View no_internet_view;
    private WebView webView_protocol;

    private String url;

    public SoftwareProtocolActivityModel(SoftwareProtocolActivity context){
        this.context = context;
    }

    public void initView(){
        tb_protocol_title = (TitleBar) context.findViewById(R.id.tb_protocol_title);
        no_internet_view = context.findViewById(R.id.view_no_internet);
        pb_progress = (ProgressBar) context.findViewById(R.id.pb_progress);
        webView_protocol = (WebView) context.findViewById(R.id.webView_protocol);
    }

    public void initData(){
        initTitleData();
        initWebViewContainer();
    }

    private void initTitleData(){
        //设置透明状态栏
        tb_protocol_title.setImmersive(true);
        //设置背景颜色
        tb_protocol_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_protocol_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_protocol_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        tb_protocol_title.setTitle("软件协议");
        //设置标题颜色
        tb_protocol_title.setTitleColor(Color.BLACK);
        //设置标题栏下划线
        tb_protocol_title.setDividerColor(Color.parseColor("#DFE0E1"));
    }

    private void initWebViewContainer(){
        setWebViewProperty();
        removeJSInterface();
    }

    private void setWebViewProperty(){
        webView_protocol.getSettings().setUseWideViewPort(false);
        webView_protocol.getSettings().setDefaultTextEncodingName("UTF-8"); // 设置默认的显示编码
        webView_protocol.setWebViewClient(new BaseWebClient());
        webView_protocol.setWebChromeClient(new WebChromeBaseClient());
    }

    /**
     * 显示判断系统版本 ，如果在4.2以下，就手动移除removeJavascriptInterface
     * 因为在4.3.1~3.0版本，webview默认添加了searchBoxJavaBridge_接口,
     */
    private void removeJSInterface(){
        if(Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            try {
                webView_protocol.removeJavascriptInterface("searchBoxJavaBridge_");
                webView_protocol.removeJavascriptInterface("accessibility");
                webView_protocol.removeJavascriptInterface("accessibilityTraversal");
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
    }

    public void initIntorductionPage(){
        initUrl();
        loadIntroductionPage();
    }

    private void initUrl(){
        url = "file:///android_asset/58商家通APP用户协议.html";
    }

    private void loadIntroductionPage(){
        if(!AppInfoUtils.isNetworkConnected(context)){
            no_internet_view.setVisibility(View.VISIBLE);
        }
        webView_protocol.loadUrl(url);
    }

    public void destoryWebView(){
        webView_protocol.removeAllViews();
        webView_protocol.destroy();
        webView_protocol = null;
    }

    private class BaseWebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        }
    }

    private class WebChromeBaseClient  extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb_progress.setProgress(newProgress);
            if(newProgress==100){
                pb_progress.setVisibility(View.GONE);
            }else{
                if(pb_progress.getVisibility() != View.VISIBLE){
                    pb_progress.setVisibility(View.VISIBLE);
                }

            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }
    }
}
