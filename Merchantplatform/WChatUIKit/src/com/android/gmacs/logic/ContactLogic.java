package com.android.gmacs.logic;

import com.android.gmacs.event.AddContactMsgEvent;
import com.android.gmacs.event.ContactReportEvent;
import com.android.gmacs.event.ContactsEvent;
import com.android.gmacs.event.DeleteContactEvent;
import com.android.gmacs.event.PublicAccountListEvent;
import com.android.gmacs.event.RemarkEvent;
import com.android.gmacs.event.StarEvent;
import com.android.gmacs.event.UpdateSelfInfoEvent;
import com.common.gmacs.core.ContactsManager;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.RecentTalkManager;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.parse.contact.GmacsUser;
import com.common.gmacs.parse.contact.Remark;
import com.common.gmacs.parse.message.GmacsUserInfo;
import com.common.gmacs.parse.pubcontact.PAFunctionConfig;
import com.common.gmacs.parse.pubcontact.PublicContactInfo;
import com.common.gmacs.parse.talk.Talk;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by zhangxiaoshuang on 2015/12/18.
 */
public class ContactLogic extends BaseLogic implements ContactsManager.ContactsChangeCb {

    private volatile static ContactLogic ourInstance;

    public static ContactLogic getInstance() {
        if (null == ourInstance) {
            synchronized (ContactLogic.class) {
                if (null == ourInstance) {
                    ourInstance = new ContactLogic();
                }
            }
        }
        return ourInstance;
    }

    private ContactLogic() {
    }

    @Override
    public void init() {
        super.init();
        ContactsManager.getInstance().regContactsChangeCb(this);
        getContacts();
    }

    /**
     * 获取联系人
     */
    public void getContacts() {
        ContactsManager.getInstance().getContactsAsync(new ContactsManager.GetContactsCb() {
            @Override
            public void done(int errorCode, String errorMessage, List<Contact> contacts, List<Contact> stars) {
                if (errorCode != 0) {
                    EventBus.getDefault().post(errorMessage);
                } else {
                    EventBus.getDefault().post(new ContactsEvent(contacts, stars));
                }
            }
        });
    }

    /**
     * 删除联系人
     */
    public void delContact(String contactId, int contactSource) {
        ContactsManager.getInstance().delContactAsync(contactId, contactSource, new ContactsManager.DelContactCb() {
            @Override
            public void done(int errorCode, String errorMessage, String contactId, int contactSource) {
                if (errorCode != 0) {
                    EventBus.getDefault().post(errorMessage);
                } else {
                    EventBus.getDefault().post(new DeleteContactEvent(contactId, contactSource));
                }
            }
        });
    }

    /**
     * 添加联系人
     */
    public void addContact(String contactId, int contactSource) {
        ContactsManager.getInstance().addContactAsync(contactId, contactSource, new ContactsManager.AddContactCb() {
            @Override
            public void done(int errorCode, String errorMessage, String contactId, int contactSource) {
                if (errorCode != 0) {
                    EventBus.getDefault().post(errorMessage);
                } else {
                    EventBus.getDefault().post(new AddContactMsgEvent(contactId,contactSource));
                }
            }
        });
    }

