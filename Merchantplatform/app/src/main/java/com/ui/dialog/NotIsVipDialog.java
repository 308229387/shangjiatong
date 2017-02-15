package com.ui.dialog;

import android.app.Activity;
import android.view.View;

/**
 * Created by songyongmeng on 2017/2/15.
 */

public class NotIsVipDialog {

    private CommonGlobalDialog mLogoutDialog;

    public NotIsVipDialog(Activity activity, String message) {

        if (mLogoutDialog != null && mLogoutDialog.isShowing()) {
            mLogoutDialog.dismiss();
        }

        if (mLogoutDialog == null) {
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
                }

            });
            mLogoutDialog.show();

        }

    }
}
