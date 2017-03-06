package com.android.gmacs.logic;

import android.app.Application;
import android.app.Notification;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.Utils.Urls;
import com.Utils.UserUtils;
import com.Utils.eventbus.IMCustomChangeEvent;
import com.android.gmacs.event.LoadHistoryMessagesEvent;
import com.android.gmacs.utils.CustomMessageUtil;
import com.bean.BindStaffResponce;
import com.callback.DialogCallback;
import com.callback.JsonCallback;
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
import com.merchantplatform.application.HyApplication;
import com.okhttputils.OkHttpUtils;
import com.okhttputils.callback.AbsCallback;
import com.xxganji.gmacs.proto.CommonPB;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

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
    public void msgRecved(final Message msg) {
        Log.i("stuff", "收到消息-" + msg.mSenderInfo.mUserId);
        boolean delay = false;//delay是否需要延迟发送消息

        //专属客服消息单独处理
        if (msg.mSenderInfo.mUserSource == 8) {
            Log.i("stuff", "收到专属客服消息-" + msg.mSenderInfo.mUserId);
            final String spUserId = UserUtils.getCustomId(context);//获取当前客服Id
            //判断是不是专属客服变更消息
            if (TextUtils.isEmpty(spUserId) || spUserId.equals("0") || !msg.mSenderInfo.mUserId.equals(spUserId)) {
                Log.i("stuff", "收到专属客服切换消息-" + msg.mSenderInfo.mUserId);
                delay = true;//专属客服消息需要延迟
                //发送确认请求判断是否真正变更
                OkHttpUtils.get(Urls.GLOBAL_BINDSTAFF).execute(new JsonCallback<BindStaffResponce>() {
                    @Override
                    public void onResponse(boolean isFromCache, BindStaffResponce bindStaffResponce, Request request, @Nullable Response response) {
                        //判断消息体是否为空
                        if (null != bindStaffResponce && null != bindStaffResponce.getData()) {
                            String stuffId = bindStaffResponce.getData().getStaffId();
                            Log.i("stuff", "收到专属客服确认消息-" + stuffId);
                            //最终专属客服的变更都已服务端返回为准，判断是否确认变更
                            if (!TextUtils.isEmpty(stuffId) && !spUserId.equals(stuffId)) {
                                Log.i("stuff", "更新专属客服Id-" + stuffId);
                                UserUtils.setCustomId(context, stuffId);//更新本地客服账号
                                EventBus.getDefault().post(new IMCustomChangeEvent(msg.mSenderInfo));//发送客服变更消息Event
                                EventBus.getDefault().post(msg);//发送新消息Event
                            }
                        }
                    }
                });
            }
            //消息入私有库 除了取消绑定消息
            if (!msg.mSenderInfo.mUserId.equals("0")) {
                IMMessageEntity imMessageEntity = CustomMessageUtil.originalToEntity(msg);
                IMMessageDaoOperate.insertOrReplace(imMessageEntity);
            }
        }

        if (!delay)
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
