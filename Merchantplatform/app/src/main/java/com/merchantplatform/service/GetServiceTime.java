package com.merchantplatform.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.callback.JsonCallback;
import com.merchantplatform.bean.GetSystemTime;
import com.okhttputils.OkHttpUtils;
import com.utils.ToastUtils;
import com.utils.Urls;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by songyongmeng on 2017/2/7.
 * 描    述：此服务为获取系统时间的服务。因为目前需求为计算倒计时，已经自动转化为秒，
 */

public class GetServiceTime extends Service {

    private Timer timer;
    private TimerTask task;
    final Handler handler = new TempHandler();
    final int WHAT = 102;
    private long tempSystemTime = 1486537996648l;
    public static int systemTimeSecond = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getSystemTime();
        systemTimeSecond = messageTimeFormat(tempSystemTime);
        CereatTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    public void getSystemTime() {
        OkHttpUtils.get(Urls.GET_SERVICE_TIME)
                .execute(new TimeData());
    }

    private void CereatTimer() {
        task = new TempTimeTask();
        timer = new Timer();
        timer.schedule(task, 0, 1000);
    }

    private void setMessage() {
        Message message = new Message();
        message.what = WHAT;
        message.obj = System.currentTimeMillis();
        handler.sendMessage(message);
    }

    protected int messageTimeFormat(long messageTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(messageTime);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        int SystemHour = calendar.get(Calendar.HOUR);
        int SystemMinute = calendar.get(Calendar.MINUTE);
        int SystemSecond = calendar.get(Calendar.SECOND);

        return SystemHour * 3600 + SystemMinute * 60 + SystemSecond;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class TimeData extends JsonCallback<GetSystemTime> {
        @Override
        public void onResponse(boolean isFromCache, GetSystemTime s, Request request, @Nullable Response response) {
            dealWithData(s);
        }

        private void dealWithData(GetSystemTime s) {
            String a = s.getData().getCurrentTime();
            systemTimeSecond = messageTimeFormat(Long.valueOf(a));
        }
    }

    public class TempTimeTask extends TimerTask {
        @Override
        public void run() {
            setMessage();
        }
    }

    public class TempHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT:
                    if (systemTimeSecond != (-1))
                        systemTimeSecond++;
                    Log.i("song", systemTimeSecond + "");
                    break;
            }
        }

    }
}
