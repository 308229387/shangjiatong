package com.utils;

import android.content.Context;
import android.util.Log;

import com.merchantplatform.R;
import com.wuba.loginsdk.external.ILogger;
import com.wuba.loginsdk.external.LoginSdk;

import static com.okhttputils.interceptor.LoggerInterceptor.TAG;

/**
 * Created by SongYongmeng on 2016/11/22.
 */

public class LoginRegisterUtils {

    public LoginRegisterUtils(Context context) {
        LoginSdk.LoginConfig loginConfig = new LoginSdk.LoginConfig()
                .setLogLevel(ILogger.FULL_LOG)
                .setAppId("1008")
                .setChannel("58")
                .setProductId("huangyeshanghupingtai")
                .setLogoResId(R.drawable.loginsdk_account_newlogin_logo);
        LoginSdk.register(context, loginConfig, new LoginSdk.RegisterCallback() {
            @Override
            public void onInitialized() {
                Log.d(TAG, "WubaLoginSDK registered");
            }
        });
    }
}
