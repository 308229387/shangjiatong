
package com.android.gmacs.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.Utils.UserUtils;
import com.Utils.eventbus.IMCustomChangeEvent;
import com.Utils.eventbus.IMDetailDestroyEvent;
import com.Utils.eventbus.IMKickoffEvent;
import com.Utils.eventbus.IMReconnectEvent;
import com.android.gmacs.R;
import com.android.gmacs.adapter.GmacsChatAdapter;
import com.android.gmacs.core.GmacsManager;
import com.android.gmacs.event.DeleteContactEvent;
import com.android.gmacs.event.GetCaptchaEvent;
import com.android.gmacs.event.LoadHistoryMessagesEvent;
import com.android.gmacs.event.RemarkEvent;
import com.android.gmacs.event.UpdateInsertChatMsgEvent;
import com.android.gmacs.event.UpdateSelfInfoEvent;
import com.android.gmacs.event.ValidateCaptchaEvent;
import com.android.gmacs.logic.ContactLogic;
import com.android.gmacs.logic.MessageLogic;
import com.android.gmacs.logic.TalkLogic;
import com.android.gmacs.observer.CardMsgClickListener;
import com.android.gmacs.sound.SoundPlayer;
import com.android.gmacs.sound.SoundRecord;
import com.android.gmacs.sound.SoundRecordUtil;
import com.android.gmacs.utils.CustomMessageUtil;
import com.android.gmacs.utils.IMConstant;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.PublicAccountMenu;
import com.android.gmacs.view.ResizeLayout;
import com.android.gmacs.view.SendMoreLayout;
import com.android.gmacs.view.SendMsgLayout;
import com.android.gmacs.view.listview.XXListView;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.core.MessageManager;
import com.common.gmacs.core.RecentTalkManager;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.data.IMLocationMsg;
import com.common.gmacs.msg.data.IMTipMsg;
import com.common.gmacs.parse.captcha.Captcha;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.parse.contact.GmacsUser;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.pubcontact.PAFunctionConfig;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.parse.talk.TalkType;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsConfig;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ToastUtil;
import com.commonview.CommonDialog;
import com.db.dao.IMMessageEntity;
import com.db.helper.IMMessageDaoOperate;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.xxganji.gmacs.proto.CommonPB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by caotongjun on 2015/11/25.
 */
