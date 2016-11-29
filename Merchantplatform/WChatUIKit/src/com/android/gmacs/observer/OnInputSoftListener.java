package com.android.gmacs.observer;

/**
 * 软键盘弹出时回调函数
 */

public interface OnInputSoftListener {

    // 软键盘显示的回调方法
    void onShow();

    // 软键盘隐藏时的回调方法
    void onHide();
}
