package com.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.application.HyApplication;

/**
 * Created by 58 on 2016/12/19.
 */

public class LogoutDialog {

    private CommonGlobalDialog mLogoutDialog;

    public LogoutDialog(String message) {

        if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
           return ;
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
                goToLoginActivity(HyApplication.getApplication());
            }

        });
        mLogoutDialog.show();
    }

    private void  goToLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
