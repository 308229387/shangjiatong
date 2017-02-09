package com.merchantplatform.model;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.Utils.UserUtils;
import com.merchantplatform.activity.GuideActivity;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.service.GetServiceTime;
import com.ui.dialog.CommonDialog;
import com.utils.IMLoginUtils;
import com.utils.PageSwitchUtils;
import com.wuba.loginsdk.external.LoginClient;

/**
 * Created by SongYongmeng on 2016/11/22.
 */
public class GuideActivityModel extends BaseModel {
    private GuideActivity context;

    private static final long DELAYED_TIMES = 2 * 1000;
    private Handler handler = new Handler();

    private CommonDialog mAlertDialog;

    private static final int CODE_READ_PHONE_STATE = 128;

    public GuideActivityModel(GuideActivity context) {
        this.context = context;
    }

    /**
     * 获取手机状态权限
     */
    public void getPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean findMethod = true;
            try {
                ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                findMethod = false;
            }

            if (findMethod && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_PHONE_STATE)) {
                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
                    return;
                }

                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_PHONE_STATE}, CODE_READ_PHONE_STATE);
                return;

            } else {
                waitAndGo();
            }

        } else {
            waitAndGo();
        }
    }

    private void showAlertDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        mAlertDialog = new CommonDialog(context);
        mAlertDialog.setCancelable(false);
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.setContent("未取得您的手机权限，58商家通无法正常启动。请在设置-应用-58商家通-权限中，允许获取手机设备信息。");
        mAlertDialog.setBtnSureText("设置");
        mAlertDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {
                mAlertDialog.dismiss();
                goToPermissionCenter();
            }

            @Override
            public void onDialogClickCancel() {
                mAlertDialog.dismiss();
                finish();
            }

        });
        mAlertDialog.show();
    }

    private void goToPermissionCenter() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }


    public void waitAndGo() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToWhere();
            }
        }, DELAYED_TIMES);
    }

    private void goToWhere() {
        judgeHowManyComeAfterGo();
        finish();
    }

    private void judgeHowManyComeAfterGo() {
        if (neverCome()) {
            PageSwitchUtils.goToActivity(context, LoginActivity.class);
        } else {
            new IMLoginUtils(context);
            PageSwitchUtils.goToActivity(context, HomepageActivity.class);
        }
    }

    private boolean neverCome() {
        return TextUtils.isEmpty(UserUtils.getUserId(context))
                || !UserUtils.isValidate(context)
                || TextUtils.isEmpty(LoginClient.doGetPPUOperate(HyApplication.getApplication()));
    }


    public void requestPermissionResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == CODE_READ_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                waitAndGo();
            } else {
                showAlertDialog();
            }
        }
    }

    private void finish() {
        context.finish();
    }

    public void destoryDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
    }

    public void getServiceTime() {
        Intent startIntent = new Intent(context, GetServiceTime.class);
        context.startService(startIntent);
    }
}