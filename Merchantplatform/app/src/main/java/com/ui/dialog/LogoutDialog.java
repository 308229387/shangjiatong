package com.ui.dialog;

import android.app.Activity;
import android.view.View;

import com.merchantplatform.application.HyApplication;
import com.utils.LogoutInintUtils;

/**
 * Created by 58 on 2016/12/19.
 */

public class LogoutDialog {

    private CommonGlobalDialog mLogoutDialog;

    public LogoutDialog(Activity activity, String message) {

        if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
            mLogoutDialog.dismiss();
        }

        if(mLogoutDialog == null ){
            synchronized (LogoutDialog.class) {
                if (mLogoutDialog == null) {
                    mLogoutDialog = new CommonGlobalDialog(activity);
                }
            }

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
                    mLogoutDialog.dismiss();
                    new LogoutInintUtils(HyApplication.getApplication());
                }

            });
            mLogoutDialog.show();

        }

    }

}
