package com.ui.dialog;

import android.app.Activity;
import android.view.View;

import com.merchantplatform.activity.LoginActivity;
import com.utils.PageSwitchUtils;

/**
 * Created by 58 on 2016/12/19.
 */

public class PayAlertDialog {

    private CommonDialog mPayAlertDialog;

    public PayAlertDialog(final Activity context, String message) {

        if (mPayAlertDialog != null && mPayAlertDialog.isShowing()) {
            mPayAlertDialog.dismiss();
        }
        mPayAlertDialog = new CommonDialog(context);
        mPayAlertDialog.setCancelable(false);
        mPayAlertDialog.setCanceledOnTouchOutside(false);
        mPayAlertDialog.setContent(message);
        mPayAlertDialog.setBtnSureVisible(View.GONE);
        mPayAlertDialog.setBtnCancelText("确定");
        mPayAlertDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {
            }

            @Override
            public void onDialogClickCancel() {
                mPayAlertDialog.dismiss();
            }

        });
        mPayAlertDialog.show();
    }
}
