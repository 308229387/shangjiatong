package com.android.gmacs.sound;

import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class SoundRecordUtil {

    //用于滑动取消发送录音
    private static Rect mRecordViewRect;

    private static void initRecordViewRect(View recordView) {
        int[] location = new int[2];
        recordView.getLocationInWindow(location);
        int left = location[0];
        int top = location[1];
        int right = left + recordView.getWidth();
        int bottom = top + recordView.getHeight();
        mRecordViewRect = new Rect(left, top, right, bottom);
    }

    public static void dispatchTouchEvent(MotionEvent ev, SoundRecord record, View recordView) {
        //只有录音状态才走下面逻辑
        if (record.isRecording()) {
            int x = (int) (ev.getX());
            int y = (int) (ev.getY());
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if (mRecordViewRect == null) {
                    initRecordViewRect(recordView);
                }
                //已经移除了录音button范围，进行其他处理
                if (!mRecordViewRect.contains(x, y)) {
                    record.tellUserHowCancelRecord();
                } else {
                    record.showRecordingView();
                }
            } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                //mRecordViewRect为null表明根本就没滑动的动作，根本就没录音
                if (mRecordViewRect != null) {
                    record.isTryCancelRecord = !mRecordViewRect.contains(x, y);
                    if (record.isTryCancelRecord) {
                        record.stopRecord();
                    }
                }
            }
        }
    }

    public static void destroy() {
        mRecordViewRect = null;
    }
}
