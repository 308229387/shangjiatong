package com.android.gmacs.utils;

import com.common.gmacs.parse.message.Message;
import com.xxganji.gmacs.proto.CommonPB;

/**
 * Created by linyueyang on 17/2/20.
 */

public class CustomMessage {

    public Message.MessageUserInfo mSenderInfo;
    public Message.MessageUserInfo mReceiverInfo;
    public int mTalkType;
    public long mId;
    public boolean isDeleted;
    public boolean isTalkDeleted;
    public long mLinkMsgId;
    public boolean hasSetShowTime;
    public boolean shouldShowTime;
    public boolean shouldHideUnreadCount;
    public boolean shouldHideOnTalkList;


    public long mMsgUpdateTime;
    public String mContentType;
    public CommonPB.SendStatus sendStatus;
    public CommonPB.ReadStatus readStatus;
    public CommonPB.PlayStatus playStatus;
    public boolean mIsSelfSendMsg;
    public String refer;


    public String mMsg;
    public String mUrl;
    public String mWidth;
    public String mHeight;
    public int progress;
    public String mType;

    public Message.MessageUserInfo getmSenderInfo() {
        return mSenderInfo;
    }

    public void setmSenderInfo(Message.MessageUserInfo mSenderInfo) {
        this.mSenderInfo = mSenderInfo;
    }

    public Message.MessageUserInfo getmReceiverInfo() {
        return mReceiverInfo;
    }

    public void setmReceiverInfo(Message.MessageUserInfo mReceiverInfo) {
        this.mReceiverInfo = mReceiverInfo;
    }

    public int getmTalkType() {
        return mTalkType;
    }

    public void setmTalkType(int mTalkType) {
        this.mTalkType = mTalkType;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isTalkDeleted() {
        return isTalkDeleted;
    }

    public void setTalkDeleted(boolean talkDeleted) {
        isTalkDeleted = talkDeleted;
    }

    public long getmLinkMsgId() {
        return mLinkMsgId;
    }

    public void setmLinkMsgId(long mLinkMsgId) {
        this.mLinkMsgId = mLinkMsgId;
    }

    public boolean isHasSetShowTime() {
        return hasSetShowTime;
    }

    public void setHasSetShowTime(boolean hasSetShowTime) {
        this.hasSetShowTime = hasSetShowTime;
    }

    public boolean isShouldShowTime() {
        return shouldShowTime;
    }

    public void setShouldShowTime(boolean shouldShowTime) {
        this.shouldShowTime = shouldShowTime;
    }

    public boolean isShouldHideUnreadCount() {
        return shouldHideUnreadCount;
    }

    public void setShouldHideUnreadCount(boolean shouldHideUnreadCount) {
        this.shouldHideUnreadCount = shouldHideUnreadCount;
    }

    public boolean isShouldHideOnTalkList() {
        return shouldHideOnTalkList;
    }

    public void setShouldHideOnTalkList(boolean shouldHideOnTalkList) {
        this.shouldHideOnTalkList = shouldHideOnTalkList;
    }

    public long getmMsgUpdateTime() {
        return mMsgUpdateTime;
    }

    public void setmMsgUpdateTime(long mMsgUpdateTime) {
        this.mMsgUpdateTime = mMsgUpdateTime;
    }

    public String getmContentType() {
        return mContentType;
    }

    public void setmContentType(String mContentType) {
        this.mContentType = mContentType;
    }

    public CommonPB.SendStatus getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(CommonPB.SendStatus sendStatus) {
        this.sendStatus = sendStatus;
    }

    public CommonPB.ReadStatus getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(CommonPB.ReadStatus readStatus) {
        this.readStatus = readStatus;
    }

    public CommonPB.PlayStatus getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(CommonPB.PlayStatus playStatus) {
        this.playStatus = playStatus;
    }

    public boolean ismIsSelfSendMsg() {
        return mIsSelfSendMsg;
    }

    public void setmIsSelfSendMsg(boolean mIsSelfSendMsg) {
        this.mIsSelfSendMsg = mIsSelfSendMsg;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getmMsg() {
        return mMsg;
    }

    public void setmMsg(String mMsg) {
        this.mMsg = mMsg;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmWidth() {
        return mWidth;
    }

    public void setmWidth(String mWidth) {
        this.mWidth = mWidth;
    }

    public String getmHeight() {
        return mHeight;
    }

    public void setmHeight(String mHeight) {
        this.mHeight = mHeight;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getmType() {
        return mType;
    }

    public void setmType(String mType) {
        this.mType = mType;
    }
}
