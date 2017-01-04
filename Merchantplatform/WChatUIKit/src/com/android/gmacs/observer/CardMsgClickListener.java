package com.android.gmacs.observer;

import com.common.gmacs.msg.IMMessage;

/**
 * Created by caotongjun on 2016/1/13.
 */
public interface CardMsgClickListener {
    /**
     * 供宿程序实现消息点击事件
     */
    void onCardMsgClick(String contentType, IMMessage imMessage, String url, String title);
}
