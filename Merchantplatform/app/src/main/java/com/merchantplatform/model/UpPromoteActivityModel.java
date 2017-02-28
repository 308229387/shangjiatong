package com.merchantplatform.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.Utils.TitleBar;
import com.Utils.Urls;
import com.Utils.eventbus.UpPromoteFirstSuccessEvent;
import com.dataStore.PromotePrefersUtil;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.UpPromoteActivity;
import com.utils.AppInfoUtils;
import com.utils.Constant;
import com.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by 58 on 2017/1/9.
 */

public class UpPromoteActivityModel extends BaseModel{

    private UpPromoteActivity context;
    private TitleBar tb_up_promote_title;
    private ProgressBar pb_progress;
    private View no_internet_view;
    private WebView webView_up_promote;
    private String url;
    private String infoId;  //帖子id

    public UpPromoteActivityModel(UpPromoteActivity context){
        this.context = context;
    }

    public void initView(){
        tb_up_promote_title = (TitleBar) context.findViewById(R.id.tb_up_promote_title);
        no_internet_view = context.findViewById(R.id.view_no_internet);
        pb_progress = (ProgressBar) context.findViewById(R.id.pb_progress);
        webView_up_promote = (WebView) context.findViewById(R.id.webView_up_promote);
    }

    public void initData(){
        initTitleData();
        getData();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_up_promote_title.setImmersive(true);
        //设置背景颜色
        tb_up_promote_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_up_promote_title.setLeftImageResource(R.mipmap.title_back);
        //设置标题
        tb_up_promote_title.setTitle("置顶推广");
        //设置标题颜色
        tb_up_promote_title.setTitleColor(Color.BLACK);
        //设置标题栏下划线
        tb_up_promote_title.setDividerColor(Color.parseColor("#DFE0E1"));
    }

    private void getData(){
        infoId = context.getIntent().getStringExtra(Constant.INFOID);
    }

    public void setListener(){
        //设置左侧点击事件
        tb_up_promote_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_TZ_ZDSDK_FH);
                context.onBackPressed();
            }
        });
    }

    public void initWebViewContainer(){
        setWebViewProperty();
        removeJSInterface();
    }

    private void setWebViewProperty() {
        webView_up_promote.getSettings().setJavaScriptEnabled(true);
        webView_up_promote.getSettings().setUseWideViewPort(true);
        webView_up_promote.getSettings().setDefaultTextEncodingName("UTF-8"); // 设置默认的显示编码
        webView_up_promote.setWebViewClient(new BaseWebClient());
        webView_up_promote.setWebChromeClient(new WebChromeBaseClient());
    }

    /**
     * 显示判断系统版本 ，如果在4.2以下，就手动移除removeJavascriptInterface
     * 因为在4.3.1~3.0版本，webview默认添加了searchBoxJavaBridge_接口,
     */
    private void removeJSInterface() {
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            try {
                webView_up_promote.removeJavascriptInterface("searchBoxJavaBridge_");
                webView_up_promote.removeJavascriptInterface("accessibility");
                webView_up_promote.removeJavascriptInterface("accessibilityTraversal");
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
    }

    public void initPromotePage(){
        initUrl();
        loadRechargePage();
    }

    private void initUrl(){
        url = Urls.UP_PROMOTE + infoId;
    }

    private void loadRechargePage() {
        if (!AppInfoUtils.isNetworkConnected(context)) {
            no_internet_view.setVisibility(View.VISIBLE);
        }
        if(!TextUtils.isEmpty(infoId)){
            webView_up_promote.loadUrl(url);
        }

    }

    public void destoryWebView() {
        webView_up_promote.removeAllViews();
        webView_up_promote.destroy();
        webView_up_promote = null;
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
            if (context == null) {
                return false;
            }
            if(url.startsWith(Urls.PROMOTE_MESSAGE) ||url.startsWith(Urls.PROMOTE_OTHER_MESSAGE)){
                String upTime =  PromotePrefersUtil.getInstance().getUpPromote();
                if(DateUtils.isEmptyAndNotToday(upTime)){
                    UpPromoteFirstSuccessEvent upPromoteFirstSuccessEvent = new UpPromoteFirstSuccessEvent();
                    upPromoteFirstSuccessEvent.setData(upTime);
                    EventBus.getDefault().post(upPromoteFirstSuccessEvent);
                }
                String currentTime = DateUtils.getCurrentDateTime();
                PromotePrefersUtil.getInstance().saveUpPromote(currentTime);
                context.onBackPressed();
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        }
    }

    private class WebChromeBaseClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            pb_progress.setProgress(newProgress);
            if (newProgress == 100) {
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
