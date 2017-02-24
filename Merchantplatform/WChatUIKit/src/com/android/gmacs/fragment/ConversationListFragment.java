package com.android.gmacs.fragment;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils.JumpExtendAction;
import com.Utils.JumpSystemNotificationAction;
import com.Utils.ShowRedDotSystemNotificationAction;
import com.Utils.SystemGetNotificationInfoAction;
import com.Utils.SystemNotification;
import com.Utils.SystemNotificationInfoAction;
import com.Utils.UserUtils;
import com.Utils.eventbus.IMCustomChangeEvent;
import com.Utils.eventbus.IMDetailDestroyEvent;
import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.adapter.ConversationListAdapter;
import com.android.gmacs.utils.DateUtils;
import com.android.gmacs.utils.IMConstant;
import com.android.gmacs.utils.CustomMessage;
import com.android.gmacs.utils.CustomMessageUtil;
import com.android.gmacs.event.RecentTalksEvent;
import com.android.gmacs.logic.TalkLogic;
import com.android.gmacs.view.GmacsDialog;
import com.common.gmacs.core.ChannelManager;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.msg.MsgContentType;
import com.common.gmacs.msg.data.IMTextMsg;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.NetworkUtil;
import com.db.dao.IMMessageEntity;
import com.db.helper.IMMessageDaoOperate;
import com.google.gson.Gson;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YanQi on 2015/12/7.
 */
public class ConversationListFragment extends BaseFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, ClientManager.ConnectListener {

    protected ListView mListView;
    private ConversationListAdapter mTalkAdapter;
    private LinearLayout mHeaderView, mConnectionStatusHeaderViewContainer;
    private boolean isBackground;
    private TextView redDot;
    private TextView systemText;
    private boolean showRed = true;

    /**
     * 为其添加子视图, 在会话列表为空时, 可以显示友好提示
     */
    protected LinearLayout mTalkListEmptyPromptView;

    /**
     * Connection status bar
     */
    protected RelativeLayout systemHead;
    protected LinearLayout mConnectionStatusHeaderView;
    protected TextView mConnectionStatusTextView;
    protected ImageView mConnectionStatusImageView;

    protected ArrayList<Talk> mTalks = new ArrayList<>();
    private ImageView callPhone;
    private RelativeLayout extendHead;

    private TextView customTime;
    private TextView customTextView;
    private TextView customRedDot;
    Message.MessageUserInfo messageUserInfo;
    private View customView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.gmacs_conversation_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Add the child what you want in parent view mHeaderView, as headerView of ListView.
     * <br><b>Invoke this function in method <i>initView().</i></b></br>
     *
     * @return mHeaderView The parent view which has been added in ListView as its headerView.
     */
    protected LinearLayout getHeaderView() {
        return mHeaderView;
    }

    protected boolean showDisconnectedHeader() {
        return true;
    }

