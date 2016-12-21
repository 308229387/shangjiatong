package com.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.application.HyApplication;
import com.utils.LogoutInintUtils;
import com.utils.UserUtils;
import com.wuba.loginsdk.external.LoginClient;
import com.wuba.wbpush.Push;

/**
 * Created by 58 on 2016/12/19.
 */

public class LogoutDialog {

    private CommonGlobalDialog mLogoutDialog;

    public LogoutDialog(String message) {

        if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
            mLogoutDialog.dismiss();
        }
        mLogoutDialog = new CommonGlobalDialog(HyApplication.getApplication());
        mLogoutDialog.setCancelable(false);
        mLogoutDialog.setCanceledOnTouchOutside(false);
        mLogoutDialog.setContent(message);
        mLogoutDialog.setBtnSureVisible(View.GONE);
        mLogoutDialog.setBtnCancelText("确定");
        mLogoutDialog.setOnDialogClickListener(new CommonGlobalDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {

            }

            @Override
            public void onDialogClickCancel() {
              new LogoutInintUtils(HyApplication.getApplication());
            }

        });
        mLogoutDialog.show();
    }



}
