package com.android.gmacs.logic;

import android.app.Application;
import android.app.Notification;
import android.text.TextUtils;
import android.util.Log;

import com.Utils.UserUtils;
import com.Utils.eventbus.IMCustomChangeEvent;
import com.android.gmacs.utils.CustomMessage;
import com.android.gmacs.utils.CustomMessageUtil;
import com.android.gmacs.event.LoadHistoryMessagesEvent;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.ContactsManager;
import com.common.gmacs.core.MessageManager;
import com.common.gmacs.core.RecentTalkManager;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.parse.message.GmacsUserInfo;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.TalkType;
import com.common.gmacs.utils.GmacsUtils;
import com.db.dao.IMMessageEntity;
import com.db.helper.IMMessageDaoOperate;
import com.google.gson.Gson;
import com.xxganji.gmacs.proto.CommonPB;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caotongjun on 2015/11/26.
 */
public class MessageLogic extends BaseLogic implements MessageManager.RecvMsgListener {

    private static volatile MessageLogic ourInstance;

    private static Application context;

    public static void init(Application application) {
        context = application;
    }

    public Application getApplication() {
        return context;
    }

    public static MessageLogic getInstance() {
        if (null == ourInstance) {
            synchronized (MessageLogic.class) {
                if (null == ourInstance) {
                    ourInstance = new MessageLogic();
                }
            }
        }
        return ourInstance;
    }

    private MessageLogic() {

    }

    /**
     * 加载聊天消息记录
     */
    public void getHistoryMessages(String otherId, int otherSource, long beginMsgId, int count) {
        MessageManager.getInstance().getHistoryAsync(otherId, otherSource, beginMsgId, count, new MessageManager.GetHistoryMsgCb() {
            @Override
            public void done(int errorCode, String errorMessage, List<Message> msgList) {
                EventBus.getDefault().post(new LoadHistoryMessagesEvent(msgList));
                if (errorCode != 0) {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * 删除消息
     */
    public void deleteMsgByMsgIds(long[] msgIdList) {
        if (msgIdList == null || msgIdList.length == 0) {
            return;
        }
        MessageManager.getInstance().deleteByMsgIdAsync(msgIdList, new MessageManager.ActionCb() {
            @Override
            public void done(int errorCode, String errorMessage) {
                if (errorCode != 0) {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });

    }

    /**
     * 更新消息播放状态
     */
    public void updatePlayStatusByMsgId(long msgId, int newStatus) {
        ArrayList<Long> msgIds = new ArrayList<>();
        msgIds.add(msgId);
        MessageManager.getInstance().updatePlayStatusBatchByMsgIdAsync(msgIds, newStatus, null);
    }

    @Override
    public void init() {
        super.init();
        MessageManager.getInstance().regRecvMsgListener(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        MessageManager.getInstance().unRegRecvMsgListener(this);
    }

    @Override
    public void msgRecved(Message msg) {
        //TODO：Penta接收到的消息判断是否是客服消息
        //if (msg.mSenderInfo.mUserSource == 8) {
        if (true) {
            String spUserId = UserUtils.getCustomId(context);
            if (TextUtils.isEmpty(spUserId) || !msg.mSenderInfo.mUserId.equals(spUserId)) {

                //TODO:服务端确认是否变更
                //发送客服变更消息Event
                UserUtils.setCustomId(context, msg.mSenderInfo.mUserId);
                EventBus.getDefault().post(new IMCustomChangeEvent(msg.mSenderInfo));
            }

            //消息入私有库
            IMMessageEntity imMessageEntity = CustomMessageUtil.originalToEntity(msg);
            IMMessageDaoOperate.insertOrReplace(imMessageEntity);
        }

        //发送接收消息EventBus
        EventBus.getDefault().post(msg);
        if (msg.mMsgDetail.getmMsgContent().isNotice() && notifyHelper != null) {
            // 排除当前正进行的会话
            boolean isNotify = !(RecentTalkManager.getInstance().isBelongTalking(msg)
                    && RecentTalkManager.getInstance().isTalking());
            boolean read = msg.mMsgDetail == null
                    || CommonPB.ReadStatus.MSG_READ == msg.mMsgDetail.getReadStatus();
            long loginTime = ClientManager.getInstance().getLoginTimeStamp();
            if (isNotify && !read && msg.mMsgDetail.mMsgUpdateTime >= loginTime) {
                notifyHelper.showNotify(msg);
            }
        }
    }

    public abstract static class NotifyHelper {

        /**
         * 把消息在状态栏里显示，具体由子类来实现
         *
         * @param msg
         */
        protected void showNotify(final Message msg) {
            if (msg == null) {
                return;
            }
            boolean dataValid = msg.mMsgDetail != null
                    && msg.mMsgDetail.getmMsgContent() != null;
            if (!dataValid) {
                return;
            }
            final Message.MessageUserInfo info = msg.getTalkOtherUserInfo();
            if (info != null) {
                if (info.gmacsUserInfo == null || TextUtils.isEmpty(info.gmacsUserInfo.userId)) {
                    if (!TalkType.isGroupTalk(msg)) {
                        ContactsManager.getInstance().getUserInfoAsync(info.mUserId, info.mUserSource,
                                new ContactsManager.GetUserInfoCb() {
                                    @Override
                                    public void done(int errorCode, String errorMessage, Contact contactInfo,
                                                     String userId, int userSource) {
                                        if (errorCode == 0 && contactInfo != null) {
                                            info.gmacsUserInfo = GmacsUserInfo.getUserInfoFromContact(contactInfo);
                                            GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    showMsgNotification(msg);
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                } else {
                    GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showMsgNotification(msg);
                        }
                    });
                }
            }
        }

        /**
         * 显示通知栏具体处理逻辑，具体由子类实现
         *
         * @param message
         */
        protected abstract void showMsgNotification(Message message);

        /**
         * 根据当前的提醒设置获取经过设定好了的notification，如果返回为null，说明当前不应该有通知栏提醒,具体由子类实现
         */
        protected abstract Notification configNotification(Message msg, Notification notification);

    }

    private NotifyHelper notifyHelper;

    public void setNotifyHelper(NotifyHelper notifyHelper) {
        this.notifyHelper = notifyHelper;
    }
}
