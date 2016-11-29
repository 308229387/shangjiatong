package com.utils;

import android.content.Context;
import android.text.TextUtils;

import com.push.WPushListener;
import com.wuba.wbpush.Push;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/11/29.
 */

public class WPushInitUtils implements  Push.MessageListener,
        Push.PushErrorListener,
        Push.DeviceIDAvalibleListener,
        Push.NotificationClickedListener{

    private WPushListener pushListener;
    private String mDeviceID;
    private ArrayList<Push.PushMessage> mPushMessageList ;

    public WPushInitUtils(Context context) {
        mDeviceID = null;
        mPushMessageList = new ArrayList<>();
        Push.getInstance().registerMessageListener(this);//消息到达监听器
        Push.getInstance().setErrorListener(this);//设置错误监听器
        Push.getInstance().setDeviceIDAvalibleListener(this);//设置设备ID监听器
        Push.getInstance().setNotificationClickedListener(this);//设置通知点击监听器
        Push.getInstance().enableDebug(context, true); //线下Debug模式true，正式为false
        Push.getInstance().registerPush(context, Constant.WPUSH_APP_ID, Constant.WPUSH_APP_KEY, AppInfoUtils.getChannelId(context));
//        Push.getInstance().binderUserID(""); //绑定/解绑用户信息:非空串,绑定指定的userID,空串(“”),解绑userID
//        Push.getInstance().binderAlias(""); //绑定/解绑别名:非空串,绑定指定的alias ,空串(“”),解绑alias。
    }

    public void setPushListener(WPushListener listener) {
        pushListener = listener;
        if (!TextUtils.isEmpty(mDeviceID) && pushListener != null) {
            pushListener.onDeviceIDAvalible(mDeviceID);
        }
        synchronized (mPushMessageList) {
            for (int i = 0; i < mPushMessageList.size();i++) {
                pushListener.OnMessage(mPushMessageList.get(i));
            }
            mPushMessageList.clear();
        }
    }


    @Override
    public void onDeviceIDAvalible(String deviceID) {
        if (pushListener != null) {
            pushListener.onDeviceIDAvalible(deviceID);
        }else {
            mDeviceID = deviceID;
        }
    }

    @Override
    public void OnMessage(Push.PushMessage pushMessage) {
        //注意如果pushListener为空，则需要上层存储次消息
        if (pushListener != null) {
            pushListener.OnMessage(pushMessage);
        }else {
            synchronized (mPushMessageList) {
                mPushMessageList.add(pushMessage);
            }
        }
    }

    @Override
    public void onNotificationClicked(String messageId) {
        if (pushListener != null) {
            pushListener.onNotificationClicked(messageId);
        }
    }

    @Override
    public void onError(int errorCode, String errorString) {
        if (pushListener != null) {
            pushListener.onError(errorCode, errorString);
        }
    }
}
