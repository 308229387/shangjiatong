package com.merchantplatform.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.merchantplatform.R;
import com.merchantplatform.application.HyApplication;
import com.utils.Constant;
import com.utils.UpdateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * AppDownloadService.startService(App.getAppContext(), bean.getUrl());
 */
public class AppDownloadService extends IntentService {

    private static final int NOTIFY_ID = 0;
    private int mProgress;
    private NotificationManager mNotificationManager;
    private boolean mCanceled;
    private NotificationCompat.Builder mNotification;

    public static void startService(Context context, String url) {
        Intent intent = new Intent(context, AppDownloadService.class);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    public AppDownloadService() {
        super("AppDownloadService");
    }

    @Override
    public void onDestroy() {
        HyApplication.getInstance().setStartDownService(false);
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!HyApplication.getInstance().isStartDownService()) {
            HyApplication.getInstance().setStartDownService(true);
            String url = intent.getStringExtra("url");
            setUpNotification();
            startDownload(url);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void startDownload(String apkUrl) {
        mCanceled = false;
        try {
            URL url = new URL(apkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            int length = conn.getContentLength();
            InputStream is = conn.getInputStream();
            File file = new File(Constant.Directorys.TEMP);
            if (!file.exists()) {
                file.mkdirs();
            }
            String apkFilePath = UpdateUtils.getApkFilePath();
            File apkFile = new File(apkFilePath);
            FileOutputStream fos = new FileOutputStream(apkFile);

            int count = 0;
            byte buf[] = new byte[1024];
            int lastRate = 0;
            do {
                int numread = is.read(buf);
                count += numread;
                mProgress = (int) (((float) count / length) * 100);
                if (mProgress >= lastRate + 1) {
                    updateNotification(mProgress);
                    lastRate = mProgress;
                }
                if (numread <= 0) {
                    mNotificationManager.cancel(NOTIFY_ID);
                    UpdateUtils.getInstance().installApk(AppDownloadService.this, apkFilePath);
                    HyApplication.getInstance().setStartDownService(false);
                    mCanceled = true;
                    break;
                }
                fos.write(buf, 0, numread);
            } while (!mCanceled);

            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            updateNotificationError();
        } catch (IOException e) {
            e.printStackTrace();
            updateNotificationError();
        }
    }

    private void setUpNotification() {
        CharSequence tickerText = HyApplication.getApplication().getString(R.string.update_version_tip);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.iv_logo)
                .setTicker(tickerText)
                .setOngoing(false)
                .setContentTitle(tickerText)
                .setContentText(HyApplication.getApplication().getString(R.string.update_version_downloading));
        mNotification = builder;
        mNotificationManager.notify(NOTIFY_ID, builder.build());
    }

    private void updateNotification(int rate) {
        if (rate < 100) {
            mNotification.setProgress(100, rate, false);
        } else {
            mNotification.setContentTitle(HyApplication.getApplication().getString(R.string.update_download_done))
                    .setContentText("")
                    .setProgress(0, 0, false)
                    .setOngoing(false);
        }
        mNotificationManager.notify(NOTIFY_ID, mNotification.build());
    }

    private void updateNotificationError() {
        mNotification.setContentTitle(HyApplication.getApplication().getString(R.string.update_download_failure))
                .setContentText(HyApplication.getApplication().getString(R.string.update_download_please_retry))
                .setProgress(0, 0, false)
                .setOngoing(false);
        mNotificationManager.notify(NOTIFY_ID, mNotification.build());
    }
}
