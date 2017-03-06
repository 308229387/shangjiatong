package com.android.gmacs.utils;

import android.text.TextUtils;
import android.util.Log;

import com.common.gmacs.core.MessageManager;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.MsgContentType;
import com.common.gmacs.msg.data.IMImageMsg;
import com.common.gmacs.msg.data.IMTextMsg;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.message.MessageDetail;
import com.common.gmacs.utils.BitmapUtil;
import com.db.dao.IMMessageEntity;
import com.db.helper.IMMessageDaoOperate;
import com.google.gson.Gson;
import com.xxganji.gmacs.proto.CommonPB;

import java.util.Date;

/**
 * Created by linyueyang on 17/2/20.
 */

public class CustomMessageUtil {

    public static Message entityToOriginal(IMMessageEntity imMessageEntity) {
        return customToOriginal(entityToCustom(imMessageEntity));
    }

    public static IMMessageEntity originalToEntity(Message message) {
        return customToEntity(originalToCustom(message));
    }

    public static CustomMessage entityToCustom(IMMessageEntity imMessageEntity) {
        Gson gson = new Gson();
        return gson.fromJson(imMessageEntity.getInfo(), CustomMessage.class);

    }

    public static IMMessageEntity customToEntity(CustomMessage customMessage) {
        Gson gson = new Gson();
        String info = gson.toJson(customMessage);
        Log.i("接收到客服消息准备入库", info);
        IMMessageEntity imMessageEntity = new IMMessageEntity();
        imMessageEntity.setSenderId(customMessage.mSenderInfo.mUserId);
        imMessageEntity.setReceiverId(customMessage.mReceiverInfo.mUserId);
        imMessageEntity.setInfo(info);
        imMessageEntity.setTimestamp(customMessage.mMsgUpdateTime);
        imMessageEntity.setIsReaded(false);
        return imMessageEntity;
    }


    public static Message customToOriginal(CustomMessage customMessage) {
        Message msg = new Message();

        IMMessage imMessage = null;
        if (null != customMessage.mType) {
            if (customMessage.mType.equals(MsgContentType.TYPE_TEXT)) {
                imMessage = new IMTextMsg();
                ((IMTextMsg) imMessage).mMsg = customMessage.mMsg;
            } else if (customMessage.mType.equals(MsgContentType.TYPE_IMAGE)) {
                imMessage = new IMImageMsg();
                ((IMImageMsg) imMessage).mUrl = customMessage.mUrl;
                ((IMImageMsg) imMessage).mWidth = customMessage.mWidth;
                ((IMImageMsg) imMessage).mHeight = customMessage.mHeight;
                ((IMImageMsg) imMessage).progress = customMessage.progress;
                ((IMImageMsg) imMessage).mType = customMessage.mType;
            }
        }

        MessageDetail messageDetail = new MessageDetail(msg);
        messageDetail.setMsgContent(imMessage);
        messageDetail.mMsgUpdateTime = customMessage.mMsgUpdateTime;
        messageDetail.setMsgSendStatus(customMessage.sendStatus);
        messageDetail.setMsgReadStatus(customMessage.readStatus);
        messageDetail.setMsgPlayStatus(customMessage.playStatus);
        messageDetail.mIsSelfSendMsg = customMessage.mIsSelfSendMsg;
        messageDetail.setRefer(customMessage.refer);


        msg.mMsgDetail = messageDetail;
        msg.mSenderInfo = customMessage.mSenderInfo;
        msg.mReceiverInfo = customMessage.mReceiverInfo;
        msg.mTalkType = customMessage.mTalkType;
        msg.mId = customMessage.mId;
        msg.isDeleted = customMessage.isDeleted;
        msg.isTalkDeleted = customMessage.isTalkDeleted;
        msg.mLinkMsgId = customMessage.mLinkMsgId;
        msg.hasSetShowTime = customMessage.hasSetShowTime;
        msg.shouldShowTime = customMessage.shouldShowTime;
        msg.shouldHideUnreadCount = customMessage.shouldHideUnreadCount;
        msg.shouldHideOnTalkList = customMessage.shouldHideOnTalkList;

        msg.mMsgDetail.mMessage = msg;
        if (imMessage != null)
            msg.mMsgDetail.getmMsgContent().parentMsg = messageDetail;
        return msg;
    }