public class GmacsChatActivity extends BaseActivity implements SendMoreLayout.OnMoreItemClickListener,
        MessageManager.SendIMMsgListener, CardMsgClickListener, ClientManager.PopLogViewListener {

    protected ResizeLayout resizeLayout;
    protected XXListView chatListView;
    protected GmacsChatAdapter chatAdapter;
    public SendMsgLayout sendMsgLayout;
    public PublicAccountMenu publicAccountMenu;
    protected ProgressBar loadMsgsProgressBar;
    private boolean isAutoPull;

    // 该视图为自定义顶部消息置顶视图控件，初始值状态是隐藏的，由上层宿主实例来填充和控制
    protected LinearLayout personTopView;

    // 录制声音实例
    private SoundRecord mRecord = new SoundRecord();
    // 播放声音实例
    private SoundPlayer mPlayer = SoundPlayer.getInstance();

    // 当前会话的会话信息
    protected Talk mTalk;

    private Contact otherUserInfo;

    // 用户类型
    protected int userType = Gmacs.UserType.USERTYPE_NORMAL.getValue();

    public boolean isScrollEnd = true;
    private float mPosY, mCurPosY;
    private boolean isHidedInActionDown;
    private boolean isHidedInActionMove;
    private boolean shouldShowInputSoftAuto = true;
    private String refer;
    protected String otherDeviceId;
    protected String otherOpenId;
    private final int maxSpan = GmacsEnvi.appContext.getResources().getDimensionPixelOffset(R.dimen.show_input_method_max_span);
    public boolean sendMsgLayoutTouchable = true;
    private boolean hasMore = true;
    private final int msgCountPerRequest = 20;
    private boolean isCaptchaDialogShowing;
    private GmacsDialog.Builder captchaDialog;
    private Set<Message> msgsNeedIdentify;

    private CommonDialog commonDialog;//断线重连弹窗

    private int type;//聊天类型 1：专属客服聊天 0：普通聊天
    private long customMessageTime = 0;//最后一条消息时间戳，用于增量拉取
    private Message.MessageUserInfo messageUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_chat);
        setBackEnable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mTalk != null) {
            mTalk.setTalkState(Talk.TALK_STATE_ING);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ClientManager.getInstance().registerLogViewListener(this);
        showImKickoffDialog();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        beginMsgId = -1;
        mTalk = null;
        MessageManager.getInstance().removeSendIMMsgListener(this);
        TalkLogic.getInstance().getRecentTalks();
        parseExtraObjects(intent);
        refreshData();
    }

    @Override
    protected void initView() {
        resizeLayout = (ResizeLayout) findViewById(R.id.resizeLayout);
        chatListView = (XXListView) findViewById(R.id.listview_chat);
        personTopView = (LinearLayout) findViewById(R.id.person_msg_layout);
        initSendMsgLayout();
        publicAccountMenu = (PublicAccountMenu) findViewById(R.id.public_account_menu);
        chatListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPosY = event.getY();
                        isHidedInActionDown = sendMsgLayout.inputSoftIsShow;
                        sendMsgLayout.collapseMoreAndInputMethod();
                        sendMsgLayoutTouchable = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sendMsgLayoutTouchable = false;
                        isHidedInActionMove = sendMsgLayout.inputSoftIsShow;
                        if (isHidedInActionMove && !isHidedInActionDown) {
                            sendMsgLayout.collapseMoreAndInputMethod();
                        }
                        mCurPosY = event.getY();
                        if (mPosY == 0) {
                            mPosY = mCurPosY;
                        }
                        if (shouldShowInputSoftAuto && mPosY - mCurPosY > maxSpan
                                && !isHidedInActionDown && !isHidedInActionMove && isScrollEnd) {
                            //向上滑动
                            setListViewTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                            sendMsgLayout.showInputSoft();
                            isHidedInActionDown = true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mPosY = 0;
                        mCurPosY = 0;
                        isHidedInActionDown = false;
                        isHidedInActionMove = false;
                        sendMsgLayoutTouchable = true;
                        break;
                    default:
                        isHidedInActionMove = false;
                        isHidedInActionDown = false;
                        sendMsgLayoutTouchable = true;
                        break;
                }
                return false;
            }
        });
        registerReceiver();
        chatListView.setXXListViewListener(mXXListViewListener);
        loadMsgsProgressBar = (ProgressBar) findViewById(R.id.load_new_msgs_progressBar);
        resizeLayout.setInputSoftListener(sendMsgLayout);
        loadMsgsProgressBar.setVisibility(View.VISIBLE);
        chatListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                isScrollEnd = absListView.getChildAt(absListView.getChildCount() - 1).getBottom() <= absListView.getHeight();
                if (hasMore && scrollState == SCROLL_STATE_IDLE && absListView.getFirstVisiblePosition() == 0) {
                    chatListView.startLoadMore();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    protected void initData() {
        //注册EventBus
        EventBus.getDefault().register(this);
        //获取intent传过来的数据
        parseExtraObjects(getIntent());
        refreshData();
    }

    private void refreshData() {
        if (mTalk == null) {
            return;
        }
        RecentTalkManager.getInstance().updateTalkRead(mTalk.mTalkOtherUserId, mTalk.mTalkOtherUserSource);
        getOtherInfo();
        resetViewState();
        clearNotice();
        if (chatAdapter != null) {
            chatAdapter.clearData();
            chatAdapter.setTalk(mTalk);
        } else {
            setChatAdapter(new GmacsChatAdapter(this, mTalk));
        }
        //加载历史消息
        loadHistoryMsgs();
        loadPAFunctionConfig();
        String currentTalkId = mTalk.getTalkId();
        boolean isTalkHasSendingMsgs = MessageManager.getInstance().isTalkHasSendingMsgs(currentTalkId);
        // 若有则把发送消息listener添加到发送manager中
        if (isTalkHasSendingMsgs) {
            MessageManager.getInstance().addSendIMMsgListener(this);
        }
        mTitleBar.setRightImageView(0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionOnNeed(Manifest.permission.READ_EXTERNAL_STORAGE,
                    GmacsConstant.REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    protected void setChatAdapter(GmacsChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
        chatListView.setAdapter(chatAdapter);
    }


    /**
     * 重新设置view状态,从onNewIntent方法进入时需要重置状态
     */
    protected void resetViewState() {
        shouldShowInputSoftAuto = getShouldShowInputSoftAutoConfig();
        chatListView.setPullRefreshEnable(true);
        sendMsgLayout.switchSendText(false);
        // 特定会话隐藏底部控件
        if (TalkType.isSystemTalk(mTalk)) {
            sendMsgLayout.setVisibility(View.GONE);
        } else {
            sendMsgLayout.setVisibility(View.VISIBLE);
        }
        publicAccountMenu.setVisibility(View.GONE);
        msgsNeedIdentify = null;
        isCaptchaDialogShowing = false;
        captchaDialog = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getInstance().unRegisterLogViewListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendMsgLayout.stopRecord();
        if (mTalk != null) {
            mTalk.setTalkState(Talk.TALK_STATE_PAUSE);
        }
    }

    public void sendTextMsg(String message) {
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
            CustomMessageUtil.sendIMTextMsg(message, "{\"show_in_app\": [\"10081\",\"100217\",\"100218\"]}", messageUserInfo, new IMMsgSendListener(this));
        } else {
            MessageManager.getInstance().sendIMTextMsg(mTalk.mTalkType, message
                    , refer, mTalk.mTalkOtherUserId
                    , mTalk.mTalkOtherUserSource, otherOpenId
                    , otherDeviceId, new IMMsgSendListener(this));
        }
    }

    public void sendAudioMsg(String filePath, int duration) {
        MessageManager.getInstance().sendIMAudioMsg(mTalk.mTalkType, refer
                , filePath, duration, mTalk.mTalkOtherUserId
                , mTalk.mTalkOtherUserSource, otherOpenId, otherDeviceId
                , new IMMsgSendListener(this));
    }

    public void sendImageMsg(String filePath, boolean sendRawImage) {
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
            CustomMessageUtil.sendIMImageMsg("{\"show_in_app\":['10081','100217','100218']}", filePath, sendRawImage, messageUserInfo, new IMMsgSendListener(this));
        } else {
            MessageManager.getInstance().sendIMImageMsg(mTalk.mTalkType, refer
                    , filePath, mTalk.mTalkOtherUserId
                    , mTalk.mTalkOtherUserSource, otherOpenId, otherDeviceId
                    , sendRawImage, new IMMsgSendListener(this));
        }
    }

    public void sendLocationMsg(double longitude, double latitude, String address) {
        IMLocationMsg imLocationMsg = new IMLocationMsg();
        imLocationMsg.mLongitude = longitude;
        imLocationMsg.mLatitude = latitude;
        imLocationMsg.mAddress = address;
        sendMsg(imLocationMsg, refer);
    }

    public void sendMsg(IMMessage imMessage) {
        sendMsg(imMessage, null);
    }

    public void sendMsg(IMMessage imMessage, String refer) {
        MessageManager.getInstance().sendIMMsg(mTalk.mTalkType, imMessage
                , refer, mTalk.mTalkOtherUserId
                , mTalk.mTalkOtherUserSource, otherOpenId, otherDeviceId
                , new IMMsgSendListener(this));
    }

    public void sendMsg(Message message) {
        MessageManager.getInstance().sendIMMsg(message, new IMMsgSendListener(this));
    }

    public void reSendMsg(Message message) {
        if (message != null && message.mMsgDetail != null) {
            message.mMsgDetail.mMsgUpdateTime = System.currentTimeMillis();
            MessageManager.getInstance().sendIMMsg(message, new IMMsgSendListener(this));
        }
    }

    public boolean sendAudioEnable() {
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
            return false;
        } else {
            return true;
        }
    }

    public boolean sendLocationEnable() {
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
            return false;
        } else {
            return true;
        }
    }

    public boolean sendEmojiEnable() {
        return true;
    }

    public boolean sendMoreEnable() {
        return true;
    }

    protected void setListViewTranscriptMode(int mode) {
        chatListView.setTranscriptMode(mode);
    }

    public void popMsgUpOfSendMsgLayout() {
        setListViewTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    public void hideSendMsgLayout() {
        setListViewTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
    }

    private void addMsgToAdapter(Message message) {
        // 将信息先显示到列表中
        chatAdapter.addMsgToEndPosition(message);
        chatListView.setSelection(chatListView.getBottom());
    }

    /**
     * 初始化发送消息layout，子类可以重写该方法
     */
    protected void initSendMsgLayout() {
        sendMsgLayout = (SendMsgLayout) findViewById(R.id.send_msg_layout);
        sendMsgLayout.setRecord(mRecord);
        sendMsgLayout.setGmacsChatActivity(this);
        sendMsgLayout.registerOnMoreItemClick(this);
    }

    /**
     * You can modify buttons except for adding your buttons and using none of or part of or all of our 3 default buttons,
     * whose texts and imgIds are SendMsgLayout.DEFAULT_BTN_TEXT_IMAGE + R.drawable.gmacs_ic_send_image,
     * SendMsgLayout.DEFAULT_BTN_TEXT_CAMERA + R.drawable.gmacs_ic_send_camera and
     * SendMsgLayout.DEFAULT_BTN_TEXT_LOCATION + R.drawable.gmacs_ic_send_location in SendMsgLayout.mSendMoreView.
     *
     * @param imgResId                       The resource id of image for buttons.
     * @param text                           The unique texts for buttons.
     * @param isShowItemsSingleLinePreferred If true passed by param and the amount of items below 5,
     *                                       the SendMoreView height would be set as single line.
     *                                       <br>Otherwise, double-line height as default value instead.</br>
     */
    public void setSendMoreItemResources(@NonNull int[] imgResId, @NonNull String[] text,
                                         boolean isShowItemsSingleLinePreferred) {
        sendMsgLayout.setSendMoreItemResources(imgResId, text, isShowItemsSingleLinePreferred);
    }

    /**
     * Override this method and handle buttons' click action by yourself.
     *
     * @param position The max position is equal to the amount of all the buttons subtracts one.
     */
    @Override
    public void onMoreItemClick(int position) {

    }

    public boolean isSoftInputHidden() {
        return resizeLayout.isSoftInputHidden;
    }

    public void resetSoftInputHiddenFlag() {
        resizeLayout.isSoftInputHidden = false;
    }

    /**
     * 获取快捷回复内容，现在是空实现，若需要需要子类实现该方法
     */
    public String[] getQuickMsgContents() {
        return null;
    }

    /**
     * 返回true时点击快速回复消息紧紧会将消息填充到输入框，否则直接发送。供子类重写该方法
     *
     * @return
     */
    public boolean justPutQuickMsgToInput() {
        return false;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                if (mPlayer != null && mPlayer.ismIsSoundPlaying()) {
                    mPlayer.stopPlayAndAnimation();
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        SoundRecordUtil.dispatchTouchEvent(ev, mRecord, sendMsgLayout.getRecordVoice());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        if (!sendMsgLayout.onBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sendMsgLayout.mSendMoreLayout.onActivityForResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void destroy() {
        SoundRecordUtil.destroy();
        if (mRecord != null && mRecord.isRecording()) {
            mRecord.stopRecord();
        }
        if (mPlayer != null && mPlayer.ismIsSoundPlaying()) {
            mPlayer.stopPlay();
        }
        unregisterReceiver(mBroadcastReceiver);
        MessageManager.getInstance().removeSendIMMsgListener(this);
        TalkLogic.getInstance().getRecentTalks();
        if (mTalk != null) {
            mTalk.setTalkState(Talk.TALK_STATE_END);
        }
        RecentTalkManager.getInstance().setmTalking(null);
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        if(type == IMConstant.EXTRA_TYPE_CUSTOM) {
            IMMessageDaoOperate.updateDataRedDot(UserUtils.getUserId(this));
            EventBus.getDefault().post(new IMDetailDestroyEvent());
        }
        destroy();
        super.onDestroy();
        LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_RETURN);

    }

    /**
     * 收到新消息
     */
    protected void receivedNewMsg(Message message) {
        if (chatListView != null) {
            int size = chatListView.getCount() - 1;
            if (size > 0 && chatListView.getLastVisiblePosition() == size) {
                setListViewTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            } else {
                setListViewTranscriptMode(ListView.TRANSCRIPT_MODE_DISABLED);
            }
            chatAdapter.addMsgToEndPosition(message);
        }
    }

    public Talk getTalk() {
        return mTalk;
    }

    /**
     * 从extra中解析一些数据
     *
     * @param intent
     */
    protected void parseExtraObjects(Intent intent) {
        messageUserInfo = intent.getParcelableExtra(GmacsConstant.EXTRA_OHTER_USER_INFO);
        refer = intent.getStringExtra(GmacsConstant.EXTRA_REFER);
        type = intent.getIntExtra(IMConstant.EXTRA_TYPE, 0);//获取会话类型 0：普通聊天 1：客服聊天
        sendMsgLayout.notifyMorelayout();//更新更多布局，主要是为了去掉专属客服的位置功能。
        initExtraConfig();
    }

    private void initExtraConfig() {
        if (messageUserInfo == null || TextUtils.isEmpty(messageUserInfo.mUserId)) {
            finish();
            return;
        } else {
            otherDeviceId = messageUserInfo.mDeviceId;
            otherOpenId = messageUserInfo.mOpenId;
        }
        if (messageUserInfo.mTalkType == 0) {
            GLog.d(TAG, "messageUserInfo 需要传入正确的talkType");
            finish();
            return;
        }
        // 避免自己和自己聊天
        if (messageUserInfo.mTalkType == Gmacs.TalkType.TALKETYPE_NORMAL.getValue()
                && messageUserInfo.mUserId.equals(GmacsUser.getInstance().getUserId())
                && messageUserInfo.mUserSource == GmacsUser.getInstance().getSource()) {
            finish();
            return;
        }
        otherUserInfo = null;
        mTalk = Talk.createTalkByMessageUserInfo(messageUserInfo);
        mTalk.setTalkState(Talk.TALK_STATE_ING);
        RecentTalkManager.getInstance().setmTalking(mTalk);
        // 设置草稿内容
        sendMsgLayout.setMsgEditText(mTalk.mDraftBoxMsg);
        // 设置标题栏名称
        setTitle(mTalk.getOtherName(this, defaultName(TalkType.isGroupTalk(mTalk))));
    }

    @Override
    public void setTitle(CharSequence title) {
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
            super.setTitle("专属客服");
        } else {
            super.setTitle(title);
        }

    }

    private void getOtherInfo() {
        ContactLogic.getInstance().getUserInfo(mTalk.mTalkOtherUserId, mTalk.mTalkOtherUserSource);
    }

    /**
     * 用以子类重写该方法，当昵称未空时来设置默认昵称,若子类不重写则默认显示userId
     *
     * @return
     */
    protected String defaultName(boolean isGroupTalk) {
        return null;
    }

    protected void willSendMessage(Message message) {
    }

    protected void didSendmessage(final Message message, int errorCode, String errorMessage) {
    }

    public static class InsertLocalMessageCb implements MessageManager.InsertLocalMessageCb {
        WeakReference<GmacsChatActivity> chatActivityWeakReference;

        InsertLocalMessageCb(GmacsChatActivity chatActivity) {
            this.chatActivityWeakReference = new WeakReference<>(chatActivity);
        }

        @Override
        public void onInsertLocalMessage(int errorCode, String errorMessage, final Message message) {
            final GmacsChatActivity ChatActivity = chatActivityWeakReference.get();
            if (ChatActivity != null) {
                ChatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ChatActivity.addMsgToAdapter(message);
                    }
                });
            }
        }
    }

    @Override
    public void onPreSaveMessage(Message message) {

    }

    @Override
    public void onAfterSaveMessage(Message message, int errorCode, String errorMessage) {

    }

    @Override
    public void onSendMessageResult(Message message, int errorCode, String errorMessage) {
        if (!mTalk.getTalkId().equals(Talk.getTalkId(message))) {
            return;
        }
        runOnUiThread(new Runnable() {
            public void run() {
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCardMsgClick(String contentType, IMMessage imMessage, String url, String title) {

    }

    @Override
    public void onShowLogView(final String log) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(GmacsChatActivity.this, GmacsDialog.Builder.DIALOG_TYPE_TEXT_NEU_BUTTON);
                dialog.initDialog(log, getText(R.string.close), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
    }

    public static class IMMsgSendListener implements MessageManager.SendIMMsgListener {

        private WeakReference<GmacsChatActivity> mActivityReference;

        IMMsgSendListener(GmacsChatActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void onPreSaveMessage(Message message) {
            final GmacsChatActivity chatActivity = mActivityReference.get();
            if (chatActivity != null) {
                chatActivity.willSendMessage(message);
            }
        }

        @Override
        public void onAfterSaveMessage(final Message message, int errorCode, String errorMessage) {
            final GmacsChatActivity chatActivity = mActivityReference.get();
            if (chatActivity != null) {
                chatActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        chatActivity.addMsgToAdapter(message);
                    }
                });
            }
        }

        @Override
        public void onSendMessageResult(final Message message, final int errorCode, final String errorMessage) {
            final GmacsChatActivity chatActivity = mActivityReference.get();
            if (chatActivity != null) {
                chatActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (errorCode == 40021) { // 灌水
                            if (chatActivity.msgsNeedIdentify == null) {
                                chatActivity.msgsNeedIdentify = new HashSet<>();
                            }
                            GmacsManager.getInstance().getCaptcha(message);
                            chatActivity.msgsNeedIdentify.add(message);
                            if (message.mMsgDetail != null) {
                                message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SENDING);
                            }
                        } else if (errorCode == 42001) { // 敏感词
                            IMTipMsg imTipMsg = new IMTipMsg();
                            imTipMsg.mText = errorMessage;
                            MessageManager.getInstance().insertLocalMessage(
                                    message.mTalkType, message.mSenderInfo, message.mReceiverInfo,
                                    message.mMsgDetail.getRefer(), imTipMsg, true,
                                    new InsertLocalMessageCb(chatActivity));
                        } else {
                            if (errorCode != 0) {
                                ToastUtil.showToast(errorMessage);
                            }
                            chatActivity.updateData();
                        }
                    }
                });
                chatActivity.didSendmessage(message, errorCode, errorMessage);
            }
        }

    }

    /**
     * 拉取历史记录
     */
    private void loadHistoryMsgs() {
        isRequestLoading = true;
        if (type == IMConstant.EXTRA_TYPE_CUSTOM) {

            List<IMMessageEntity> imMessageEntities = IMMessageDaoOperate.getCustomMessageByLastTime(
                    customMessageTime, UserUtils.getUserId(this));

            if (null != imMessageEntities && imMessageEntities.size() > 0) {

                //记录最后一条消息时间戳
                customMessageTime = imMessageEntities.get(imMessageEntities.size() - 1).getTimestamp();

                List<Message> messageList = new ArrayList<>();
                for (IMMessageEntity imMessageEntity : imMessageEntities) {
                    Message message = CustomMessageUtil.entityToOriginal(imMessageEntity);
                    messageList.add(message);
                }
                addAndSelectionFromTopMsgs(messageList);
            }
            chatListView.stopLoadMore();//停止下拉刷新
            isRequestLoading = false;//不是请求消息状态
            loadMsgsProgressBar.setVisibility(View.GONE);//关闭获取消息动画

            if (null == imMessageEntities || imMessageEntities.size() < 20) {
                hasMore = false;//没有更多历史消息了
            }

        } else {
            if (chatAdapter.getCount() > 0) {
                beginMsgId = chatAdapter.getItem(0).mId;
            } else {
                beginMsgId = -1;
            }
            MessageLogic.getInstance().getHistoryMessages(mTalk.mTalkOtherUserId,
                    mTalk.mTalkOtherUserSource, beginMsgId, msgCountPerRequest);
        }
    }

    /**
     * Downloading public account function config.
     */

    private void loadPAFunctionConfig() {
        sendMsgLayout.mPublicAccountMenuBtn.setVisibility(View.GONE);
        publicAccountMenu.setVisibility(LinearLayout.GONE);
        if (TalkType.isOfficialTalk(mTalk)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            // Local cache first, whatever the Internet is available.
            String menuData = (String) GmacsConfig.ClientConfig.getParam(mTalk.getTalkId() +
                    GmacsConfig.ClientConfig.KEY_PA_FUNCTION_CONFIG, "");
            boolean isEmpty = publicAccountMenu.setConfig(GmacsChatActivity.this,
                    PAFunctionConfig.buildPAFunctionConfig(menuData));

            if (isEmpty) {
                publicAccountMenu.setVisibility(LinearLayout.GONE);
                sendMsgLayout.setVisibility(View.VISIBLE);
                sendMsgLayout.mPublicAccountMenuBtn.setVisibility(View.GONE);
            } else {
                publicAccountMenu.setVisibility(LinearLayout.VISIBLE);
                sendMsgLayout.setVisibility(View.GONE);
                sendMsgLayout.mPublicAccountMenuBtn.setVisibility(View.VISIBLE);
            }

            if (cm.getActiveNetworkInfo() != null) {
                ContactLogic.getInstance().getPAFunctionConfig(mTalk.mTalkOtherUserId, mTalk.mTalkOtherUserSource);
            }
        } else if (TalkType.isSystemTalk(mTalk)) {
            sendMsgLayout.setVisibility(View.GONE);
        } else {
            sendMsgLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 请求是否正在进行
     */
    boolean isRequestLoading;
    long beginMsgId = -1;
    private XXListView.XXListViewListener mXXListViewListener = new XXListView.XXListViewListener() {

        @Override
        public void onLoadMore() {
            if (!isRequestLoading) {
                loadHistoryMsgs();
                LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_XL);
            }
        }
    };

    private void saveTalkUserInfo() {
        MessageManager.getInstance().updateReadStatusByTalkIdAsync(mTalk.mTalkOtherUserId,
                mTalk.mTalkOtherUserSource, 1, new MessageManager.ActionCb() {
                    public void done(int errorCode, String errorMessage) {
                        GLog.i(TAG, "updateTalkRead Message.ActionCb.errorCode=" + errorCode +
                                ",Message.ActionCb.errorMessage=" + errorMessage);
                    }
                });
        TalkLogic.getInstance().getRecentTalks();
    }

    /**
     * @param msgs 消息列表
     */
    private void addAndSelectionFromTopMsgs(List<Message> msgs) {
        chatAdapter.addMsgsToStartPosition(msgs);
        if (chatListView.getTranscriptMode() == ListView.TRANSCRIPT_MODE_DISABLED && !isAutoPull) {
            chatListView.setSelectionFromTop(msgs.size() + chatListView.getHeaderViewsCount(),
                    chatListView.mHeaderView.getHeight());
        } else {
            chatListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        }

    }


    /**
     * 清除notice
     */
    private void clearNotice() {
        int notice = 0;
        try {
            notice = mTalk.mTalkOtherUserId.hashCode();
        } catch (Exception e) {
        }
        if (notice != 0) {
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(notice);
        }
    }

    /**
     * 告知好友关系的改变
     *
     * @param isFriend
     */
    public void onFriendRelationShipChanged(boolean isFriend) {

    }

    private void setSomethingByUserInfo() {
        //快捷回复，放在此处，宿主可能需要根据具体Talk以及userType重写不同内容
        sendMsgLayout.initQuickMsgView(getQuickMsgContents());
        //是否需要发送语音、表情、更多 入口，放在此处，宿主可能需要根据具体Talk以及userType重写设置
        sendMsgLayout.setSendAudioEnable(sendAudioEnable());
        sendMsgLayout.setSendEmojiEnable(sendEmojiEnable());
        sendMsgLayout.setSendMoreEnable(sendMoreEnable());
    }

    public Contact getotherUserInfo() {
        return otherUserInfo;
    }

    public void setShouldShowInputSoftAuto(boolean shouldShowInputSoftAuto) {
        this.shouldShowInputSoftAuto = shouldShowInputSoftAuto;
    }

    public boolean getShouldShowInputSoftAutoConfig() {
        return true;
    }

    public void stopScroll() {
        if (chatListView != null) {
            chatListView.smoothScrollBy(0, 0);
        }
    }

    @Override
    protected void updateData() {
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (grantResults.length == 0) {
            return;
        }
        if (requestCode == GmacsConstant.REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (requestCode == GmacsConstant.REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sendMsgLayout != null) {
                    if (sendMsgLayout.mSendMoreLayout != null) {
                        sendMsgLayout.mSendMoreLayout.openCameraActivity();
                    }
                }
            }
        } else if (requestCode == GmacsConstant.REQUEST_CODE_ACCESS_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (sendMsgLayout != null) {
                    if (sendMsgLayout.mSendMoreLayout != null) {
                        sendMsgLayout.mSendMoreLayout.openLocationActivity();
                    }
                }
            }
        }
    }

    /**
     * ------------------ EventBus监听 start ------------------------
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemark(RemarkEvent event) {
        if (mTalk != null && mTalk.mTalkOtherUserId.equals(event.getUserId()) &&
                mTalk.mTalkOtherUserSource == event.getUserSource()) {
            mTalk.mTalkOtherUserRemark = event.getRemark();
            setTitle(mTalk.getOtherName(this, defaultName(false)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivedNewMessage(Message message) {
        if (mTalk != null && mTalk.hasTheSameTalkIdWith(message)) {
            receivedNewMsg(message);
            MessageManager.getInstance().ackMsgShow(message.mId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoadedHistoryMessages(LoadHistoryMessagesEvent event) {
        if (null == mTalk) {
            return;
        }
        isRequestLoading = false;
        List<Message> messages = event.getMessages();
        if (messages != null) {
            int size = messages.size();
            if (size > 0) {
                Message.MessageUserInfo info = messages.get(0).getTalkOtherUserInfo();
                if (!info.mUserId.equals(mTalk.mTalkOtherUserId) || info.mUserSource != mTalk.mTalkOtherUserSource) {
                    return;
                }
                addAndSelectionFromTopMsgs(messages);
                hasMore = messages.get(size - 1).mLinkMsgId != -3 && (beginMsgId == -1 || size >= msgCountPerRequest);
                if (-1 == beginMsgId) {
                    MessageManager.getInstance().ackMsgShow(messages.get(0).mId);
                }
                if (hasMore && chatAdapter.getCount() < 12 && -1 == beginMsgId) {
                    chatListView.startLoadMore();
                    isAutoPull = true;
                } else {
                    chatListView.stopLoadMore();
                    isAutoPull = false;
                }
            } else {
                hasMore = false;
                chatListView.stopLoadMore();
                isAutoPull = false;
            }
        } else {
            chatListView.stopLoadMore();
            isAutoPull = false;
        }
        loadMsgsProgressBar.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOtherUserInfo(Contact contact) {
        if (mTalk != null && mTalk.mTalkOtherUserId.equals(contact.getUserId())
                && mTalk.mTalkOtherUserSource == contact.getUserSource()) {
            otherUserInfo = contact;
            boolean isFriend = otherUserInfo.isContact();
            mTalk.mTalkOtherUserRemark = otherUserInfo.remark;
            mTalk.mTalkOtherUserName = otherUserInfo.getUserName();
            mTalk.mTalkOtherUserType = otherUserInfo.getUserType();
            mTalk.mTalkOtherGender = otherUserInfo.getGender();
            mTalk.mTalkOtherUserAvatar = otherUserInfo.getAvatar();
            userType = otherUserInfo.getUserType();
            setSomethingByUserInfo();
            onFriendRelationShipChanged(isFriend);
//            mTitleBar.setRightImageView(R.drawable.gmacs_ic_user);
//            mTitleBar.setRightImageViewListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    try {
//                        Intent intent = new Intent(GmacsChatActivity.this,
//                                Class.forName(GmacsUiUtil.getContactDetailInfoActivityClassName()));
//                        intent.putExtra(GmacsConstant.EXTRA_USER_ID, otherUserInfo.getUserId());
//                        intent.putExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
//                        intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, otherUserInfo.getUserSource());
//                        intent.putExtra(GmacsConstant.EXTRA_DEVICEID, GmacsUser.getInstance().getDeviceId());
//                        startActivity(intent);
//                    } catch (ClassNotFoundException e) {
//                    }
//                }
//            });
            setTitle(mTalk.getOtherName(GmacsChatActivity.this, defaultName(false)));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPAFunctionConfigEvent(PAFunctionConfig config) {
        boolean isEmpty = publicAccountMenu.setConfig(GmacsChatActivity.this, config);
        if (isEmpty) {
            publicAccountMenu.setVisibility(LinearLayout.GONE);
            sendMsgLayout.setVisibility(View.VISIBLE);
            sendMsgLayout.mPublicAccountMenuBtn.setVisibility(View.GONE);
        } else {
            publicAccountMenu.setVisibility(LinearLayout.VISIBLE);
            sendMsgLayout.setVisibility(View.GONE);
            sendMsgLayout.mPublicAccountMenuBtn.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(config.getMenuData())) {
                GmacsConfig.ClientConfig.setParam(mTalk.getTalkId() +
                        GmacsConfig.ClientConfig.KEY_PA_FUNCTION_CONFIG, config.getMenuData());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteContact(DeleteContactEvent event) {
        if (mTalk != null && event.getUserId().equals(mTalk.mTalkOtherUserId) &&
                event.getUserSource() == mTalk.mTalkOtherUserSource) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelfInfoChanged(UpdateSelfInfoEvent event) {
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取验证码
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCaptcha(final GetCaptchaEvent event) {
        Captcha captcha = event.getCaptcha();
        if (mTalk != null && mTalk.hasTheSameTalkIdWith(event.getMessage())) {
            if (captcha != null) {
                if (!isCaptchaDialogShowing) {
                    if (sendMsgLayout != null) {
                        sendMsgLayout.collapseMoreAndInputMethod();
                    }
                    LayoutInflater layoutInflater = LayoutInflater.from(this);
                    View captchaView = layoutInflater.inflate(R.layout.gmacs_captcha_layout, null);
                    final EditText et_captcha = (EditText) captchaView.findViewById(R.id.et_captcha);
                    et_captcha.setFocusable(true);
                    et_captcha.setFocusableInTouchMode(true);
                    et_captcha.requestFocus();
                    et_captcha.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_captcha, 0);
                        }
                    }, 200);
                    ImageView iv_captcha = (ImageView) captchaView.findViewById(R.id.iv_captcha);
                    final TextView tv_captcha_title = (TextView) captchaView.findViewById(R.id.tv_captcha_title);
                    TextView ok = (TextView) captchaView.findViewById(R.id.tv_ok);
                    TextView cancel = (TextView) captchaView.findViewById(R.id.tv_cancel);
                    tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_default));
                    tv_captcha_title.setText(R.string.enter_captcha);
                    iv_captcha.setImageBitmap(captcha.bitmap);
                    iv_captcha.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GmacsManager.getInstance().updateCaptcha(event.getMessage());
                            et_captcha.setText("");
                        }
                    });
                    et_captcha.setFilters(new InputFilter[]{new InputFilter() {
                        @Override
                        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                            if (source.toString().contains(" ")) {
                                return "";
                            } else {
                                return source;
                            }
                        }
                    }});
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String captchaContent = et_captcha.getText().toString();
                            if (TextUtils.isEmpty(captchaContent)) {
                                tv_captcha_title.setText(R.string.nonnull_captcha);
                                tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_error));
                                return;
                            }
                            tv_captcha_title.setText(R.string.captcha_verifying);
                            tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_default));
                            GmacsManager.getInstance().validateCaptcha(event.getCaptcha().responseId,
                                    captchaContent, event.getMessage());
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            manager.hideSoftInputFromWindow(et_captcha.getApplicationWindowToken(), 0);
                            captchaDialog.dismiss();
                            captchaDialog = null;
                            isCaptchaDialogShowing = false;
                            if (msgsNeedIdentify != null) {
                                for (Message message : msgsNeedIdentify) {
                                    message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SEND_FAILED);
                                }
                                msgsNeedIdentify.clear();
                            }
                            updateData();
                        }
                    });
                    captchaDialog = new GmacsDialog.Builder(this, GmacsDialog.Builder.DIALOG_TYPE_CUSTOM_CONTENT_VIEW);
                    GmacsDialog dialog = captchaDialog.initDialog(captchaView).setCancelable(false).create();
                    dialog.show();
                    isCaptchaDialogShowing = true;
                } else {
                    if (msgsNeedIdentify != null && msgsNeedIdentify.contains(event.getMessage())) {
                        View contentView = captchaDialog.getContentView();
                        if (contentView != null) {
                            final TextView tv_captcha_title = (TextView) contentView.findViewById(R.id.tv_captcha_title);
                            final EditText et_captcha = (EditText) contentView.findViewById(R.id.et_captcha);
                            TextView ok = (TextView) contentView.findViewById(R.id.tv_ok);
                            TextView cancel = (TextView) contentView.findViewById(R.id.tv_cancel);
                            ImageView iv_captcha = (ImageView) contentView.findViewById(R.id.iv_captcha);
                            iv_captcha.setImageBitmap(captcha.bitmap);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String captchaContent = et_captcha.getText().toString();
                                    if (TextUtils.isEmpty(captchaContent)) {
                                        tv_captcha_title.setText(R.string.nonnull_captcha);
                                        tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_error));
                                        return;
                                    }
                                    tv_captcha_title.setText(R.string.captcha_verifying);
                                    tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_default));
                                    GmacsManager.getInstance().validateCaptcha(event.getCaptcha().responseId,
                                            captchaContent, event.getMessage());
                                }
                            });
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    manager.hideSoftInputFromWindow(et_captcha.getApplicationWindowToken(), 0);
                                    captchaDialog.dismiss();
                                    captchaDialog = null;
                                    isCaptchaDialogShowing = false;
                                    if (msgsNeedIdentify != null) {
                                        for (Message message : msgsNeedIdentify) {
                                            message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SEND_FAILED);
                                        }
                                        msgsNeedIdentify.clear();
                                        updateData();
                                    }

                                }
                            });
                        }
                    }
                }
            } else {
                if (!isCaptchaDialogShowing) {
                    if (msgsNeedIdentify != null) {
                        for (Message message : msgsNeedIdentify) {
                            message.mMsgDetail.setMsgSendStatus(CommonPB.SendStatus.MSG_SEND_FAILED);
                        }
                        msgsNeedIdentify.clear();
                        updateData();
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onValidateCaptchaEvent(ValidateCaptchaEvent event) {
        if (event.isSuccess()) {
            ToastUtil.showToast(getText(R.string.valid_captcha));
            if (msgsNeedIdentify != null) {
                for (Message message : msgsNeedIdentify) {
                    reSendMsg(message);
                }
                msgsNeedIdentify.clear();
            }
            if (captchaDialog != null) {
                captchaDialog.dismiss();
                captchaDialog = null;
                isCaptchaDialogShowing = false;
            }
        } else {
            if (isCaptchaDialogShowing && captchaDialog != null) {
                View messageView = captchaDialog.getContentView();
                if (messageView != null) {
                    TextView tv_captcha_title = (TextView) messageView.findViewById(R.id.tv_captcha_title);
                    EditText et_captcha = (EditText) messageView.findViewById(R.id.et_captcha);
                    et_captcha.setText("");
                    tv_captcha_title.setText(R.string.invalid_captcha);
                    tv_captcha_title.setTextColor(getResources().getColor(R.color.captcha_text_error));
                    GmacsManager.getInstance().updateCaptcha(event.getMessage());
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateInsertChatMsg(UpdateInsertChatMsgEvent event) {
        if (mTalk != null && mTalk.hasTheSameTalkIdWith(event.getMessage())) {
            receivedNewMsg(event.getMessage());
        }
    }

    /**
     * IM断开监听
     *
     * @param action
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMKickoffEvent action) {
        showImKickoffDialog();
    }

    /**
     * 专属客服更新判断
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(IMCustomChangeEvent event) {
        String customId = UserUtils.getCustomId(this);

        if (TextUtils.isEmpty(customId)) {
            showCustomKickoffDialog();
        } else {
            if (event.getData() instanceof Message.MessageUserInfo) {
                messageUserInfo = (Message.MessageUserInfo) event.getData();
                initExtraConfig();
                //refreshData();
            }
        }


    }

    /**------------------ EventBus监听 end ------------------------*/


    /**
     * IM断开连接后，弹窗提示
     */
    public void showImKickoffDialog() {

        if (GmacsManager.isLoginState == false) {
            if (null == commonDialog) {
                commonDialog = new CommonDialog(this);
                commonDialog.setBtnCancelColor(com.android.gmacs.R.color.common_text_gray);
                commonDialog.setContent("您的消息在别处连接，请重新连接");
                commonDialog.setContentColor(com.android.gmacs.R.color.common_text_gray);
                commonDialog.setTitle("提示");
                commonDialog.setBtnCancelColor(com.android.gmacs.R.color.common_text_gray);
                commonDialog.setBtnSureText("重新连接");
                commonDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                    @Override
                    public void onDialogClickSure() {
                        EventBus.getDefault().post(new IMReconnectEvent());
                    }

                    @Override
                    public void onDialogClickCancel() {
                        commonDialog.dismiss();
                    }
                });


            }
            //dialog没有展示,则展示
            if (!commonDialog.isShowing()) {
                commonDialog.show();
            }
        }
    }

    /**
     * 专属客服到期，弹窗提示
     */
    public void showCustomKickoffDialog() {

        if (null == commonDialog) {
            commonDialog = new CommonDialog(this);
            commonDialog.setBtnCancelColor(com.android.gmacs.R.color.common_text_gray);
            commonDialog.setContent("您的专属客服已到期，请续费使用");
            commonDialog.setContentColor(com.android.gmacs.R.color.common_text_gray);
            commonDialog.setTitle("提示");
            commonDialog.setBtnCancelColor(com.android.gmacs.R.color.common_text_gray);
            commonDialog.setBtnSureText("确认");
            commonDialog.setCancelable(false);
            commonDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
                @Override
                public void onDialogClickSure() {
                    commonDialog.dismiss();
                    finish();
                }

                @Override
                public void onDialogClickCancel() {

                }
            });
        }
        //dialog没有展示,则展示
        if (!commonDialog.isShowing()) {
            commonDialog.show();
        }

    }


}