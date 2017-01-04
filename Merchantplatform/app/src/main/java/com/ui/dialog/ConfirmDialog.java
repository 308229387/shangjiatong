package com.ui.dialog;

import android.app.Activity;

import com.merchantplatform.activity.LoginActivity;
import com.utils.PageSwitchUtils;

/**
 * Created by 58 on 2016/12/19.
 */

public class ConfirmDialog {

    private CommonDialog mConfirmDialog;

    public ConfirmDialog(final Activity context, String message) {

        if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
        }
        mConfirmDialog = new CommonDialog(context);
        mConfirmDialog.setCancelable(false);
        mConfirmDialog.setCanceledOnTouchOutside(false);
        mConfirmDialog.setContent(message);
        mConfirmDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {
                mConfirmDialog.dismiss();
                PageSwitchUtils.goToActivity(context, LoginActivity.class);
                context.finish();
            }

            @Override
            public void onDialogClickCancel() {
                mConfirmDialog.dismiss();
            }

        });
        mConfirmDialog.show();
    }
}
