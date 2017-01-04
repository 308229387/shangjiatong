package com.push;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.Utils.SystemNotification;
import com.dataStore.SettingPushPreferUtil;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.application.HyApplication;

/**
 * Created by 58 on 2016/12/9.
 */

public class WPushNotify {

    protected static int notifyID = 011000;

    //推送显示在通知栏
    public static void notification(SystemNotification bean){
        if(bean == null){
            return ;
        }

        NotificationManager mNotificationManager = (NotificationManager) HyApplication.getApplication().getSystemService(HyApplication.getApplication().NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(HyApplication.getApplication());

        Resources res = HyApplication.getApplication().getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.mipmap.iv_logo);

        //设置通知栏基本属性
        mBuilder.setContentTitle(bean.getTitle())//设置通知栏标题
                .setContentText(bean.getDescribe())//设置通知栏显示内容
//              .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
//              .setNumber(number)//设置通知集合的数量
                .setLargeIcon(bmp)//设置通知大ICON
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setTicker(bean.getTitle())//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_MAX)//设置该通知优先级
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(getNotifyType())//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.iv_logo);//设置通知小ICON

        //设置通知栏点击之后的跳转
        Intent intent = getStartActivityIntent(HyApplication.getApplication(),bean);
        if(intent != null){
            PendingIntent pendingIntent = PendingIntent.getActivity(HyApplication.getApplication(),notifyID,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(pendingIntent);
            mNotificationManager.notify(notifyID,mBuilder.build());
        }

    }

    public static int getNotifyType(){
        int type = Notification.DEFAULT_LIGHTS;

        if(SettingPushPreferUtil.getInstance(HyApplication.getApplication()).isPushSoundAlertOpened()){
            if(SettingPushPreferUtil.getInstance(HyApplication.getApplication()).isPushVibrateAlertOpened()){
                type = Notification.DEFAULT_ALL;
            }else{
                type = Notification.DEFAULT_SOUND;
            }
        }else if(SettingPushPreferUtil.getInstance(HyApplication.getApplication()).isPushVibrateAlertOpened()){
            type = Notification.DEFAULT_VIBRATE;
        }

        return type;
    }


    private static Intent getStartActivityIntent(Context context, SystemNotification pushBean){
        if(pushBean == null){
            return null;
        }
        Intent intent = new Intent(context, HomepageActivity.class);
        intent.putExtra("pushBean", pushBean);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

}
