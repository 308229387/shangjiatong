package com.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.merchantplatform.R;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.service.AppDownloadService;
import com.ui.dialog.AppDownLoadDialog;
import com.ui.dialog.UpdateDialog;

import java.io.File;

/**
 * Created by 58 on 2016/12/15.
 */

public class UpdateUtils {
    private static UpdateUtils mInstance;
    private static final String SAVE_FILE_NAME = "update.apk";

    private UpdateDialog mUpdateDialog;
    private AppDownLoadDialog mAppDownLoadDialog;
    public static UpdateUtils getInstance(){
        if(mInstance == null){
            mInstance = new UpdateUtils();
        }
        return mInstance;
    }

    public static String getApkFilePath() {
        String apkFile = Constant.Directorys.TEMP + SAVE_FILE_NAME;
        return apkFile;
    }

    public void installApk(Context context, String apkfilePath) {
        if (StringUtil.isEmpty(apkfilePath)) {
            ToastUtils.showToast(HyApplication.getApplication().getString(R.string.update_download_apk_file_path_empty));
            return;
        }
        File apkfile = new File(apkfilePath);
        if (!apkfile.exists()) {
            ToastUtils.showToast(HyApplication.getApplication().getString(R.string.update_download_apk_file_no_find));
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
    }

    public UpdateDialog showUpateDialog(final Context context,final String appUrl,final String isForce){
        mUpdateDialog = new UpdateDialog(context);
        mUpdateDialog.setForceUpdateFlag(isForce);
        mUpdateDialog.setOkOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("1".equals(isForce)){
                    mAppDownLoadDialog = new AppDownLoadDialog(context);
                    mAppDownLoadDialog.setCanceledOnTouchOutside(false);
                    mAppDownLoadDialog.setAppVersionData(appUrl);
                    mAppDownLoadDialog.show();
                }else{
                    AppDownloadService.startService(context, appUrl);
                }
            }
        });
        mUpdateDialog.show();
        return mUpdateDialog;
    }
}