    @Override
    protected void initView() {
        mListView = (ListView) getView().findViewById(R.id.lv_conversation_list);
        mTalkListEmptyPromptView = (LinearLayout) getView().findViewById(R.id.ll_conversation_list_empty_prompt);
        callPhone = (ImageView) getView().findViewById(R.id.call_phone);
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_DY_KFDH);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + UserUtils.getStaffPhone(getActivity()));
                intent.setData(data);
                startActivity(intent);
            }
        });

        /** 创建ListViewHeader部分 start */

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        //IM连接状态栏
        if (showDisconnectedHeader()) {
            ClientManager.getInstance().regConnectListener(this);
            mConnectionStatusHeaderViewContainer = new LinearLayout(getActivity());
            mConnectionStatusHeaderViewContainer.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT));
            mConnectionStatusHeaderView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.gmacs_conversation_connection_status_header, mConnectionStatusHeaderViewContainer, false);

            mConnectionStatusTextView = (TextView) mConnectionStatusHeaderView.findViewById(R.id.gmacs_connection_status_text);
            mConnectionStatusImageView = (ImageView) mConnectionStatusHeaderView.findViewById(R.id.gmacs_connection_status_img);
            mConnectionStatusHeaderViewContainer.addView(mConnectionStatusHeaderView);
            showOrHideDisconnectHeader(ClientManager.getInstance().getConnectionStatus());
        }

        //系统通知
        View systemNotificationView = inflater.inflate(R.layout.system_notification_list_layout, null);
        systemHead = (RelativeLayout) systemNotificationView.findViewById(R.id.system_notification_layout);
        redDot = (TextView) systemHead.findViewById(R.id.tv_conversation_msg_count);
        systemText = (TextView) systemHead.findViewById(R.id.tv_conversation_msg_text);
        systemHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redDot.setVisibility(View.GONE);
                EventBus.getDefault().post(new JumpSystemNotificationAction("jump"));
            }
        });
        //专属客服
        customView = inflater.inflate(R.layout.im_custom_layout, null);
        customTextView = (TextView) customView.findViewById(R.id.tv_conversation_msg_text);
        customTime = (TextView) customView.findViewById(R.id.tv_conversation_msg_time);
        customRedDot = (TextView) customView.findViewById(R.id.tv_conversation_msg_count);
        refreshCustom();
        //推广助手
        View extendView = inflater.inflate(R.layout.extend_list_layout, null);
        extendHead = (RelativeLayout) extendView.findViewById(R.id.extend_layout);
        extendHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_XX_TGZS);
                EventBus.getDefault().post(new JumpExtendAction("jump"));

            }
        });
        mHeaderView = new LinearLayout(getActivity());
        mHeaderView.setOrientation(LinearLayout.VERTICAL);
        mHeaderView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        //HeaderView 加入各个子View
        if (null != mConnectionStatusHeaderViewContainer) {
            mHeaderView.addView(mConnectionStatusHeaderViewContainer);
        }
        mHeaderView.addView(systemNotificationView);
        mHeaderView.addView(customView);
        mHeaderView.addView(extendView);
        //给ListView添加头部
        mListView.addHeaderView(mHeaderView);
        /** 创建ListViewHeader部分 start */
    }


    @Subscribe
    public void onEvent(ShowRedDotSystemNotificationAction temp) {//显隐红点
        if (temp.getJump().equals("show"))
            showRed = true;
        else if (temp.getJump().equals("dismiss")) {
            showRed = false;
            redDot.setVisibility(View.GONE);
        }

    }

    @Subscribe
    public void onEvent(SystemNotification temp) {//来消息时更新
        systemText.setText(temp.getDescribe());
        if (showRed)
            redDot.setVisibility(View.VISIBLE);
        redDot.setText(temp.getIsReaded() + "");
    }

    @Subscribe
    public void onEvent(SystemNotificationInfoAction temp) {//进来时拉取
        systemText.setText(temp.getJump());
        if (temp.getRedDot() > 0 && showRed)
            redDot.setVisibility(View.VISIBLE);
        redDot.setText(temp.getRedDot() + "");
    }

    /**
     * 专属客服更新判断
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMCustomChangeEvent event) {
        //更新专属客服是否需要展示
        judgeHasCustom();
    }

    /**
     * IM聊天详情 退出事件，更新专属客服红点
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMDetailDestroyEvent event) {
        refreshCustom();
    }


    @Override
    protected void initData() {
        EventBus.getDefault().post(new SystemGetNotificationInfoAction("info"));
        mTalkAdapter = new ConversationListAdapter(this.getActivity(), mTalks);
        mListView.setAdapter(mTalkAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        EventBus.getDefault().register(this);
        TalkLogic.getInstance().getRecentTalks();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int realPosition = position - mListView.getHeaderViewsCount();
        if (realPosition < 0 || realPosition >= mTalks.size()) {
            return;
        }
        Talk talk = mTalks.get(realPosition);
        if (!onItemClickDelegate(talk)) {
            LogUmengAgent.ins().log(LogUmengEnum.LOG_XIAOXILBY_HHDJ);
            Intent intent = new Intent(GmacsUiUtil.createToChatActivity(getActivity(), "", talk.getmLastMessage().getTalkOtherUserInfo()));
            startActivity(intent);
        }

    }

    /**
     * Override this method to delegate OnItemClickListener of conversation list.
     * You must declare which Activity you'd like to jump to by passing contents of talk as parameters.
     *
     * @param talk The assential information supporting your chat Activity's launching.
     * @return
     */
    protected boolean onItemClickDelegate(Talk talk) {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getActivity(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
        int realPosition = position - this.mListView.getHeaderViewsCount();
        if (realPosition < 0 || realPosition >= mTalks.size()) {
            return false;
        }
        final Talk talk = mTalks.get(realPosition);
        if (talk.mNoReadMsgCount > 0) {
            dialog.setListTexts(new String[]{getString(R.string.mark_as_read), getString(R.string.delete_talk)});
        } else {
            dialog.setListTexts(new String[]{getString(R.string.delete_talk)});
        }
        dialog.initDialog(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (talk.mNoReadMsgCount > 0) {
                    if (position == 0) {
                        TalkLogic.getInstance().updateTalkRead(talk.mTalkOtherUserId, talk.mTalkOtherUserSource);
                    } else if (position == 1) {
                        LogUmengAgent.ins().log(LogUmengEnum.LOG_XIAOXILBY_DELETE);
                        TalkLogic.getInstance().deleteTalk(talk.mTalkOtherUserId, talk.mTalkOtherUserSource);
                    }
                } else {
                    if (position == 0) {
                        LogUmengAgent.ins().log(LogUmengEnum.LOG_XIAOXILBY_DELETE);
                        TalkLogic.getInstance().deleteTalk(talk.mTalkOtherUserId, talk.mTalkOtherUserSource);
                    }
                }
                dialog.dismiss();
            }
        }).create().show();

        return true;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (showDisconnectedHeader()) {
            ClientManager.getInstance().unRegConnectListener(this);
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        isBackground = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isBackground = true;
    }

    /**
     * The method that can be invoked to update adapter associated with mListView.
     *
     * @param newAdapter
     */
    protected void setAdapter(ConversationListAdapter newAdapter) {
        mTalkAdapter = newAdapter;
        mListView.setAdapter(mTalkAdapter);
    }


    private void specialTalksPreparedWrapper(List<Talk> specialTalks, boolean isTalkListEmpty) {
        if (isTalkListEmpty) {
            mListView.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        } else {
            mListView.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
        }
        mListView.requestLayout();
        specialTalksPrepared(specialTalks, isTalkListEmpty);
    }

    /**
     * Notifying specialTalks View for Child Fragment.
     *
     * @param specialTalks
     */
    protected void specialTalksPrepared(List<Talk> specialTalks, boolean isTalkListEmpty) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTalkListChanged(RecentTalksEvent event) {

        refreshCustom();

        List<Talk> talks = event.getTalks();

        //TODO: Penta去除客服talks;


        mTalks.clear();
        if (talks != null) {
            mTalks.addAll(talks);
        }
        if (mTalkAdapter != null) {
            if (isBackground) {
                int visibleHeaderViewCount = 0;
                for (int i = 0; i < mListView.getChildCount(); i++) {
                    if (mListView.getChildAt(i).getTag() == null) {
                        visibleHeaderViewCount++;
                    } else {
                        break;
                    }
                }
                int firstVisiblePosition = mListView.getFirstVisiblePosition()
                        - mListView.getHeaderViewsCount()
                        + visibleHeaderViewCount;
                int lastVisiblePosition = mListView.getLastVisiblePosition()
                        - mListView.getHeaderViewsCount()
                        - mListView.getFooterViewsCount();
                for (int i = firstVisiblePosition, j = visibleHeaderViewCount;
                     i <= lastVisiblePosition && i <= mTalks.size() - 1 && j < mListView.getChildCount();
                     i++, j++) {
                    mTalkAdapter.getView(i, mListView.getChildAt(j), null);
                }
            } else {
                mTalkAdapter.notifyDataSetChanged();
            }
        }
        GLog.d(TAG, "talks.size=" + mTalks.size());
        boolean isTalkListEmpty = mTalks.size() == 0;
        specialTalksPreparedWrapper(ChannelManager.getInstance().getSpecialTalks(), isTalkListEmpty);
    }

    /**
     * 更新客服消息栏状态
     */
    private void refreshCustom() {

        //判断是否展示专属客服
        if (!judgeHasCustom()) {
            return;
        }

        IMMessageEntity imMessageEntity = IMMessageDaoOperate.getLastCustomMessage(UserUtils.getUserId(getActivity()));
        if (null != imMessageEntity) {

            Message message = CustomMessageUtil.entityToOriginal(imMessageEntity);
            if (imMessageEntity.getReceiverId().equals(UserUtils.getUserId(getActivity()))) {
                messageUserInfo = message.mSenderInfo;
            } else if (imMessageEntity.getSenderId().equals(UserUtils.getUserId(getActivity()))) {
                messageUserInfo = message.mReceiverInfo;
            }

            if (messageUserInfo != null)
                customView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getActivity(), "进入详情页", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(createToChatActivity(getActivity(), "", messageUserInfo, IMConstant.EXTRA_TYPE_CUSTOM));
                        startActivity(intent);
                        IMMessageDaoOperate.updateDataRedDot(UserUtils.getUserId(getActivity()));
                    }
                });
            if (null != message.mMsgDetail.getmMsgContent().mType) {
                if (message.mMsgDetail.getmMsgContent().mType.equals(MsgContentType.TYPE_TEXT)) {
                    customTextView.setText(((message.mMsgDetail.getmMsgContent())).getPlainTextSpannableStringBuilder(getActivity()));
                } else if (message.mMsgDetail.getmMsgContent().mType.equals(MsgContentType.TYPE_IMAGE)) {
                    customTextView.setText("[图片]");
                }
            }

            long count = IMMessageDaoOperate.countUnRead(UserUtils.getUserId(getActivity()));
            if (count > 0) {
                customRedDot.setText("" + count);
                customRedDot.setVisibility(View.VISIBLE);
            } else {
                customRedDot.setVisibility(View.GONE);
            }
            customTime.setText(DateUtils.messageTimeFormat(message.mMsgDetail.mMsgUpdateTime));


        }

    }

    private boolean judgeHasCustom() {
        String customId = UserUtils.getCustomId(getActivity());

        if (TextUtils.isEmpty(customId)) {
            customView.setVisibility(View.GONE);
            return false;
        } else {
            customView.setVisibility(View.VISIBLE);
            return true;
        }
    }

    public static Intent createToChatActivity(Context var0, String var1, Message.MessageUserInfo var2, int type) {
        Intent var3 = new Intent(var0, GmacsChatActivity.class);
        var3.putExtra("refer", var1);
        var3.putExtra("otherUserInfo", var2);
        var3.putExtra(IMConstant.EXTRA_TYPE, type);
        return var3;
    }


    @Override
    public void connectStatusChanged(final int status) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showOrHideDisconnectHeader(status);
                }
            });
        }
    }

    @Override
    public void connectionTokenInvalid(String errorMsg) {

    }

    /**
     * Handle the visibility of ConnectionStatusBar and change StatusBar's appearance
     * according to connection status by overriding this method.
     *
     * @param status Connection status
     */
    protected void showOrHideDisconnectHeader(int status) {
        if (null == mConnectionStatusHeaderViewContainer) {
            return;
        }
        switch (status) {
            case GmacsConstant.STATUS_DISCONNECTED_VALUE:
            default:
                mConnectionStatusHeaderView.setVisibility(View.VISIBLE);
                if (NetworkUtil.isNetworkAvailable()) {
                    mConnectionStatusTextView.setText(R.string.connection_status_disconnected);
                } else {
                    mConnectionStatusTextView.setText(R.string.network_unavailable);
                }
                break;
            case GmacsConstant.STATUS_WAITING_VALUE:
                mConnectionStatusHeaderView.setVisibility(View.VISIBLE);
                mConnectionStatusTextView.setText(R.string.connection_status_connecting);
                break;
            case GmacsConstant.STATUS_CONNECTING_VALUE:
                mConnectionStatusHeaderView.setVisibility(View.VISIBLE);
                mConnectionStatusTextView.setText(R.string.connection_status_connecting);
                break;
            case GmacsConstant.STATUS_CONNECTED_VALUE:
                mConnectionStatusHeaderView.setVisibility(View.GONE);
                break;
            case GmacsConstant.STATUS_KICK_OFF:
                mConnectionStatusHeaderView.setVisibility(View.VISIBLE);
                mConnectionStatusTextView.setText(R.string.connection_status_kickedoff);
                break;
        }
    }

}