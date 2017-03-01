package com.android.gmacs.core;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.gmacs.adapter.ConversationListAdapter;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.core.ContactsManager;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.parse.message.GmacsUserInfo;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.parse.talk.TalkType;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsUtils;
import com.common.gmacs.utils.ImageUtil;
import com.xxganji.gmacs.Client;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;

/**
 * Created by zhaobing on 16/6/23.
 * 用户资料缓存管理类
 */
public class UserInfoCacheManager {
    private static final String TAG = UserInfoCacheManager.class.getSimpleName();
    private volatile static UserInfoCacheManager ourInstance;

    public static UserInfoCacheManager getInstance() {
        if (null == ourInstance) {
            synchronized (UserInfoCacheManager.class) {
                if (null == ourInstance) {
                    ourInstance = new UserInfoCacheManager();
                }
            }
        }
        return ourInstance;
    }

    private ConversationListAdapter adapter;

    public void registerConversationListAdapter(ConversationListAdapter adapter) {
        if (adapter == null) {
            return;
        }
        this.adapter = adapter;
    }

    public void unRegisterConversationListAdapter() {
        adapter = null;
    }

    private UserInfoCacheManager() {
    }

    public void updateUserInfoFromCache(final Message.MessageUserInfo messageUserInfo, final NetworkImageView imageView, final TextView textView) {
        if (null == messageUserInfo) {
            return;
        }
        Client.getInstance().post(new Runnable() {
            @Override
            public void run() {
                final GmacsUserInfo gmacsUserInfo = GmacsUserInfo.getUserInfoFromDBCache(messageUserInfo.mUserId, messageUserInfo.mUserSource);
                if (null == gmacsUserInfo) {
                    ContactsManager.getInstance().getUserInfoAsync(messageUserInfo.mUserId, messageUserInfo.mUserSource, new ContactsManager.GetUserInfoCb() {
                        @Override
                        public void done(int errorCode, String errorMessage, final Contact contactInfo, String userId, int userSource) {
                            if (errorCode == 0 && contactInfo != null) {
                                messageUserInfo.gmacsUserInfo = GmacsUserInfo.getUserInfoFromContact(contactInfo);

                                if (imageView != null && textView != null) {
                                    Message.MessageUserInfo userInfo = (Message.MessageUserInfo) imageView.getTag();
                                    if (userInfo != null && userInfo.mUserId.equals(userId) && userInfo.mUserSource == userSource) {
                                        GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageView.setImageUrl(ImageUtil.makeUpUrl(contactInfo.getAvatar(), IMG_RESIZE, IMG_RESIZE));
                                                String name = contactInfo.getUserName();
                                                if (TextUtils.isEmpty(name)) {
                                                    textView.setText(messageUserInfo.mUserId);
                                                } else {
                                                    textView.setText(name);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                } else {
                    messageUserInfo.gmacsUserInfo = gmacsUserInfo;

                    if (imageView != null && textView != null) {
                        Message.MessageUserInfo userInfo = (Message.MessageUserInfo) imageView.getTag();
                        if (userInfo != null && messageUserInfo.mUserId.equals(userInfo.mUserId) && userInfo.mUserSource == messageUserInfo.mUserSource) {
                            GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageUrl(ImageUtil.makeUpUrl(gmacsUserInfo.avatar, IMG_RESIZE, IMG_RESIZE));
                                    String name = gmacsUserInfo.userName;
                                    if (TextUtils.isEmpty(name)) {
                                        textView.setText(messageUserInfo.mUserId);
                                    } else {
                                        textView.setText(name);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void updateTalkFromCache(final Talk talk, final NetworkImageView imageView, final TextView textView, final View otherInfoLayout) {
        if (null == talk) {
            return;
        }
        GLog.d(TAG, "updateTalkFromCache" + talk);
        Client.getInstance().post(new Runnable() {
            @Override
            public void run() {
                if (!talk.getTalkFromDBCache(talk.mTalkOtherUserId, talk.mTalkOtherUserSource)) {
                    if (!TalkType.isGroupTalk(talk)){
                        ContactsManager.getInstance().getUserInfoAsync(talk.mTalkOtherUserId, talk.mTalkOtherUserSource, new ContactsManager.GetUserInfoCb() {
                            @Override
                            public void done(int errorCode, String errorMessage, final Contact contactInfo, String userId, int userSource) {
                                if (errorCode == 0 && contactInfo != null) {
                                    talk.mTalkOtherUserAvatar = contactInfo.getAvatar();
                                    talk.mTalkOtherGender = contactInfo.getGender();
                                    talk.mTalkOtherUserType = contactInfo.getUserType();
                                    talk.mTalkOtherUserName = contactInfo.getUserName();
                                    talk.mTalkOtherUserRemark = contactInfo.remark;
                                    if (imageView != null && textView != null) {
                                        final Message.MessageUserInfo userInfo = (Message.MessageUserInfo) imageView.getTag();
                                        if (userInfo != null && userInfo.mUserId.equals(userId) && userInfo.mUserSource == userSource) {
                                            if (userInfo.gmacsUserInfo == null) {
                                                userInfo.gmacsUserInfo = GmacsUserInfo.getUserInfoFromTalk(talk);
                                            }
                                            GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    imageView.setImageUrl(ImageUtil.makeUpUrl(userInfo.gmacsUserInfo.avatar, IMG_RESIZE, IMG_RESIZE));
                                                    String name = talk.mTalkOtherUserRemark.remark_name;
                                                    if (TextUtils.isEmpty(name)) {
                                                        name = userInfo.gmacsUserInfo.userName;
                                                    }
                                                    if (TextUtils.isEmpty(name)) {
                                                        name = talk.mTalkOtherUserName;
                                                    }
                                                    if (TextUtils.isEmpty(name)) {
                                                        textView.setText(talk.mTalkOtherUserId);
                                                    } else {
                                                        textView.setText(name);
                                                    }
                                                    if (adapter != null) {
                                                        adapter.updateOtherInfoLayout(otherInfoLayout, talk);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });
                    }
                } else {
                    GmacsUserInfo gmacsUserInfo = GmacsUserInfo.getUserInfoFromTalk(talk);
                    if (talk.getmLastMessage() != null) {
                        talk.getmLastMessage().getTalkOtherUserInfo().gmacsUserInfo = gmacsUserInfo;
                    }
                    if (imageView != null && textView != null) {
                        Message.MessageUserInfo userInfo = (Message.MessageUserInfo) imageView.getTag();
                        if (userInfo != null && userInfo.mUserId.equals(talk.mTalkOtherUserId) && userInfo.mUserSource == talk.mTalkOtherUserSource) {
                            GmacsUtils.getInstance().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageUrl(ImageUtil.makeUpUrl(talk.mTalkOtherUserAvatar, IMG_RESIZE, IMG_RESIZE));
                                    String name = talk.mTalkOtherUserRemark.remark_name;
                                    if (TextUtils.isEmpty(name)) {
                                        name = talk.mTalkOtherUserName;
                                    }
                                    if (TextUtils.isEmpty(name)) {
                                        textView.setText(talk.mTalkOtherUserId);
                                    } else {
                                        textView.setText(name);
                                    }
                                    if (adapter != null) {
                                        adapter.updateOtherInfoLayout(otherInfoLayout, talk);
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}
