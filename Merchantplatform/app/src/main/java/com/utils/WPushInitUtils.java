package com.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.Utils.SystemNotification;
import com.Utils.UserUtils;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.google.gson.Gson;
import com.merchantplatform.R;
import com.merchantplatform.application.HyApplication;
import com.push.WPushNotify;
import com.ui.dialog.LogoutDialog;
import com.wuba.wbpush.Push;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 58 on 2016/11/29.
 */

public class WPushInitUtils implements Push.MessageListener,
        Push.PushErrorListener,
        Push.DeviceIDAvalibleListener,
        Push.NotificationClickedListener {

    private  Context mContext;

    public WPushInitUtils(Context context) {
        this.mContext = context;
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
        try {
            Gson temp = new Gson();
            final SystemNotification bean = temp.fromJson(pushMessage.messageContent, SystemNotification.class);
            if (bean.getType() == 103) {
                //应用在后台，不需要刷新UI,通知栏提示新消息
                if (!AppInfoUtils.isRunningForeground(HyApplication.getApplication())) {
                    WPushNotify.notification(bean);
                }
                saveDataToDB(bean);
                EventBus.getDefault().post(bean);
            } else if (bean.getType() == 104) {
                List<Activity> list = HyApplication.getInstance().getActivityList();
                if (list != null && list.size() > 0) {
                    Activity activity = list.get(list.size() - 1);
                    if(!TextUtils.isEmpty(bean.getDescribe())){
                        new LogoutDialog(activity, bean.getDescribe());
                    }else{
                        new LogoutDialog(activity, activity.getString(R.string.force_exit));
                    }

                }
            }
        } catch (Exception e) {
            LogUtils.e("wpush", e.getMessage());
        }

        Log.i("song", pushMessage.messageContent);

    }

    private void saveDataToDB(SystemNotification temp1) {
        ArrayList<SystemNotificationDetial> temp = SystemNotificationOperate.checkReaded(HyApplication.getApplication());
        final SystemNotificationDetial data = new SystemNotificationDetial();
        data.setType(temp1.getType());
        data.setTitle(temp1.getTitle());
        data.setUserId(UserUtils.getUserId(mContext));
        data.setSortId(temp1.getSortId());
        data.setId(temp1.getId());
        data.setContent(temp1.getContent());
        data.setContentType(temp1.getContentType());
        data.setDescribe(temp1.getDescribe());
        data.setIsReaded(temp.size() + 1);
        temp1.setIsReaded(temp.size() + 1);
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
