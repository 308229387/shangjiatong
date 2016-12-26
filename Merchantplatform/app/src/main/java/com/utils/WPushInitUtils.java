package com.utils;

import android.content.Context;
import android.util.Log;

import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.google.gson.Gson;
import com.Utils.SystemNotification;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.application.HyApplication;
import com.wuba.wbpush.Push;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 58 on 2016/11/29.
 */

public class WPushInitUtils implements Push.MessageListener,
        Push.PushErrorListener,
        Push.DeviceIDAvalibleListener,
        Push.NotificationClickedListener {

    public WPushInitUtils(Context context) {
        Push.getInstance().registerMessageListener(this);//消息到达监听器
        Push.getInstance().setErrorListener(this);//设置错误监听器
        Push.getInstance().setDeviceIDAvalibleListener(this);//设置设备ID监听器
        Push.getInstance().setNotificationClickedListener(this);//设置通知点击监听器
        Push.getInstance().enableDebug(context, true); //线下Debug模式true，正式为false
        Push.getInstance().registerPush(context, Constant.WPUSH_APP_ID, Constant.WPUSH_APP_KEY, AppInfoUtils.getChannelId(context));
//        Push.getInstance().binderUserID(""); //绑定/解绑用户信息:非空串,绑定指定的userID,空串(“”),解绑userID

    }


    @Override
    public void onDeviceIDAvalible(String deviceID) {
    }

    /**
     * 收到的Push消息
     *
     * @param pushMessage
     */
    @Override
    public void OnMessage(Push.PushMessage pushMessage) {
        Gson temp = new Gson();
        final SystemNotification bean = temp.fromJson(pushMessage.messageContent, SystemNotification.class);

        if (bean.getType() == 103) {
            saveDataToDB(bean);
            EventBus.getDefault().post(bean);
        }

        Log.i("song", pushMessage.messageContent);

//         //应用在后台，不需要刷新UI,通知栏提示新消息
//        if(!AppInfoUtils.isRunningForeground(HyApplication.getApplication())){
//            WPushNotify.notification(messageContent);
//        }

        String type = pushMessage.messageType == Push.MessageType.Notify ? "Notify" : "PassThrough";
        String msgString = null;
        if (pushMessage.messageInfos != null) {
            msgString = String.format("messgeID:%s messageType:%s messaegContent:%s pushType:%s",
                    pushMessage.messageID, type, pushMessage.messageContent, pushMessage.messageInfos.get("pushType"));
        } else {
            msgString = String.format("messgeID:%s messageType:%s messaegContent:%s",
                    pushMessage.messageID, type, pushMessage.messageContent);
        }
        Log.d("PushUtils", "onMessage-pushMessage:" + msgString);


    }

    private void saveDataToDB(SystemNotification temp1) {
        final SystemNotificationDetial data = new SystemNotificationDetial();
        data.setType(temp1.getType());
        data.setTitle(temp1.getTitle());
        data.setSortId(temp1.getSortId());
        data.setId(temp1.getId());
        data.setContent(temp1.getContent());
        data.setContentType(temp1.getContentType());
        data.setDescribe(temp1.getDescribe());
        new Thread() {
            @Override
            public void run() {
                SystemNotificationOperate.insertOrReplace(HyApplication.getApplication(), data);
            }
        }.start();
    }

    /**
     * 点击通知事件监听
     *
     * @param messageId
     */
    @Override
    public void onNotificationClicked(String messageId) {
    }

    @Override
    public void onError(int errorCode, String errorString) {

    }


}
