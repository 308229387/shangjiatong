package com.merchantplatform.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

public class PhoneReceiver extends BroadcastReceiver {

    public static final String CALL_OVER = "call_over";
    public static final String CALL_UP = "call_up";
    public static final String CALL_OUT = "call_out";

    public static ArrayList<BRInteraction> interactionList = new ArrayList<>();

    public static void addToMonitor(BRInteraction interaction) { //增加监听
        interactionList.add(interaction);
    }

    public static void releaseMonitor(BRInteraction interaction) { //释放监听，避免内存泄漏等问题
        interactionList.remove(interaction);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) { //如果是去电

        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE); //获取电话服务管理类
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE); //侦听呼叫状态改变事件
        }
    }

    PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) { //处理侦测到的电话呼叫状态的改变事件。通过该回调事件，可以获取来电号码，而且可以获取电话呼叫状态。
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲（无呼入或已挂机）
                    for (BRInteraction interaction : interactionList) { //通知所有监听电话已经挂断
                        if (interaction != null) {
                            interaction.sendAction(CALL_OVER);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING://响铃（接听中）接听
                    for (BRInteraction interaction : interactionList) {
                        if (interaction != null) {
                            interaction.sendAction(CALL_UP);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK://摘机（有呼入）,呼出
                    for (BRInteraction interaction : interactionList) {
                        if (interaction != null) {
                            interaction.sendAction(CALL_OUT);
                        }
                    }
                    break;
            }
        }
    };

    public interface BRInteraction {
        void sendAction(String action);
    }
}