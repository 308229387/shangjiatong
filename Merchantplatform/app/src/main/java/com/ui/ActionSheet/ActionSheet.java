package com.ui.actionsheet;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.merchantplatform.R;

/**
 * Created by 58 on 2016/12/13.
 */

public abstract class ActionSheet {
    protected Context mContext;
    protected Dialog mDialog;
    protected Display mDisplay;
    private DialogInterface.OnCancelListener mOnCancelListener;
    private DialogInterface.OnDismissListener mOnDismissListener;

    public ActionSheet(Context context) {
        this(context, R.style.ActionSheetDialogStyle);
    }

    public ActionSheet(Context context, int dialogStyle) {
        mContext = context;
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mDisplay = windowManager.getDefaultDisplay();
        // 定义Dialog布局和参数
        mDialog = new Dialog(mContext, dialogStyle);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if(mOnCancelListener != null) {
                    mOnCancelListener.onCancel(dialog);
                }

            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if(mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(dialog);
                }

            }
        });
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
    }

    public abstract ActionSheet builder();

    public void show() {
        if(mDialog != null) {
            mDialog.show();
        }

    }

    public void dismiss() {
        if(mDialog != null) {
            mDialog.dismiss();
        }

    }

    public ActionSheet setCanceledOnTouchOutside(boolean cancel) {
        if(mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }

        return this;
    }

    public ActionSheet setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
        return this;
    }

    public ActionSheet setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mOnCancelListener = listener;
        return this;
    }

    public ActionSheet setDialogHeight(int height) {
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = height;
        dialogWindow.setAttributes(lp);
        return this;
    }
}
