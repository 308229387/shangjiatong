package com.android.gmacs.sound;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.text.TextUtils;

import com.common.gmacs.utils.GmacsEnvi;

import java.util.List;

/**
 * 每时每刻只有一个声音正在播放
 */
public class SoundPlayer implements OnCompletionListener, OnErrorListener {

    private static SoundPlayer mSoundPlayer = new SoundPlayer();

    /**
     * 主要用于聊天界面，标志哪行声音在被播放，-1表明播放的是普通不需要显示动画的声音动作
     */
    private long mCurrentPlayId = -2;
    private VoiceCompletion mCurrentListener;

    /**
     * 声音是否正在播放
     */
    private boolean mIsSoundPlaying;

    private SoundPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    public boolean ismIsSoundPlaying() {
        return mIsSoundPlaying;
    }

    public static synchronized SoundPlayer getInstance() {
        return mSoundPlayer;
    }

    private MediaPlayer mPlayer;

    /**
     * 用在普通的播放声音，播放声音时不需要动画效果
     *
     * @param fileName
     */
    private void startPlaying(String fileName) {
        try {
            // setVolumeToMax();
            mPlayer.reset();
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.start();
            mIsSoundPlaying = true;
        } catch (Exception e) {
            if (mCurrentListener != null) {
                mCurrentPlayId = -2;
                mCurrentListener.onCompletion(mPlayer, false);
                mCurrentListener = null;
            }
        }
    }

    public void stopPlay() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mCurrentListener = null;
        mCurrentPlayId = -2;
    }

    /**
     * 停止声音播放，并且把动画停掉
     */
    public void stopPlayAndAnimation() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mCurrentPlayId = -2;
        if (mCurrentListener != null) {
            mCurrentListener.onCompletion(mPlayer, false);
            mCurrentListener = null;
        }
    }

    /**
     * 用在聊天界面，播放声音，需要动画效果
     *
     * @param fileName
     * @param listener position 列表中哪行声音被播放
     */
    public void startPlaying(String fileName, VoiceCompletion listener, long msgId) {
        if (TextUtils.isEmpty(fileName)) {
            mCurrentPlayId = -2;
            listener.onCompletion(mPlayer, false);
            return;
        }

        // 说明当前行正在播放，停掉
        if (msgId == mCurrentPlayId) {
            mIsSoundPlaying = false;
            // this.onCompletion(mPlayer);
            stopPlay();
            if (mCurrentListener != null) {
                mCurrentListener.onCompletion(mPlayer, false);
            }
            return;
        } else {
            // 把上一个正在播放的停掉
            // mPlayer.stop();
            if (mCurrentListener != null) {
                mCurrentListener.onCompletion(mPlayer, false);
            }
            mIsSoundPlaying = false;
        }
        mCurrentListener = listener;
        mCurrentPlayId = msgId;
        startPlaying(fileName);
    }

    public void autoStartPlaying(String fileName, VoiceCompletion listener, long msgId) {
        if (TextUtils.isEmpty(fileName)) {
            mCurrentPlayId = -2;
            listener.onCompletion(mPlayer, false);
            return;
        }
        mIsSoundPlaying = false;
        mCurrentListener = listener;
        mCurrentPlayId = msgId;
        startPlaying(fileName);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mCurrentListener != null) {
            mCurrentPlayId = -2;
            if (isBackground(GmacsEnvi.appContext)) {
                mCurrentListener.onCompletion(mp, false);
            } else {
                mCurrentListener.onCompletion(mp, true);
            }
        }
        mIsSoundPlaying = false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (mCurrentListener != null) {
            mCurrentPlayId = -2;
            mCurrentListener.onCompletion(mp, false);
            mCurrentListener = null;
        }
        mIsSoundPlaying = false;
        return false;
    }

    /**
     * 聊天界面正在播放声音索引值，-1表示没有
     *
     * @return
     */
    public long currentPlayId() {
        return mCurrentPlayId;
    }

    public interface VoiceCompletion {
        void onCompletion(MediaPlayer mp, boolean isNormal);
    }

    private static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                return appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }
}
