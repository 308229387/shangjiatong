package com.android.gmacs.msg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.android.gmacs.R;
import com.android.gmacs.logic.MessageLogic;
import com.common.gmacs.core.ChannelManager;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.downloader.RequestManager;
import com.common.gmacs.downloader.VolleyError;
import com.common.gmacs.downloader.image.ImageLoader;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.TalkType;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.ImageUtil;

import java.util.Calendar;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;

/**
 * Created by caotongjun on 2015/12/22.
 */
public class MessageNotifyHelper extends MessageLogic.NotifyHelper {
    private static final int NTFC_ID_GMACS = 1024;
    private String refer;

    @Override
    protected void showNotify(Message msg) {
        super.showNotify(msg);
    }

    @Override
    protected void showMsgNotification(final Message message) {

        IMMessage msg = message.mMsgDetail.getmMsgContent();
        refer = message.mMsgDetail.getRefer();

        final NotificationManager nm = (NotificationManager) GmacsEnvi.appContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String tickerText;
        String content;
        int noticeId;
        final String title = ChannelManager.appName;
        String msgContent = msg.getPlainText();
        Intent intent = null;

        Message.MessageUserInfo otherUserInfo = message.getTalkOtherUserInfo();
        try {
            intent = new Intent(GmacsEnvi.appContext, Class.forName(GmacsUiUtil.getAppMainClassName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (intent != null) {
            intent.putExtra(GmacsConstant.EXTRA_REFER, refer);
            intent.putExtra(GmacsConstant.EXTRA_OHTER_USER_INFO, otherUserInfo);
            intent.putExtra(GmacsConstant.FROM_NOTIFY_EXTRA, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        //判断是群消息还是一对一消息
        if (TalkType.isGroupTalk(message)) {
            if (message.mMsgDetail.isShowSenderName()) {
                tickerText = message.mSenderInfo.gmacsUserInfo == null ? ""
                        : message.mSenderInfo.gmacsUserInfo.userName
                        + "（"
                        + otherUserInfo.getUserName(GmacsEnvi.appContext) + "）" + "："
                        + msgContent;
            } else {
                tickerText = "（"
                        + otherUserInfo.getUserName(GmacsEnvi.appContext) + "）" + "："
                        + msgContent;
            }
        } else if (TalkType.isNormalTalk(message)) {
            if (message.mMsgDetail.isShowSenderName()) {
                tickerText = otherUserInfo.getUserName(GmacsEnvi.appContext) + "：" + msgContent;
            } else {
                tickerText = msgContent;
            }
        } else {
            tickerText = msgContent;
        }
        try {
            noticeId = otherUserInfo.mUserId.hashCode();
        } catch (Exception e) {
            e.printStackTrace();
            noticeId = NTFC_ID_GMACS;
        }
        content = tickerText;
        final PendingIntent pendingIntent = PendingIntent.getActivity(GmacsEnvi.appContext,
                noticeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String avatar = message.getTalkOtherUserInfo().gmacsUserInfo.avatar;
        final String finalTickerText = tickerText;
        final String finalContent = content;
        final int finalNoticeId = noticeId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RequestManager.getInstance().getImageLoader().get(ImageUtil.makeUpUrl(avatar, IMG_RESIZE, IMG_RESIZE), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        Notification notification = new NotificationCompat.Builder(
                                GmacsEnvi.appContext)
                                .setTicker(finalTickerText)
                                .setContentTitle(title)
                                .setContentText(finalContent)
                                .setSmallIcon(R.drawable.icon_transparent)
                                .setLargeIcon(response.getBitmap())
                                .setAutoCancel(true).setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .build();
                        notification = configNotification(message, notification);
                        if (notification != null) {
                            nm.notify(finalNoticeId, notification);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Notification notification = new NotificationCompat.Builder(
                            GmacsEnvi.appContext)
                            .setTicker(finalTickerText)
                            .setContentTitle(title)
                            .setContentText(finalContent)
                            .setSmallIcon(R.drawable.icon_transparent)
                            .setLargeIcon(BitmapFactory.decodeResource(GmacsEnvi.appContext.getResources(), R.drawable.icon))
                            .setAutoCancel(true).setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .build();
                    notification = configNotification(message, notification);
                    if (notification != null) {
                        nm.notify(finalNoticeId, notification);
                    }
                }
            }, IMG_RESIZE, IMG_RESIZE);
        } else {
            Notification notification = new NotificationCompat.Builder(
                    GmacsEnvi.appContext)
                    .setTicker(finalTickerText)
                    .setContentTitle(title)
                    .setContentText(finalContent)
                    .setSmallIcon(R.drawable.icon)
                    .setAutoCancel(true).setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            notification = configNotification(message, notification);
            if (notification != null) {
                nm.notify(finalNoticeId, notification);
            }
        }
    }

    @Override
    protected Notification configNotification(Message message, Notification notification) {

        // 声音、震动提醒方式是否在允许的时间段内
        int timeOfstart = 8;//允许收到通知开始时间
        int timeOfend = 22;//允许收到通知结束时间
        boolean openSound = true;//是否开启声音
        boolean openShake = true;//是否震动

        Calendar now = Calendar.getInstance();
        Calendar startTime = (Calendar) now.clone();
        startTime.set(Calendar.MINUTE, 0);
        startTime.set(Calendar.HOUR_OF_DAY, timeOfstart);
        Calendar endTime = (Calendar) now.clone();
        endTime.set(Calendar.MINUTE, 0);
        endTime.set(Calendar.HOUR_OF_DAY, timeOfend);
        if (now.before(startTime) || now.after(endTime)) {
            openSound = false;
            openShake = false;
        }

        notification.defaults = 0;

        if (openSound) {
            // 可以声音提醒
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        if (openShake) {
            // 可以震动
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        return notification;
    }
}
