package com.merchantplatform.model;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.Utils.TitleBar;
import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.activity.PromoteMessageActivity;
import com.ta.utdid2.android.utils.StringUtils;
import com.ui.dialog.LogoutDialog;
import com.ui.dialog.PayAlertDialog;
import com.utils.AppInfoUtils;
import com.utils.PageSwitchUtils;
import com.utils.Urls;
import com.utils.UserUtils;

/**
 * Created by 58 on 2017/1/6.
 */

public class PromoteMessageActivityModel extends BaseModel{

    private PromoteMessageActivity context;
    private TitleBar tb_promote_title;
    private ProgressBar pb_progress;
    private View no_internet_view;
    private WebView webView_promote;
    private String url;


   public PromoteMessageActivityModel(PromoteMessageActivity context){
       this.context = context;
   }

    public void initView(){
        tb_promote_title = (TitleBar) context.findViewById(R.id.tb_promote_title);
        no_internet_view = context.findViewById(R.id.view_no_internet);
        pb_progress = (ProgressBar) context.findViewById(R.id.pb_progress);
        webView_promote = (WebView) context.findViewById(R.id.webView_promote);
    }

    public void initData(){
        //设置透明状态栏
        tb_promote_title.setImmersive(true);
        //设置背景颜色
        tb_promote_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_promote_title.setLeftImageResource(R.mipmap.title_back);
        //设置标题
        tb_promote_title.setTitle("推广助手");
        //设置标题颜色
        tb_promote_title.setTitleColor(Color.BLACK);
        //设置标题栏下划线
        tb_promote_title.setDividerColor(Color.parseColor("#DFE0E1"));
    }

    public void setListener(){
        //设置左侧点击事件
        tb_promote_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }

    public void initWebViewContainer(){
        setWebViewProperty();
        removeJSInterface();
    }

    private void setWebViewProperty() {
        webView_promote.getSettings().setJavaScriptEnabled(true);
        webView_promote.getSettings().setUseWideViewPort(true);
        webView_promote.getSettings().setDefaultTextEncodingName("UTF-8"); // 设置默认的显示编码
        webView_promote.setWebViewClient(new BaseWebClient());
        webView_promote.setWebChromeClient(new WebChromeBaseClient());
    }

    /**
     * 显示判断系统版本 ，如果在4.2以下，就手动移除removeJavascriptInterface
     * 因为在4.3.1~3.0版本，webview默认添加了searchBoxJavaBridge_接口,
     */
    private void removeJSInterface() {
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            try {
                webView_promote.removeJavascriptInterface("searchBoxJavaBridge_");
                webView_promote.removeJavascriptInterface("accessibility");
                webView_promote.removeJavascriptInterface("accessibilityTraversal");
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
        url = Urls.PROMOTE_MESSAGE;
    }

    private void loadRechargePage() {
        if (!AppInfoUtils.isNetworkConnected(context)) {
            no_internet_view.setVisibility(View.VISIBLE);
        }
        webView_promote.loadUrl(url);
    }

    public void destoryWebView() {
        webView_promote.removeAllViews();
        webView_promote.destroy();
        webView_promote = null;
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
            //点击充值
            if (url.startsWith("http://paycenter.58.com/wappay")) {
                String isPayOpen = UserUtils.getPay();
                if(!StringUtils.isEmpty(isPayOpen)){
                    if("1".equals(isPayOpen)){
                        PageSwitchUtils.goToActivity(context,FundingManageActivity.class);
                    }else{
                        new PayAlertDialog(context, "本APP暂不支持充值业务，请在58同城APP上进行充值");
                    }
                }
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
