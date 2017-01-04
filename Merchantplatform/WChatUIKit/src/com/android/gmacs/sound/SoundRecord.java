package com.android.gmacs.sound;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gmacs.R;
import com.common.gmacs.utils.FileUtil;
import com.common.gmacs.utils.ToastUtil;

import java.io.File;

public class SoundRecord {

    // 最少的录制时间
    private static final int MIN_RECORD_DURATION = 1;

    private MediaRecorder recorder;

    // 录制声音的目录
    private String soundFilePath = FileUtil.getSDCardFile("record").getAbsolutePath();
    // 录制的音频文件名
    private String resultFilePath;

    private long startTime;
    private int duration;

    private RecordSoundAnimation recordSoundDialog;
    private RecordListener currentListener;

    // 录制过程是否发生错误，发生错误，则不把录制的错误文件传出去
    private boolean isSuccessRecord;

    // 是否正在录音
    private boolean isRecording;
    // 用户是否正尝试着取消录音
    boolean isTryCancelRecord;

    private final int ONE_MINUTE = 60;
    private int recordTime = ONE_MINUTE;

    private UpdateTime update;
    // 是否是录音时间到了，录音时长是60s
    private boolean isRecordTimeOut;

    public interface RecordListener {
        void onSuccessRecord(String filePath, int duration);
        void onFailedRecord();
    }

    public boolean isRecording() {
        return isRecording;
    }

    /**
     * 用户是否取消了录音
     *
     * @return
     */
    public boolean isUserCancelRecord() {
        return isTryCancelRecord;
    }

    /**
     * 上划取消录音
     */
    void tellUserHowCancelRecord() {
        if (recordSoundDialog != null && recordSoundDialog.cancelRecordView.getVisibility() == View.GONE) {
            recordSoundDialog.cancelRecordView.setVisibility(View.VISIBLE);
            recordSoundDialog.recordingView.setVisibility(View.GONE);
        }
    }

    /**
     * 点击录音
     */
    void showRecordingView() {
        if (recordSoundDialog != null && recordSoundDialog.recordingView.getVisibility() == View.GONE) {
            recordSoundDialog.cancelRecordView.setVisibility(View.GONE);
            recordSoundDialog.recordingView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 开始录音
     *
     * @param context
     * @param isShowDialog 是否需要显示录音动画
     * @param listener
     */
    public boolean startRecord(Context context, boolean isShowDialog, RecordListener listener) {
        // 应该先判断sd是否存在
        if (!FileUtil.sdcardAvailable()) {
            ToastUtil.showToast(context.getResources().getString(R.string.sdcard_not_exist));
            return false;
        }
        File dir = new File(soundFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        currentListener = listener;
        // 正在录音
        isRecording = true;
        isTryCancelRecord = false;

        resultFilePath = dir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".amr";
        recorder = new MediaRecorder();
        // 设置MediaRecorder的音频源为麦克风
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置MediaRecorder录制的音频格式
        recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        // 设置MediaRecorder录制音频的编码为amr
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置录制好的音频文件保存路径
        recorder.setOutputFile(resultFilePath);

        try {
            recorder.prepare();
            recorder.start();
            if (isShowDialog) {
                recordSoundDialog = new RecordSoundAnimation(context, R.style.publish_btn_dialog);
                recordSoundDialog.show();
            }
            // 启动计时
            startUpTime();
            isSuccessRecord = true;
        } catch (Throwable e) {
            isSuccessRecord = false;
            isRecording = false;
            ToastUtil.showToast(context.getText(R.string.record_failed));
            return false;
        }
        return true;
    }

    /**
     * 开始计时
     */
    private void startUpTime() {
        startTime = System.currentTimeMillis();
        update = new UpdateTime();
        recordTime = ONE_MINUTE;
        update.start();
    }

    /**
     * 结束计时
     */
    private void endTime() {
        if (update != null) {
            update.isStop = true;
            update = null;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        isRecording = false;
        if (recorder == null) {
            return;
        }
        // 只有录制成功了才会把数据发出去
        if (isSuccessRecord) {
            try {
                // 停止计时
                endTime();
                // 开始计算录音时间
                if (!isRecordTimeOut) {
                    duration = (int) (System.currentTimeMillis() - startTime) / 1000;
                } else {
                    // 录音时间超过一分钟
                    duration = ONE_MINUTE;
                    if (recordSoundDialog != null) {
                        recordSoundDialog.showFailedRecordHint(R.string.record_time_too_longer);
                    }
                }
                release();
                // 用户主动放弃录音，不做任何处理
                if (isTryCancelRecord) {
                    if (recordSoundDialog != null) {
                        recordSoundDialog.dismiss();
                    }
                    return;
                }

                if (duration < MIN_RECORD_DURATION) {
                    // 录制时间太短，废弃
                    if (recordSoundDialog != null) {
                        recordSoundDialog.showFailedRecordHint(R.string.record_time_too_shorter);
                    }
                    if (currentListener != null) {
                        currentListener.onFailedRecord();
                    }

                    File file = new File(resultFilePath);
                    if (file.exists()) {
                        file.delete();
                    }
                    resultFilePath = null;
                } else {
                    File file = new File(resultFilePath);
                    long length = file.length();
                    if (length <= 94) {
                        if (recordSoundDialog != null) {
                            recordSoundDialog.showFailedRecordHint(R.string.record_error_permission_denied);
                        }
                        if (currentListener != null) {
                            currentListener.onFailedRecord();
                        }
                        resultFilePath = null;
                        return;
                    }
                    // 由于计算出的时间不准确，所以用这种方法补充
                    if (duration != ONE_MINUTE) {
                        duration = ONE_MINUTE - recordTime;
                    }
                    if (recordSoundDialog != null && !isRecordTimeOut) {
                        recordSoundDialog.dismiss();
                    }
                    currentListener.onSuccessRecord(resultFilePath, duration);
                }
            } catch (final Throwable e) {
                if (recordSoundDialog != null) {
                    recordSoundDialog.handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(String.format(recordSoundDialog.getContext()
                                    .getString(R.string.record_failed_err_msg), e.getMessage()));
                        }
                    });
                }
            }
        } else {
            release();
        }
    }

    /**
     * 释放资源
     */
    private void release() {
        if (recorder != null) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (Throwable e) {
            }
        }
    }

