package com.merchantplatform.model;

import android.app.Activity;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.activity.MobileValidateActivity;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.bean.LoginResponse;
import com.okhttputils.OkHttpUtils;
import com.ui.dialog.CommonDialog;
import com.utils.AppInfoUtils;
import com.utils.IMLoginUtils;
import com.utils.PageSwitchUtils;
import com.utils.Urls;
import com.utils.UserUtils;
import com.wuba.loginsdk.external.LoginCallback;
import com.wuba.loginsdk.external.LoginClient;
import com.wuba.loginsdk.external.Request;
import com.wuba.loginsdk.external.SimpleLoginCallback;
import com.wuba.loginsdk.model.LoginSDKBean;
import com.wuba.wbpush.Push;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class LoginActivityModel extends BaseModel {
    private LoginActivity context;
    private LoginCallback callback;

    private String VALIDATED = "1";

    private CommonDialog loginErrorDialog;

    public LoginActivityModel(LoginActivity context) {
        this.context = context;
    }

    public void createCallback() {
        callback = new SimpleLoginCallback() {
            @Override
            public void onLogin58Finished(boolean isSuccess, String msg, @Nullable LoginSDKBean loginSDKBean) {
                super.onLogin58Finished(isSuccess, msg, loginSDKBean);
                if (isSuccess && loginSDKBean != null) {
                    loginPassrotSuccess(loginSDKBean);
                }

                if (isPassportLoginFail(loginSDKBean)) {
                    context.finish();
                }
            }
        };
    }

    private void loginPassrotSuccess(@Nullable LoginSDKBean loginSDKBean){
        saveUserId(loginSDKBean);
        bindAlias();
        loginLocal();
        initIM();
    }

    private void loginLocal(){
        OkHttpUtils.post(Urls.LOGIN)
                .execute(new localLoginCallback(context));
    }

    private void saveUserId(@Nullable LoginSDKBean loginSDKBean){
        UserUtils.setUserId(context, loginSDKBean.getUserId());
    }

    private void initIM(){
        new IMLoginUtils(context);
    }

    private void bindAlias(){
        StringBuilder temp = new StringBuilder();
        temp.append(UserUtils.getUserId() + "_");
        temp.append(AppInfoUtils.getIMEI(HyApplication.getApplication()));
        String alias= temp.toString();
        Log.i("song",alias);
        Push.getInstance().binderAlias(alias); //绑定/解绑别名:非空串,绑定指定的alias ,空串(“”),解绑alias。
    }


    private void LoginSuccess(LoginResponse loginResponse) {
        GoToWhere(loginResponse);
        context.finish();
    }

    private void GoToWhere(LoginResponse loginResponse) {
        if (hasValidated(loginResponse)) {
            UserUtils.hasValidate(context.getApplicationContext());
            PageSwitchUtils.goToActivity(context, HomepageActivity.class);
        } else {
            PageSwitchUtils.goToActivity(context, MobileValidateActivity.class);
        }
    }

    private Boolean hasValidated(LoginResponse loginResponse) {
        Boolean validate = loginResponse.getData().getVerified().equals(VALIDATED);
        return validate;
    }

    private boolean isPassportLoginFail(@Nullable LoginSDKBean loginSDKBean) {
        return loginSDKBean != null && loginSDKBean.getCode() == LoginSDKBean.CODE_CANCEL_OPERATION;
    }


    public void removeOtherActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                HyApplication.getInstance().removeOtherActivity(context);
            }
        }, 1000);
    }

    public void registerCallback() {
        LoginClient.register(callback);
    }

    public void unregisterLoginSDK() {
        LoginClient.unregister(callback);
    }

    public void createRequest() {
        Request request = new Request.Builder()
                //必选，服务类型为登录
                .setOperate(Request.LOGIN)
                .setLogoResId(R.drawable.loginsdk_account_newlogin_logo)
                //可选，设置三方登录区域是否可见
                .setSocialEntranceEnable(false)
                //是否显示手机动态码登录入口
                .setPhoneLoginEnable(false)
                //可选，设置左上角关闭按钮是否可见
                .setCloseButtonEnable(true)
                //可选，在登录页面成功后跳转的目标activity，这个参数用于登录成功后跳转，不传则不跳转
                .setWaitSecondsAfterLoginSucceed(true)
                .create();
        LoginClient.launch(context, request);
    }

    private class localLoginCallback extends DialogCallback<LoginResponse> {
        public localLoginCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, LoginResponse loginResponse, okhttp3.Request request, @Nullable Response response) {
            if(loginResponse != null){
                LoginSuccess(loginResponse);
            }
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            if(e!= null && !TextUtils.isEmpty(e.getMessage())){
                if(e.getMessage().equals(PPU_UNVALID)){
                    initLoginErrorDialog(context.getString(R.string.ppu_expired));
                }else if(e.getMessage().equals(SINGLE_DEVICE_LOGIN)){
                    initLoginErrorDialog(context.getString(R.string.force_exit));
                }else{
                   initLoginErrorDialog(e.getMessage());
                }
            }
        }
    }

    private void initLoginErrorDialog(String message){
        if (loginErrorDialog != null && loginErrorDialog.isShowing()) {
            loginErrorDialog.dismiss();
        }
        loginErrorDialog = new CommonDialog(context);
        loginErrorDialog.setCancelable(false);
        loginErrorDialog.setCanceledOnTouchOutside(false);
        loginErrorDialog.setContent(message);
        loginErrorDialog.setBtnSureVisible(View.GONE);
        loginErrorDialog.setBtnCancelText("确定");
        loginErrorDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {

            }

            @Override
            public void onDialogClickCancel() {
                loginErrorDialog.dismiss();
                context.finish();
            }

        });
        loginErrorDialog.show();
    }

}
