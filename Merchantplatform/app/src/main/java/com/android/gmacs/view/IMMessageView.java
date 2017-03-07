package com.android.gmacs.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.adapter.GmacsChatAdapter;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.utils.GLog;


/**
 * 声音，图片，文本等消息view的父view
 */
public abstract class IMMessageView implements OnClickListener {

    protected String TAG = this.getClass().getSimpleName();
    protected GmacsChatAdapter mAdapter;
    protected GmacsChatActivity mChatActivity;
    protected int mPosition;
    protected View mContentView;

    private IMMessage mIMMsg;

    int type;

    /**
     * 模板方法
     *
     * @param msg
     * @param parentView
     * @param inflater
     * @param position
     * @param adapter
     * @param activity
     */
    public View createIMView(IMMessage msg, ViewGroup parentView, LayoutInflater inflater, int position, GmacsChatAdapter adapter, GmacsChatActivity activity, int type) {
        init(position, adapter, activity, msg);
        parentView.addView(initView(inflater));
        mContentView.setTag(this);
        this.type = type;
        return mContentView;
    }

    public void init(final int position, GmacsChatAdapter adapter, GmacsChatActivity activity, IMMessage msg) {
        mAdapter = adapter;
        mChatActivity = activity;
        mPosition = position;
        mIMMsg = msg;
        setIMMessage(msg);
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * 初始化一个消息view，由子类来实现
     *
     * @param inflater
     * @return
     */
    protected View initView(LayoutInflater inflater) {
        return mContentView;
    }


    @Override
    public void onClick(View v) {
    }

    /**
     * 为view设置数据
     */
    public void setDataForView() {

    }

    /**
     * 删除一条消息
     */
    protected void deleteIMMessageView() {
        if (mIMMsg == null) {
            return;
        }
        GLog.d("mIMMsg----", mIMMsg + "");
        mAdapter.deleteMsg(mPosition);
    }

    protected abstract void setIMMessage(IMMessage msg);


}
