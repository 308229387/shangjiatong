package com.android.gmacs.msg.view;


import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.MsgContentType;

/**
 * View工厂模式
 * 根据消息类型创建相应的对象
 */

public class IMViewFactory {

    public IMMessageView createItemView(IMMessage msg) {
        IMMessageView resultView = null;
        switch(msg.mType) {
            case MsgContentType.TYPE_TEXT:
                resultView = new IMTextMsgView();
                break;
            case MsgContentType.TYPE_IMAGE:
                resultView = new IMImageMsgView();
                break;
            case MsgContentType.TYPE_AUDIO:
                resultView = new IMAudioMsgView();
                break;
            case MsgContentType.TYPE_LOCATION:
                resultView = new IMLocationMsgView();
                break;
            case MsgContentType.TYPE_TIP:
                resultView = new IMTipMsgView();
                break;
            case MsgContentType.TYPE_GIF:
                resultView = new IMGifMsgView();
        }
        return resultView;
    }

}
