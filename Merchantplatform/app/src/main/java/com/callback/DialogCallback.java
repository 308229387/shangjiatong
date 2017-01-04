package com.callback;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.loadview.ShapeLoadingDialog;
import com.merchantplatform.R;
import com.okhttputils.request.BaseRequest;
import com.ui.dialog.LogoutDialog;
import com.utils.ToastUtils;

import java.lang.reflect.Type;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 描    述：对于网络请求是否需要弹出进度对话框
 */
public abstract class DialogCallback<T> extends JsonCallback<T> {
    private ShapeLoadingDialog shapeLoadingDialog;
    private Activity activity;

    protected boolean isToast = false;
    private Boolean isShow = false;

    private void initDialog(Activity activity) {
        shapeLoadingDialog = new ShapeLoadingDialog(activity);
    }

    public DialogCallback(Activity activity) {
        super();
        this.activity = activity;
        initDialog(activity);
    }

    public DialogCallback(Activity activity, Class<T> clazz) {
        super(clazz);
        this.activity = activity;
        initDialog(activity);
    }

    public DialogCallback(Activity activity, Type type) {
        super(type);
        this.activity = activity;
        initDialog(activity);
    }

    public DialogCallback(Activity activity, boolean isShowDialog) {
        super();
        this.activity = activity;
        if (isShowDialog)
            initDialog(activity);
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //网络请求前显示对话框
        if (shapeLoadingDialog != null && !shapeLoadingDialog.isShowing()) {
            shapeLoadingDialog.show();
        }
    }

    @Override
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onError(isFromCache, call, response, e);
        if (e != null) {
            if (e.getMessage().equals(PPU_UNVALID) || e.getMessage().equals(SINGLE_DEVICE_LOGIN)) {
                isToast = true;
            }
            if (!isToast) {
                ToastUtils.showToast(e.getMessage());
            }
        }
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable T t, Call call, @Nullable Response response, @Nullable Exception e) {
        super.onAfter(isFromCache, t, call, response, e);
        //网络请求结束后关闭对话框
        if (shapeLoadingDialog != null && shapeLoadingDialog.isShowing()) {
            shapeLoadingDialog.dismiss();
        }
        if (activity != null && !activity.isFinishing() && e != null) {
            if (e.getMessage().equals(PPU_UNVALID) && !isShow) {
                isShow = true;
                new LogoutDialog(activity, activity.getString(R.string.ppu_expired));
                exitTimeRunTask();
            }
            if (e.getMessage().equals(SINGLE_DEVICE_LOGIN) && !isShow) {
                isShow = true;
                new LogoutDialog(activity, activity.getString(R.string.force_exit));
                exitTimeRunTask();
            }
        }
    }

    private void exitTimeRunTask() {
        Timer isDialogShow = new Timer();
        isDialogShow.schedule(new TimerTask() {
            @Override
            public void run() {
                isShow = false;
            }
        }, 3000);
    }
}