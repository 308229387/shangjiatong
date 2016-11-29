package com.push;

import com.wuba.wbpush.Push;

/**
 * Created by 58 on 2016/11/28.
 */

public interface WPushListener {
    public void onDeviceIDAvalible(String deviceID);
    public void onError(int errorCode,String errorString);
    public void onNotificationClicked(String messageId);
    public void OnMessage(Push.PushMessage message);
}
