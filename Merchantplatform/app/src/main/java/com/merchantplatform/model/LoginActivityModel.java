package com.merchantplatform.model;

import android.support.annotation.Nullable;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.activity.LoginActivity;
import com.wuba.loginsdk.external.LoginCallback;
import com.wuba.loginsdk.external.LoginClient;
import com.wuba.loginsdk.external.Request;
import com.wuba.loginsdk.model.LoginSDKBean;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class LoginActivityModel extends BaseModel {
    private LoginActivity context;
    private LoginCallback callback;

    public LoginActivityModel(LoginActivity context) {
        this.context = context;
    }

    public void createCallback() {
        callback = new LoginCallback() {

            @Override
            public void onLogin58Finished(boolean b, String s, @Nullable LoginSDKBean loginSDKBean) {
                    Toast.makeText(context, "onLogin58Finished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLogoutFinished(boolean b, String s) {
                Toast.makeText(context, "onLogoutFinished", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCheckPPUFinished(boolean b, String s, @Nullable LoginSDKBean loginSDKBean) {
                Toast.makeText(context, "onCheckPPUFinished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onBindPhoneFinished(boolean b, String s) {
                Toast.makeText(context, "onBindPhoneFinished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUnBindPhoneFinished(boolean b, String s) {
                Toast.makeText(context, "onUnBindPhoneFinished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFetchUserInfoFinished(boolean b, String s, @Nullable LoginSDKBean loginSDKBean) {
                Toast.makeText(context, "onFetchUserInfoFinished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResetPasswordFinished(boolean b, String s) {
                Toast.makeText(context, "onResetPasswordFinished", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSocialAccountBound(boolean b, String s) {
                Toast.makeText(context, "onSocialAccountBound", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onQQAuthCallback(boolean b, String s) {
                Toast.makeText(context, "onQQAuthCallback", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSMSCodeSendFinished(boolean b, String s, int i, String s1) {
                Toast.makeText(context, "onSMSCodeSendFinished", Toast.LENGTH_LONG).show();
            }
        };
    }

    public void setCallback() {
        LoginClient.register(callback);
    }

    public void createRequest() {
        Request request = new Request.Builder()
                //必选，服务类型为登录
                .setOperate(Request.LOGIN)
                .setLogoResId(R.drawable.loginsdk_account_newlogin_logo)
                //可选，设置三方登录区域是否可见
                .setSocialEntranceEnable(false)
                //可选，设置左上角关闭按钮是否可见
                .setCloseButtonEnable(true)
                //可选，在登录页面成功后跳转的目标activity，这个参数用于登录成功后跳转，不传则不跳转
                .setWaitSecondsAfterLoginSucceed(true)
                .create();
        LoginClient.launch(context, request);
    }

}