    /**
     * 星标用户
     */
    public void star(String contactId, int contactSource) {
        ContactsManager.getInstance().starAsync(contactId, contactSource, new ContactsManager.StarCb() {
            @Override
            public void done(int errorCode, String errorMessage, String contactId, int contactSource) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(new StarEvent(contactId, contactSource, true));
                    ContactsManager.getInstance().getContactsAsync(null);
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * 取消星标用户
     */
    public void unStar(String contactId, int contactSource) {
        ContactsManager.getInstance().unStarAsync(contactId, contactSource, new ContactsManager.UnStarCb() {
            @Override
            public void done(int errorCode, String errorMessage, String contactId, int contactSource) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(new StarEvent(contactId, contactSource, false));
                    ContactsManager.getInstance().getContactsAsync(null);
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * 获取联系人信息
     *
     * @param userId
     * @param userSource
     */
    public void getUserInfo(String userId, int userSource) {
        ContactsManager.getInstance().getUserInfoAsync(userId, userSource, new ContactsManager.GetUserInfoCb() {
            @Override
            public void done(int errorCode, String errorMessage, Contact contact, String userId, int userSource) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(contact);
                    if (userId.equals(GmacsUser.getInstance().getUserId()) && userSource == GmacsUser.getInstance().getSource()) {
                        Gmacs.getInstance().setGmacsUserInfo(GmacsUserInfo.getUserInfoFromContact(contact));
                        EventBus.getDefault().post(new UpdateSelfInfoEvent());
                    }
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * 联系人姓名备注
     *
     * @param targetId
     * @param targetSource
     * @param guestName
     * @param postData
     */
    public void remark(String targetId, int targetSource, String guestName, final Remark postData) {
        ContactsManager.getInstance().remarkAsync(targetId, targetSource, guestName, postData, new ContactsManager.RemarkCb() {
            @Override
            public void done(int errorCode, String errorMessage, String targetId, int targetSource) {
                if (errorCode == 0) {
                    Remark finalPostData;
                    if (postData == null) {
                        finalPostData = new Remark();
                    } else {
                        finalPostData = postData;
                    }
                    EventBus.getDefault().post(new RemarkEvent(targetId, targetSource, finalPostData));
                    RecentTalkManager.getInstance().updateRemark(Talk.getTalkId(targetSource, targetId), finalPostData);
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }

        });
    }

    /**
     * 公众号列表
     */
    public void getPublicAccount(int targetSource) {
        ContactsManager.getInstance().getPublicAccountListAsync(targetSource, new ContactsManager.GetPublicAccountListCb() {
            @Override
            public void done(int errorCode, String errorMessage, List<PublicContactInfo> pubs) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(new PublicAccountListEvent(pubs));
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * Public account function config.
     */
    public void getPAFunctionConfig(String targetId, int targetSource) {
        ContactsManager.getInstance().getPAFunctionConfAsync(targetId, targetSource, new ContactsManager.GetPAFunctionConfCb() {
            @Override
            public void done(int errorCode, String errorMessage, String menuData, String targetId, int targetSource) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(PAFunctionConfig.buildPAFunctionConfig(menuData));
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    /**
     * 举报联系人
     *
     * @param reportId
     * @param reportSource
     * @param reportInfo
     */
    public void reportUser(final String reportId, int reportSource, String reportInfo) {
        ContactsManager.getInstance().reportUserAsync(reportId, reportSource, reportInfo, new ContactsManager.ReportUserCb() {
            @Override
            public void done(int errorCode, String errorMessage, boolean result, String text, String reportId, int reportSource) {
                if (errorCode == 0) {
                    EventBus.getDefault().post(new ContactReportEvent());
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }
        });
    }

    public void remarkLocal(String targetId, int targetSource, String guestName, final Remark postData) {
        ContactsManager.getInstance().remarkLocalAsync(targetId, targetSource, guestName, postData, new ContactsManager.RemarkLocalCb() {
            @Override
            public void done(int errorCode, String errorMessage, String targetId, int targetSource) {
                if (errorCode == 0) {
                    Remark finalPostData;
                    if (postData == null) {
                        finalPostData = new Remark();
                    } else {
                        finalPostData = postData;
                    }
                    EventBus.getDefault().post(new RemarkEvent(targetId, targetSource, finalPostData));
                    RecentTalkManager.getInstance().updateRemark(Talk.getTalkId(targetSource, targetId), finalPostData);
                } else {
                    EventBus.getDefault().post(errorMessage);
                }
            }

        });
    }

    @Override
    public void destroy() {
        super.destroy();
        ContactsManager.getInstance().unRegContactsChangeCb(this);
        EventBus.getDefault().post(new ContactsEvent(null, null));
    }

    @Override
    public void onContactsChanged(List<Contact> contacts, List<Contact> stars) {
        EventBus.getDefault().post(new ContactsEvent(contacts, stars));
        getUserInfo(GmacsUser.getInstance().getUserId(), GmacsUser.getInstance().getSource());
    }
}