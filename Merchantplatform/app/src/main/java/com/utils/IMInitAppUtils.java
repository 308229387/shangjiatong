package com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Process;
import android.view.View;

import com.Utils.eventbus.IMKickoffEvent;
import com.android.gmacs.GmacsEventBusIndex;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.activity.GmacsContactDetailInfoActivity;
import com.android.gmacs.core.GmacsManager;
import com.android.gmacs.dragback.ParallaxBackActivityHelper;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.emoji.FaceConversionUtil;
import com.android.gmacs.view.emoji.GifUtil;
import com.baidu.mapapi.SDKInitializer;
import com.common.gmacs.core.ChannelManager;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.msg.EmojiManager;
import com.common.gmacs.msg.GifManager;
import com.common.gmacs.utils.GmacsConfig;
import com.common.gmacs.utils.GmacsUiUtil;
import com.merchantplatform.BuildConfig;
import com.merchantplatform.R;
import com.merchantplatform.activity.GuideActivity;
import com.merchantplatform.activity.LoginActivity;
import com.merchantplatform.application.HyApplication;
import com.wuba.wbpush.Push;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by SongYongmeng on 2016/11/28.
 */
public class IMInitAppUtils {

    private final HyApplication application;

    public IMInitAppUtils(HyApplication application) {
        this.application = application;
        init();
    }

    private void init() {
        if (isUIProcess()) {
            SDKInitializer.initialize(application);//百度地图
            Gmacs.getInstance().initialize(application);//im环境配置
            configDebugLog();//配置DebugLog
            setEnvironment();
            setInfo();
            GmacsUiUtil.setAppMainClassName(GuideActivity.class.getName());
            GmacsUiUtil.setChatClassName(GmacsChatActivity.class.getName());//聊天详情
            GmacsUiUtil.setContactDetailInfoActivityClassName(GmacsContactDetailInfoActivity.class.getName());//用户详情

            EmojiManager.getInstance().setEmojiPaser(FaceConversionUtil.getInstace());
            GifManager.getInstance().setGifParser(GifUtil.getInstance());
            EventBus.builder().sendNoSubscriberEvent(true)
                    .addIndex(new GmacsEventBusIndex())
                    .installDefaultEventBus();
            ClientManager.getInstance().regConnectListener(new ClientManager.ConnectListener() {
                @Override
                public void connectStatusChanged(int status) {
                    if (GmacsConstant.STATUS_KICK_OFF == status) {
                        GmacsManager.isLoginState = false;
                        EventBus.getDefault().post(new IMKickoffEvent());
                    }

                    if (GmacsConstant.STATUS_CONNECTED_VALUE == status) {
                        GmacsManager.isLoginState = true;
                    }
                }

                @Override
                public void connectionTokenInvalid(final String errorMsg) {
                    logout("登录过期，请您重新登录！");
                }
            });

        }
    }

    private void logout(final String dialogMsg) {
        GmacsConfig.ClientConfig.removeParam(WChatConstant.LOGIN_INFO);
        Push.getInstance().binderUserID("");
        final Activity activity = ParallaxBackActivityHelper.getPeakActivity();
        ParallaxBackActivityHelper.finishAllExceptPeek();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final GmacsDialog.Builder dialog = new GmacsDialog.Builder(activity, GmacsDialog.Builder.DIALOG_TYPE_TEXT_NEU_BUTTON);
                    dialog.initDialog(dialogMsg, application.getText(R.string.ok), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            GmacsManager.getInstance().stopGmacs();
                            activity.finish();
                        }
                    }).setCancelable(false).create().show();
                }
            });
        }
    }

    public void setEnvironment() {
        ClientManager.getInstance().setServerLevel(Gmacs.SERVER_INTEGRATE);
        //ClientManager.getInstance().setServerLevel(Gmacs.SERVER_ONLINE);
    }

    public void setInfo() {
        ChannelManager.getInstance().init("商家通", "app", "1.0.0",
                "100217-wb@a2hvgoAwgHY");
    }

    private void configDebugLog() {
        if (BuildConfig.DEBUG)
            Gmacs.getInstance().setLoggable(true);
        else
            Gmacs.getInstance().setLoggable(false);
    }

    private boolean isUIProcess() {
        int pid = Process.myPid();
        String processName = "";
        ActivityManager mActivityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                processName = appProcess.processName;
                break;
            }
        }
        String packageName = application.getPackageName();
        return processName.equals(packageName);
    }
}