    public String getSoundFilePath() {
        return resultFilePath;
    }

    public int getDuration() {
        return duration;
    }

    /**
     * 计时线程
     */
    private class UpdateTime extends Thread {

        private volatile boolean isStop = false;

        @Override
        public void run() {
            isRecordTimeOut = false;
            while (!isStop) {
                if (recordSoundDialog != null) {
                    recordSoundDialog.handler.obtainMessage().sendToTarget();
                }
                if (recordTime == 0) {
                    isRecordTimeOut = true;
                    isStop = true;
                    // 把录音停掉
                    stopRecord();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                recordTime--;
            }
        }

    }

    // 显示录音动画的view,其实这是一个dialog
    private class RecordSoundAnimation extends AlertDialog implements OnShowListener {

        private TextView recordHint;
        private ImageView recording;
        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 只有到9s的时候才有提示
                if (recordTime >= 1 && recordTime <= 9) {
                    recordCountdownView.setVisibility(View.VISIBLE);
                    recordCountdownView.setText(String.valueOf(recordTime));
                    recording.setVisibility(View.GONE);
                    failedRecordView.setVisibility(View.GONE);
                    if (anim != null && anim.isRunning()) {
                        anim.stop();
                    }
                }
            }

        };

        private AnimationDrawable anim;

        private View recordingView, cancelRecordView;

        private TextView recordCountdownView;
        private View failedRecordView;

        // 让对话框消失的runnable
        private Runnable dissDialog = new Runnable() {
            @Override
            public void run() {
                RecordSoundAnimation.this.dismiss();
            }
        };

        protected RecordSoundAnimation(Context context) {
            super(context);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        }

        RecordSoundAnimation(Context context, int theme) {
            super(context, theme);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        }

        /**
         * 显示失败的录音提示
         *
         * @param failedHintRes
         */
        void showFailedRecordHint(final int failedHintRes) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    recordCountdownView.setVisibility(View.GONE);
                    recordHint.setText(failedHintRes);
                    recording.setVisibility(View.GONE);
                    failedRecordView.setVisibility(View.VISIBLE);
                }
            });
            // 1s后消失
            handler.postDelayed(dissDialog, 1500);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.gmacs_layout_record_sound);
            setOnShowListener(this);
            recordHint = (TextView) findViewById(R.id.record_time);
            recording = (ImageView) findViewById(R.id.recordImg);
            recordingView = findViewById(R.id.recording);
            cancelRecordView = findViewById(R.id.cancel_record);
            failedRecordView = findViewById(R.id.failed_record_hint);
            recordCountdownView = (TextView) findViewById(R.id.record_countdown);
        }

        @Override
        public void hide() {
            super.hide();
        }

        @Override
        public void show() {
            super.show();
            recordHint.setVisibility(View.VISIBLE);
            recordingView.setVisibility(View.VISIBLE);
            cancelRecordView.setVisibility(View.GONE);
            recordCountdownView.setVisibility(View.GONE);
            failedRecordView.setVisibility(View.GONE);
            recording.setVisibility(View.VISIBLE);
            recordHint.setText(R.string.record_slide_up_to_cancel);
        }

        public void dismiss() {
            try {
                if (isShowing()) {
                    super.dismiss();
                }
                // 把隐藏dialog的runnable隐藏了
                handler.removeCallbacks(dissDialog);
                if (isShowing()) {
                    if (anim != null) {
                        anim.stop();
                    }
                }
            } catch (Exception e) {
            }
        }

        @Override
        public void onShow(DialogInterface dialog) {
            anim = (AnimationDrawable) recording.getBackground();
            anim.stop();
            anim.start();
        }

    }
}
