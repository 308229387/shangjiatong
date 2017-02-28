package com.merchantplatform.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.Utils.Urls;
import com.callback.JsonCallback;
import com.merchantplatform.bean.GetSystemTime;
import com.merchantplatform.bean.GetTask;
import com.okhttputils.OkHttpUtils;
import com.utils.ToastUtils;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by songyongmeng on 2017/2/7.
 * 描    述：此服务为获取系统时间的服务。
 */

public class GetServiceTimeTask extends Service {

    private Timer timer;
    private TimerTask task;
    final Handler handler = new TempHandler();
    private int taskTime = -1;
    private int surplusTime;
    final int WHAT = 102;
    private long tempSystemTime = 1486537996648l;
    private int sytemTime;
    public static int hour ;
    public static int minute ;
    public static int second ;

    @Override
    public void onCreate() {
        super.onCreate();
        getTask();
        CereatTimer();
    }

    public void getTask() {
        OkHttpUtils.get(Urls.GET_TASK)
                .execute(new Task());
    }

    private void CereatTimer() {
        task = new TempTimeTask();
        timer = new Timer();
        timer.schedule(task, 0, 600000);
    }

    private void setMessage() {
        Message message = new Message();
        message.what = WHAT;
        message.obj = System.currentTimeMillis();
        handler.sendMessage(message);
    }

    public void getSystemTime() {
        OkHttpUtils.get(Urls.GET_SERVICE_TIME)
                .execute(new TimeData());
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

    public int dealWithTimeToSecond(String[] a) {
        int hour = Integer.parseInt(a[0]) * 3600;
        int minute = Integer.parseInt(a[1]) * 60;
        int second = Integer.parseInt(a[2]);
        int testAll = hour + minute + second;
        return testAll;
    }

    public int dealWithTimeToResult() {
        if (taskTime - sytemTime > 0)
            surplusTime = taskTime - sytemTime;
        else
            surplusTime = taskTime + (86400 - sytemTime);
        return surplusTime;
    }

    public int[] calculateResult() {
        int[] a = new int[3];
        a[0] = surplusTime / 3600;
        a[1] = (surplusTime % 3600) / 60;
        a[2] = (surplusTime % 3600) % 60;
        return a;
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
            if (taskTime != -1)
                surplusTime = taskTime - messageTimeFormat(Long.valueOf(a));
            ToastUtils.showToast(surplusTime);
        }
    }

    private class Task extends JsonCallback<GetTask> {
        @Override
        public void onResponse(boolean isFromCache, GetTask s, Request request, @Nullable Response response) {
            try {
                dealWithData(s);
            } catch (Exception e) {
                ToastUtils.showToast("数据解析错误");
            }
        }

        private void dealWithData(GetTask s) {
            String a = s.getData().getOpentime();
            String[] b = a.split(":");
            taskTime = dealWithTimeToSecond(b);
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
//                    getSystemTime();
                    sytemTime = messageTimeFormat(tempSystemTime);
                    dealWithTimeToResult();
                    hour = calculateResult()[0];
                    minute = calculateResult()[1];
                    second = calculateResult()[2];
                    break;
            }
        }

    }
}
