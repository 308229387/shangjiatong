package com.merchantplatform.model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.loadview.ShapeLoadingDialog;
import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.bean.RechargeOrder;
import com.pay58.sdk.api.Pay58;
import com.pay58.sdk.api.Pay58ResultCallback;
import com.pay58.sdk.common.Common;
import com.pay58.sdk.common.PayResult;
import com.pay58.sdk.order.Order;
import com.Utils.TitleBar;
import com.utils.AppInfoUtils;
import com.utils.Urls;
import com.utils.UserUtils;
import com.wuba.loginsdk.external.LoginClient;

/**
 * Created by 58 on 2016/12/8.
 */

public class FundingManageModel extends BaseModel {
    private FundingManageActivity context;

    private TitleBar tb_funding_title;
    //private ProgressBar pb_progress;
    private View no_internet_view;
    private WebView webView_funding;
    private String url;

    private ShapeLoadingDialog dialog;

    public FundingManageModel(FundingManageActivity context) {
        this.context = context;
    }

    public void initView() {
        tb_funding_title = (TitleBar) context.findViewById(R.id.tb_funding_title);
        no_internet_view = context.findViewById(R.id.view_no_internet);
        //pb_progress = (ProgressBar) context.findViewById(R.id.pb_progress);
        webView_funding = (WebView) context.findViewById(R.id.webView_funding);
    }

    public void initData() {
        //设置透明状态栏
        tb_funding_title.setImmersive(true);
        //设置背景颜色
        tb_funding_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_funding_title.setLeftImageResource(R.mipmap.title_back);
        //设置标题
        tb_funding_title.setTitle("我的资金");
        //设置标题颜色
        tb_funding_title.setTitleColor(Color.BLACK);

    }

    public void setListener() {
        //设置左侧点击事件
        tb_funding_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    public void initWebViewContainer() {
        setWebViewProperty();
        removeJSInterface();
    }

    private void setWebViewProperty() {
        webView_funding.getSettings().setJavaScriptEnabled(true);
        webView_funding.getSettings().setUseWideViewPort(true);
        webView_funding.getSettings().setDefaultTextEncodingName("UTF-8"); // 设置默认的显示编码
        webView_funding.setWebViewClient(new BaseWebClient());
        webView_funding.setWebChromeClient(new WebChromeBaseClient());
    }

    /**
     * 显示判断系统版本 ，如果在4.2以下，就手动移除removeJavascriptInterface
     * 因为在4.3.1~3.0版本，webview默认添加了searchBoxJavaBridge_接口,
     */
    private void removeJSInterface() {
        if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
            try {
                webView_funding.removeJavascriptInterface("searchBoxJavaBridge_");
                webView_funding.removeJavascriptInterface("accessibility");
                webView_funding.removeJavascriptInterface("accessibilityTraversal");
            } catch (Throwable tr) {
                tr.printStackTrace();
            }
        }
    }

    public void initRechargePage() {
        initUrl();
        startDialog();
        loadRechargePage();
    }

    private void initUrl() {
        url = Urls.RECHARGE;
    }

    private void startDialog() {
        dialog = new ShapeLoadingDialog(context);
        dialog.show();
    }

    private void loadRechargePage() {
        if (!AppInfoUtils.isNetworkConnected(context)) {
            no_internet_view.setVisibility(View.VISIBLE);
        }
        webView_funding.loadUrl(url);
    }

    public void destoryWebView() {
        webView_funding.removeAllViews();
        webView_funding.destroy();
        webView_funding = null;
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
            //调用拨号程序
            if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:") || url.startsWith("smsto:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
                return true;
            } else if (url.startsWith(Urls.RECHARGE_INTERCEPT_URL)) {
                //  0为交易宝，1为58余额，2为推广余额
                int urlLength = url.length();
                String rechargeType = url.substring(urlLength - 1, urlLength);//截取url最后一位作为rechargeType
                invokePay(rechargeType);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

        }
    }

    private void invokePay(String rechargeType) {
        String productDesc = "58推广余额充值";
        switch (Integer.parseInt(rechargeType)) {
            case 0:
                productDesc = "58交易宝充值";
                break;
            case 1:
                productDesc = "58余额充值";
                break;
            case 2:
                productDesc = "58推广余额充值";
                break;
        }
        Pay58.getInstance().setPayEnable(Common.WECHAT, false);//屏蔽微信支付
        Pay58.getInstance().setPayEnable(Common.WEBPAY, false);//屏蔽网页支付
        Pay58.getInstance().setRechargeEditable(true);
        //默认充值金额500
        Pay58.getInstance().pay58Recharge(context, generateOrder(null, productDesc, productDesc, 500, rechargeType), callback);
    }

    /**
     * 根据商户id, 商品名字，商品描述，订单金额，生成订单
     *
     * @param merid        商户id
     * @param productName  商品名称
     * @param productDesc  商品描述
     * @param orderMoney   订单金额
     * @param rechargeType 充值类型
     * @return 充值订单
     */
    private Order generateOrder(String merid, String productName, String productDesc, float orderMoney, String rechargeType) {
        String cookie = "PPU=" + LoginClient.doGetPPUOperate(context);//用户登录cookie，直接使用PPU
        String buyAccountId = UserUtils.getUserId();//购买用户的ID
        RechargeOrder rechargeOrder = new RechargeOrder();
        rechargeOrder.setOrderContent(merid, productName, productDesc, orderMoney);
        rechargeOrder.setCookie(cookie);
        rechargeOrder.setBuyAccountId(buyAccountId);
        rechargeOrder.setRechargeType(rechargeType);   //  0为交易宝，1为58余额，2为推广余额
        return generateOrder(rechargeOrder);
    }

    /**
     * 生成订单信息
     *
     * @param rechargeOrder
     * @return
     */
    private Order generateOrder(RechargeOrder rechargeOrder) {
        Order order = new Order();
        order.setParameter(Order.PRODUCT_NAME, rechargeOrder.getProductName());
        order.setParameter(Order.PRODUCT_DESC, rechargeOrder.getProductDesc());
        order.setParameter(Order.ORDER_MONEY, rechargeOrder.getOrderMoney() + "");
        order.setParameter(Order.BUY_ACCOUNT_ID, rechargeOrder.getBuyAccountId());
        order.setParameter(Order.PAY_FROM, rechargeOrder.getPayFrom());
        order.setParameter(Order.PLAT_FROM, rechargeOrder.getPlatfrom());
        order.setParameter(Order.NOTIFY_URL, rechargeOrder.getNotifyUrl());
        order.setParameter(Order.RECHARGE_TYPE, rechargeOrder.getRechargeType());
        order.setParameter(Order.COOKIE, rechargeOrder.getCookie());
        return order;
    }

    private class WebChromeBaseClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                if (null != dialog && dialog.isShowing()) {
                    dialog.dismiss();
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


    private void back() {
        if (webView_funding.canGoBack()) {
            webView_funding.goBack();
        } else {
            context.onBackPressed();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView_funding.canGoBack()) {
            webView_funding.goBack();
            return true;
        } else {
            return false;
        }
    }

    private Pay58ResultCallback callback = new Pay58ResultCallback() {

        @Override
        public void pay58ResultCallback(PayResult result) {
            loadRechargePage();
        }
    };
}
