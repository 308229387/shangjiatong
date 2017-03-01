package com.android.gmacs.msg.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.merchantplatform.R;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.data.IMTipMsg;

/**
 * Created by zhangxiaoshuang on 2015/12/7.
 * tip消息视图
 */
public class IMTipMsgView extends IMMessageView{
    private IMTipMsg mTipMsg;
    private TextView mTvTip;

    @Override
    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMTipMsg) {
            mTipMsg = (IMTipMsg) msg;
        }

    }

    @Override
    protected View initView(LayoutInflater inflater) {
        mContentView=inflater.inflate(R.layout.gmacs_adapter_talk_item_tip_content,null);
        mTvTip=(TextView)mContentView.findViewById(R.id.tv_tip);
         super.initView(inflater);
        return mContentView;
    }

    @Override
    public void setDataForView() {
        if (mTipMsg != null) {
            mTvTip.setText(mTipMsg.mText);
        }
        super.setDataForView();
    }
}
