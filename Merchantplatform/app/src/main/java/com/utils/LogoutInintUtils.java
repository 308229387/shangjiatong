package com.utils;

import android.content.Context;
import android.content.Intent;

import com.dataStore.SettingPushPreferUtil;
import com.merchantplatform.activity.LoginActivity;
import com.wuba.loginsdk.external.LoginClient;
import com.wuba.wbpush.Push;

/**
 * Created by 58 on 2016/12/21.
 */

public class LogoutInintUtils {

    public LogoutInintUtils(Context context) {
        setWPushBindsAlias();
        setLogoutStateToPPU(context);
        clearUserInfo(context);
        resetUserPreference(context);
        goToLoginActivity(context);
    }

    private void setWPushBindsAlias(){
        Push.getInstance().binderAlias("");
    }

    private void clearUserInfo(Context context){
        UserUtils.clearUserInfo(context);
    }

    private void resetUserPreference(Context context){
        SettingPushPreferUtil.getInstance(context).resetPushSettingState();
    }

    private void setLogoutStateToPPU(Context context) {
        LoginClient.doLogoutOperate(context);
    }

    private void  goToLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
