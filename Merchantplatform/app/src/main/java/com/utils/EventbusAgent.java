package com.utils;

import android.content.Context;

import org.greenrobot.eventbus.EventBus;

/**
 * 第三方工具Eventbus的封装类
 * Eventbus 基于观察者模式进行设计，主要功能是替代Intent,Handler,BroadCast
 * 在Fragment，Activity，Service，线程之间传递消息.
 * 优点是开销小，代码更优雅。以及将发送者和接收者解耦。
 * 使用方法：
 * 事件，事件可以是任意类型的对象，
 * 发布者，post事件，通知订阅者
 * 订阅者，接收特定事件的对象，主要通过onEventXXX()回调接口接收
 * Created by 58 on 2015/9/1.
 */
public class EventbusAgent {
    private static EventbusAgent defaultInstance = null;

    public static EventbusAgent getInstance() {
        if (defaultInstance == null) {
            synchronized (EventbusAgent.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventbusAgent();
                }
            }
        }
        return defaultInstance;
    }

    private EventbusAgent() {
    }

    /**
     * 不管是事件发布者还是订阅者都需要向Bus注册自己
     * 在使用Eventbus去post或者侦听事件之前，需要先进行注册
     */
    public void register(Context context) {
        EventBus.getDefault().register(context);
    }

    /**
     * 与register配合使用
     */
    public void unregister(Context context) {
        EventBus.getDefault().unregister(context);
    }

    /**
     * 事件发布者调用此方法进行发布消息
     *
     * @param event 为EventAction类型
     */
    public void post(Object event) {
        EventBus.getDefault().post(event);
    }

    /**
     * 事件订阅者可以选择实现的方法之一
     * 如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，
     * onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行，这个在Android中是非常有用的，
     * 因为在Android中只能在UI线程中更新UI，所以在onEvnetMainThread方法中是不能执行耗时操作的。
     * @param action
     */
//    public void onEventMainhread(EventAction action)
//    {
//        switch (action.getType()) {
//            case EVENT_BASE:
//                break;
//            default:
//                break;
//        }
//    }

    /**
     * 事件订阅者可以选择实现的方法之一
     * 如果使用onEvent作为订阅函数，那么该事件在哪个线程发布出来的，
     * onEvent就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程。
     * 使用这个方法时，在onEvent方法中不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。
     * @param action
     */
//    public void onEvent(EventAction action)
//    {
//
//    }

    /**
     * 事件订阅者可以选择实现的方法之一
     * 如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，
     * 那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，
     * 那么onEventBackground函数直接在该子线程中执行。
     * @param action
     */
//    public void onEventBackgroundThread(EventAction action)
//    {
//
//    }

    /**
     * 事件订阅者可以选择实现的方法之一
     * 使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行onEventAsync.
     * @param action
     */
//    public void onEventAsync(EventAction action)
//    {
//
//    }
}
