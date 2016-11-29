package com.android.gmacs.logic;

import com.android.gmacs.event.RecentTalksEvent;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.MessageManager;
import com.common.gmacs.core.RecentTalkManager;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.utils.GLog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by caotongjun on 2015/12/15.
 */
public class TalkLogic extends BaseLogic implements RecentTalkManager.TalkChangeListener {

    private static volatile TalkLogic ourInstance;

    public static TalkLogic getInstance() {
        if (null == ourInstance) {
            synchronized (TalkLogic.class) {
                if (null == ourInstance) {
                    ourInstance = new TalkLogic();
                }
            }
        }
        return ourInstance;
    }

    private final List<Integer> msgTypeList = new ArrayList<>();

    private TalkLogic() {
        msgTypeList.add(Gmacs.TalkType.TALKETYPE_SYSTEM.getValue());
        msgTypeList.add(Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
        msgTypeList.add(Gmacs.TalkType.TALKETYPE_OFFICIAL.getValue());
        msgTypeList.add(Gmacs.TalkType.TALKETYPE_POSTINGS.getValue());
    }

    @Override
    public void init() {
        super.init();
        RecentTalkManager.getInstance().registerTalkListChangeListener(this);
        MessageManager.getInstance().addSendIMMsgListener(RecentTalkManager.getInstance());
        syncRecentTalks();
    }

    /**
     * 根据类型获取最近会话
     * @param msgTypeList
     */
    public void getRecentTalks(List<Integer> msgTypeList) {
        RecentTalkManager.getInstance().getTalkByMsgTypeAsync(msgTypeList,
                new RecentTalkManager.GetTalkByMsgTypeCb() {
                    @Override
                    public synchronized void done(int errorCode, String errorMessage, List<Talk> talks) {
                        if (errorCode != 0) {
                            EventBus.getDefault().post(errorMessage);
                        } else {
                            EventBus.getDefault().post(new RecentTalksEvent(talks));
                        }
                    }
                });

    }

    public void getRecentTalks() {
        GLog.d(TAG, "getRecentTalks");
        getRecentTalks(msgTypeList);
    }

    /**
     * 与服务器同步会话列表
     */
    public void syncRecentTalks(List<Integer> msgTypeList) {
        RecentTalkManager.getInstance().syncTalkListByMsgTypeAsync(msgTypeList);
    }

    public void syncRecentTalks() {
        GLog.d(TAG, "syncRecentTalks");
        syncRecentTalks(msgTypeList);
    }


    /**
     * 删除会话
     * @param otherId
     * @param otherSource
     */
    public void deleteTalk(String otherId, int otherSource) {
        RecentTalkManager.getInstance().deleteTalkByIdAsync(otherId,
                otherSource, new RecentTalkManager.ActionCb() {
                    @Override
                    public synchronized void done(int errorCode, String errorMessage) {
                        if (errorCode != 0) {
                            EventBus.getDefault().post(errorMessage);
                        }
                    }
                });
    }

    /**
     * 更新会话阅读状态
     * @param userId
     * @param userSource
     */
    public void updateTalkRead(String userId,int userSource) {
        RecentTalkManager.getInstance().updateTalkRead(userId, userSource);
    }

    @Override
    public void destroy() {
        super.destroy();
        RecentTalkManager.getInstance().unRegisterTalkListChangeListener(this);
        MessageManager.getInstance().removeSendIMMsgListener(RecentTalkManager.getInstance());
        EventBus.getDefault().post(new RecentTalksEvent(null));
    }

    @Override
    public void onTalkListChanged(List<Talk> talks) {
        EventBus.getDefault().post(new RecentTalksEvent(talks));
    }

    @Override
    public void onTalkListChanged() {
        getRecentTalks();
    }

}