    public static CustomMessage originalToCustom(Message msg) {

        CustomMessage customMessage = new CustomMessage();

        customMessage.mSenderInfo = msg.mSenderInfo;
        customMessage.mReceiverInfo = msg.mReceiverInfo;
        customMessage.mTalkType = msg.mTalkType;
        customMessage.mId = msg.mId;
        customMessage.isDeleted = msg.isDeleted;
        customMessage.isTalkDeleted = msg.isTalkDeleted;
        customMessage.mLinkMsgId = msg.mLinkMsgId;
        customMessage.hasSetShowTime = msg.hasSetShowTime;
        customMessage.shouldShowTime = msg.shouldShowTime;
        customMessage.shouldHideUnreadCount = msg.shouldHideUnreadCount;
        customMessage.shouldHideOnTalkList = msg.shouldHideOnTalkList;


        customMessage.mMsgUpdateTime = msg.mMsgDetail.mMsgUpdateTime;
        customMessage.mContentType = msg.mMsgDetail.getmMsgContent().mType;
        customMessage.sendStatus = msg.mMsgDetail.getSendStatus();
        customMessage.readStatus = msg.mMsgDetail.getReadStatus();
        customMessage.playStatus = msg.mMsgDetail.getMsgPlayStatus();
        customMessage.mIsSelfSendMsg = msg.mMsgDetail.mIsSelfSendMsg;
        customMessage.refer = msg.mMsgDetail.getRefer();


        IMMessage imMessage = msg.mMsgDetail.getmMsgContent();
        customMessage.mType = imMessage.mType;
        if (imMessage.mType.equals(MsgContentType.TYPE_TEXT)) {
            customMessage.mMsg = ((IMTextMsg) imMessage).mMsg;
        }
        if (imMessage.mType.equals(MsgContentType.TYPE_IMAGE)) {
            customMessage.mUrl = ((IMImageMsg) imMessage).mUrl;
            customMessage.mWidth = ((IMImageMsg) imMessage).mWidth;
            customMessage.mHeight = ((IMImageMsg) imMessage).mHeight;
            customMessage.progress = ((IMImageMsg) imMessage).progress;

        }

        return customMessage;
    }


    public static Message createMessage(int mTalkType, IMMessage imMessage, Message.MessageUserInfo messageUserInfo, MessageManager.SendIMMsgListener sendIMMsgListener) {
        Message message = new Message();
        message.mReceiverInfo = messageUserInfo;
        message.mMsgDetail = new MessageDetail(message);
        message.mMsgDetail.mIsSelfSendMsg = true;
        message.mMsgDetail.setMsgContent(imMessage);
        message.mSenderInfo = Message.MessageUserInfo.createLoginUserInfo();
        message.mTalkType = mTalkType;
        message.mSenderInfo.mTalkType = message.mTalkType;
        message.mMsgDetail.mMsgUpdateTime = new Date().getTime();
        if (sendIMMsgListener != null) {
            sendIMMsgListener.onPreSaveMessage(message);
        }

        return message;
    }


    public static void sendIMTextMsg(String msg, String ref, Message.MessageUserInfo messageUserInfo, MessageManager.SendIMMsgListener sendIMMsgListener) {
        if (msg != null && msg.trim() != "") {
            IMTextMsg imTextMsg = new IMTextMsg();
            imTextMsg.mMsg = msg;
            Message message = createMessage(messageUserInfo.mTalkType, imTextMsg, messageUserInfo, sendIMMsgListener);
            message.mMsgDetail.setRefer(ref);
            MessageManager.getInstance().sendIMMsg(message, sendIMMsgListener);

            //消息入库
            message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SENT);
            IMMessageEntity imMessageEntity = originalToEntity(message);
            IMMessageDaoOperate.insertOrReplace(imMessageEntity);
        }
    }

    public static void sendIMImageMsg(String ref, String filePath, boolean sendRawImage, Message.MessageUserInfo messageUserInfo, MessageManager.SendIMMsgListener sendIMMsgListener) {
        if (!TextUtils.isEmpty(filePath)) {
            IMImageMsg imImageMsg = new IMImageMsg();
            if (sendRawImage) {
                imImageMsg.mUrl = filePath;
            } else {
                imImageMsg.mUrl = BitmapUtil.compressImage(filePath);
            }

            int[] imageSize = BitmapUtil.imageSize(imImageMsg.mUrl);
            imImageMsg.mWidth = String.valueOf(imageSize[0]);
            imImageMsg.mHeight = String.valueOf(imageSize[1]);

            Message message = createMessage(messageUserInfo.mTalkType, imImageMsg, messageUserInfo, sendIMMsgListener);
            message.mMsgDetail.setRefer(ref);
            MessageManager.getInstance().sendIMMsg(message, sendIMMsgListener);

            //消息入库
            message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SENT);
            IMMessageEntity imMessageEntity = originalToEntity(message);
            IMMessageDaoOperate.insertOrReplace(imMessageEntity);
        }
    }

}
