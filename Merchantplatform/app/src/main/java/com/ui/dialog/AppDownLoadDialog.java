package com.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.merchantplatform.R;
import com.ui.progressbar.NumberProgressBar;
import com.ui.progressbar.OnProgressBarListener;
import com.utils.Constant;
import com.utils.TaskUtil;
import com.utils.ToastUtils;
import com.utils.UpdateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 58 on 2017/1/13.
 */

public class AppDownLoadDialog extends Dialog implements OnProgressBarListener{

    private Context mContext;

    private TextView tv_title;
    private View ll_progressbar;
    private NumberProgressBar numberbar;

    private View.OnClickListener mOkListener;
    private String apkUrl;
    private boolean mFinished;
    private boolean mCanceled;
    private int mProgress;
    private String mApkFilePath;


    public AppDownLoadDialog(Context context) {
        super(context, R.style.DialogWithAnim);
        mContext = context;
    }

    public void setAppVersionData(final String url) {
        apkUrl = url;
    }

    public void setOkOnClickListener(View.OnClickListener listener) {
        mOkListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowAttrs();
        initViews();
    }

    private void initWindowAttrs() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_app_download, null);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int dialogWidth = (int) mContext.getResources().getFraction(
                R.fraction.common_dialog_width, width, width);
        setContentView(view, new ViewGroup.LayoutParams(dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("新版本下载进度");
        ll_progressbar = view.findViewById(R.id.ll_processbar);

        numberbar = (NumberProgressBar) findViewById(R.id.numberbar);
        numberbar.setOnProgressBarListener(this);
        numberbar.setMax(100);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gotoDownloadApp();
    }

    private void gotoDownloadApp(){
        if (TextUtils.isEmpty(apkUrl)) {
            try {
                ToastUtils.showToast("App版本信息为空，无法下载！");
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismiss();
            return;
        }
        mFinished = false;
        mCanceled = false;
        ll_progressbar.setVisibility(View.VISIBLE);
        TaskUtil.executeAsyncTask(new AsyncTask<Void, Integer, RequestParams>() {

            @Override
            protected RequestParams doInBackground(Void... params) {
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
                    mApkFilePath = UpdateUtils.getApkFilePath();
                    File ApkFile = new File(mApkFilePath);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];
                    int lastRate = 0;
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        mProgress = (int) (((float) count / length) * 100);
                        if (mProgress >= lastRate + 1) {
                            publishProgress((mProgress - lastRate));
                            lastRate = mProgress;
                        }
                        if (numread <= 0) {
                            publishProgress((mProgress - lastRate));
                            mFinished = true;
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
                } catch (Exception e) {
                    e.printStackTrace();
                    updateNotificationError();
                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(RequestParams requestParams) {
                super.onPostExecute(requestParams);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                if (numberbar != null) {
                    if (values != null && values.length > 0)
                        numberbar.incrementProgressBy(values[0].intValue());
                }

            }
        });

    }

    @Override
    public void onProgressChange(int current, int max) {
        if (current == max) {
            // 完成
            dismiss();
            UpdateUtils.getInstance().installApk(mContext, mApkFilePath);
        } else if (mFinished) {
            numberbar.setProgress(100);
        }
    }

    @Override
    public void dismiss() {
        mCanceled = true;
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNotificationError() {
        if (ll_progressbar != null && ll_progressbar.getVisibility() == View.VISIBLE) {
            try {
                ToastUtils.showToast("下载失败");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        dismiss();
    }
}
