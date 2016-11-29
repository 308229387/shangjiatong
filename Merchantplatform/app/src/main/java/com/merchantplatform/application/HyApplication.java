package com.merchantplatform.application;

import android.app.Activity;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.merchantplatform.BuildConfig;
import com.okhttputils.OkHttpUtils;
import com.push.WPushListener;
import com.tencent.bugly.crashreport.CrashReport;
import com.utils.Constant;
import com.utils.IMInitUtils;
import com.utils.AppInfoUtils;
import com.utils.LoginRegisterUtils;
import com.wuba.wbpush.Push;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：黄页商店平台Application,此界面会注册框架、全局上下文、单例对象等，请注意维护此界面整洁！
 */

public class HyApplication extends MultiDexApplication implements Push.MessageListener,
        Push.PushErrorListener,
        Push.DeviceIDAvalibleListener,
        Push.NotificationClickedListener{

    private static HyApplication instance;
    private static HyApplication application;
    private WPushListener pushListener;
    private String mDeviceID;
    private ArrayList<Push.PushMessage> mPushMessageList ;
    private List<Activity> activityList = new LinkedList<>();
    private static final String WPUSH_APP_ID= "1007"; //WPush的APP_ID
    private static final String WPUSH_APP_KEY = "b04RT5u0dyWXjewN"; //WPush的APP_KEY
    private static final String BUGLY_APP_ID = "900060310";

    @Override
    public void onCreate() {
        super.onCreate();
        initConfig();
    }

    private void initConfig() {
        setApplicationContext();
        initWPush();
        initOkHttp();
        initLogin();
        initIM();
        initBugly();
    }

    private void setApplicationContext() {
        application = this;
    }

    private void initWPush(){
        mDeviceID = null;
        mPushMessageList = new ArrayList<>();
        Push.getInstance().registerMessageListener(this);//消息到达监听器
        Push.getInstance().setErrorListener(this);//设置错误监听器
        Push.getInstance().setDeviceIDAvalibleListener(this);//设置设备ID监听器
        Push.getInstance().setNotificationClickedListener(this);//设置通知点击监听器
        Push.getInstance().enableDebug(this, true); //线下Debug模式true，正式为false
        Push.getInstance().registerPush(this, WPUSH_APP_ID, WPUSH_APP_KEY, AppInfoUtils.getChannelId(this));
//        Push.getInstance().binderUserID(""); //绑定/解绑用户信息:非空串,绑定指定的userID,空串(“”),解绑userID
//        Push.getInstance().binderAlias(""); //绑定/解绑别名:非空串,绑定指定的alias ,空串(“”),解绑alias。
    }

    private void initOkHttp() {
        OkHttpUtils.init(application);
    }

    public void initLogin() {
        new LoginRegisterUtils(application);
    }

    public void initIM() {
        new IMInitUtils(application);
    }

    private void initBugly() {
        if (!BuildConfig.DEBUG)
            CrashReport.initCrashReport(application, Constant.BUGLY_APP_ID, false);
    }

    public void addActivity(Activity activity) {
        if (!activityList.contains(activity))
            activityList.add(activity);
    }

    public static HyApplication getInstance() {
        if (null == instance)
            instance = new HyApplication();
        return instance;
    }

    public void removeActivity(Activity context) {
        if (activityList.contains(context))
            activityList.remove(context);
    }

    public static Context getApplication() {
        return application;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(application);
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
    public void onDeviceIDAvalible(String deviceID) {
        if (pushListener != null) {
            pushListener.onDeviceIDAvalible(deviceID);
        }else {
            mDeviceID = deviceID;
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