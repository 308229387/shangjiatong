package com.android.gmacs.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.core.UserInfoCacheManager;
import com.android.gmacs.logic.MessageLogic;
import com.android.gmacs.msg.view.IMMessageView;
import com.android.gmacs.msg.view.IMViewFactory;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.MsgContentType;
import com.common.gmacs.parse.contact.GmacsUser;
import com.common.gmacs.parse.message.GmacsUserInfo;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.parse.talk.TalkType;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.ImageUtil;
import com.xxganji.gmacs.proto.CommonPB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;

/**
 * Created by zhangxiaoshuang on 2015/11/30.
 */
public class GmacsChatAdapter extends BaseAdapter implements View.OnClickListener {
    // 5分钟显示一下时间
    private static final int FIVE_MINUTE_SECOND = 60 * 5 * 1000;
    // 所有消息
    protected ArrayList<Message> mAllMessage = new ArrayList<>();
    /**
     * 创建imview的工厂类，子类可以扩展该工厂类
     */
    private IMViewFactory mIMViewFactory = new IMViewFactory();

    private GmacsChatActivity mActivity;
    protected LayoutInflater mInflater;
    private Talk mTalk;
    private SimpleDateFormat mSimpleDateFormat = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

    public GmacsChatAdapter(Context context, Talk talk) {
        this.mActivity = (GmacsChatActivity) context;
        this.mTalk = talk;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setTalk(Talk mTalk) {
        this.mTalk = mTalk;
    }

    public void clearData() {
        mAllMessage.clear();
        notifyDataSetChanged();
    }

    public static int BASE_NUM = 0;
    public static int ITEM_TYPE_EMPTY = BASE_NUM++;
    public static int ITEM_TYPE_GROUP_JOIN = BASE_NUM++;
    public static int ITEM_TYPE_TIP = BASE_NUM++;

    public static int ITEM_TYPE_RIGHT_TEXT = BASE_NUM++;
    public static int ITEM_TYPE_RIGHT_AUDIO = BASE_NUM++;
    public static int ITEM_TYPE_RIGHT_PIC = BASE_NUM++;
    public static int ITEM_TYPE_RIGHT_GIF = BASE_NUM++;
    public static int ITEM_TYPE_RIGHT_MAP = BASE_NUM++;

    public static int ITEM_TYPE_LEFT_TEXT = BASE_NUM++;
    public static int ITEM_TYPE_LEFT_AUDIO = BASE_NUM++;
    public static int ITEM_TYPE_LEFT_PIC = BASE_NUM++;
    public static int ITEM_TYPE_LEFT_GIF = BASE_NUM++;
    public static int ITEM_TYPE_LEFT_MAP = BASE_NUM++;

    public void setIMViewFactory(IMViewFactory mIMViewFactory) {
        this.mIMViewFactory = mIMViewFactory;
    }

    /**
     * 把消息添加到结束位置
     *
     * @param msg
     */
    public void addMsgToEndPosition(Message msg) {
        if (msg != null) {
            mAllMessage.add(msg);
            notifyDataSetChanged();
        }
    }

    /**
     * 把消息们添加到起始位置
     *
     * @param msgs
     */
    public void addMsgsToStartPosition(List<Message> msgs) {
        if (msgs != null && msgs.size() > 0) {
            for (Message message : msgs) {
                mAllMessage.add(0, message);
            }
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (mAllMessage != null) {
            return mAllMessage.size();
        }
        return 0;
    }

    @Override
    public Message getItem(int position) {
        Message msg = null;
        if (position >= 0 && position <= mAllMessage.size() - 1) {
            msg = mAllMessage.get(position);
        }
        return msg;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int type = ITEM_TYPE_EMPTY;
        Message message = mAllMessage.get(position);
        String msgType = message.mMsgDetail.getmMsgContent().mType;
        if (MsgContentType.TYPE_GROUP_JOIN.equals(msgType)) {
            type = ITEM_TYPE_GROUP_JOIN;
        } else if (MsgContentType.TYPE_TIP.equals(msgType)) {
            type = ITEM_TYPE_TIP;
        } else if (message.mMsgDetail.mIsSelfSendMsg) {
            switch (msgType) {
                case MsgContentType.TYPE_TEXT:
                    type = ITEM_TYPE_RIGHT_TEXT;
                    break;
                case MsgContentType.TYPE_AUDIO:
                    type = ITEM_TYPE_RIGHT_AUDIO;
                    break;
                case MsgContentType.TYPE_IMAGE:
                    type = ITEM_TYPE_RIGHT_PIC;
                    break;
                case MsgContentType.TYPE_LOCATION:
                    type = ITEM_TYPE_RIGHT_MAP;
                    break;
                case MsgContentType.TYPE_GIF:
                    type = ITEM_TYPE_RIGHT_GIF;
            }
        } else {
            switch (msgType) {
                case MsgContentType.TYPE_TEXT:
                    type = ITEM_TYPE_LEFT_TEXT;
                    break;
                case MsgContentType.TYPE_AUDIO:
                    type = ITEM_TYPE_LEFT_AUDIO;
                    break;
                case MsgContentType.TYPE_IMAGE:
                    type = ITEM_TYPE_LEFT_PIC;
                    break;
                case MsgContentType.TYPE_LOCATION:
                    type = ITEM_TYPE_LEFT_MAP;
                    break;
                case MsgContentType.TYPE_GIF:
                    type = ITEM_TYPE_LEFT_GIF;
            }
        }
        return type;
    }

    private long lastTime;
    private ArrayList<Long> timesMap = new ArrayList<>();

    private int findSimilarMessageTime(Message message) {
        int count = 0;
        Message temp;
        for (int i = 0; i < mAllMessage.size(); i++) {
            temp = mAllMessage.get(i);
            if (Math.abs(temp.mMsgDetail.mMsgUpdateTime - message.mMsgDetail.mMsgUpdateTime) <= 60 * 1000) {
                count++;
                if (temp.equals(message)) {
                    return count;
                }
            }
        }
        return count;
    }

    private int similarMessageTimeAmount(Message message) {
        int count = 0;
        long temp;
        for (int i = 0; i < timesMap.size(); i++) {
            temp = timesMap.get(i);
            if (Math.abs(temp - message.mMsgDetail.mMsgUpdateTime) <= 60 * 1000) {
                count++;
            }
        }
        return count;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = mAllMessage.get(position);
        MsgViewHolder viewHolder;
        IMMessageView messageView;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            viewHolder = new MsgViewHolder();
            if (viewType == ITEM_TYPE_EMPTY) {
                convertView = new TextView(mActivity);
                convertView.setVisibility(View.GONE);
                return convertView;
            } else {
                convertView = getConvertParentView(viewType);
            }
            initConvertView(convertView, viewHolder);
            // 根据类型判断加载哪种消息视图
            messageView = mIMViewFactory.createItemView(message.mMsgDetail.getmMsgContent());
            View msgCardView = messageView.createIMView(message.mMsgDetail.getmMsgContent(), viewHolder.contentItem, mInflater, position, this, mActivity);
            replaceAloneSendProgressBar(viewHolder, msgCardView);
            viewHolder.contentItem.setTag(messageView);
        } else {
            if (viewType == ITEM_TYPE_EMPTY) {
                return convertView;
            }
            viewHolder = (MsgViewHolder) convertView.getTag();
            messageView = (IMMessageView) viewHolder.contentItem.getTag();
        }
        messageView.init(position, this, mActivity, message.mMsgDetail.getmMsgContent());
        setDataForView(message.mMsgDetail.getmMsgContent(), viewHolder);
        messageView.setDataForView();
        getViewType(viewHolder, position, viewType);

        // 设置时间
        if (message.hasSetShowTime) {
            if (message.shouldShowTime) {
                long msgUpdateTime = message.mMsgDetail.mMsgUpdateTime;
                int amount = similarMessageTimeAmount(message);
                String postDate;
                if (amount != 0) {
                    postDate = messageTimeFormat(msgUpdateTime - (amount - findSimilarMessageTime(message) + 1) * 60 * 1000);
                } else {
                    postDate = messageTimeFormat(msgUpdateTime);
                }
                viewHolder.time.setText(postDate);
                viewHolder.time.setVisibility(View.VISIBLE);
            } else {
                viewHolder.time.setVisibility(View.GONE);
            }
        } else {
            if (!TalkType.isSystemTalk(mTalk) && isNeed2ShowTime(position)) {
                if (Math.abs(message.mMsgDetail.mMsgUpdateTime - lastTime) > 1000 * 60) {
                    lastTime = message.mMsgDetail.mMsgUpdateTime;
                } else {
                    lastTime = message.mMsgDetail.mMsgUpdateTime - 1000 * 60;
                    timesMap.add(lastTime);
                }
                viewHolder.time.setText(messageTimeFormat(lastTime));
                viewHolder.time.setVisibility(View.VISIBLE);
            } else {
                viewHolder.time.setVisibility(View.GONE);
            }
        }

        if (viewHolder.sendFailed != null) {
            viewHolder.sendFailed.setTag(message);
            viewHolder.sendFailed.setOnClickListener(new SendFailedRetryListener(viewHolder));
        }
        return convertView;
    }

    protected String messageTimeFormat(long messageTime) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDate = calendar.get(Calendar.DATE);

        calendar.setTimeInMillis(messageTime);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        int messageYear = calendar.get(Calendar.YEAR);
        int messageMonth = calendar.get(Calendar.MONTH);
        int messageDate = calendar.get(Calendar.DATE);

        String formattedTime;
        if (currentYear == messageYear && currentMonth == messageMonth) {
            int delta = currentDate - messageDate;
            if (delta == 0) {
                mSimpleDateFormat.applyPattern("HH:mm");
                formattedTime = mSimpleDateFormat.format(calendar.getTime());
            } else if (delta > 0) {
                if (delta == 1) {
                    mSimpleDateFormat.applyPattern("HH:mm");
                    String accurateTime = mSimpleDateFormat.format(calendar.getTime());
                    formattedTime = "昨天 " + accurateTime;
                } else if (delta < 7) {
                    String[] weekOfDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
                    int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                    mSimpleDateFormat.applyPattern("HH:mm");
                    String accurateTime = mSimpleDateFormat.format(calendar.getTime());
                    formattedTime = weekOfDays[week >= 0 ? week : 0] + " " + accurateTime;
                } else {
                    mSimpleDateFormat.applyPattern("MM-dd HH:mm");
                    formattedTime = mSimpleDateFormat.format(calendar.getTime());
                }
            } else {
                mSimpleDateFormat.applyPattern("MM-dd HH:mm");
                formattedTime = mSimpleDateFormat.format(calendar.getTime());
            }
        } else if (currentYear == messageYear) {
            mSimpleDateFormat.applyPattern("MM-dd HH:mm");
            formattedTime = mSimpleDateFormat.format(calendar.getTime());
        } else {
            mSimpleDateFormat.applyPattern("yyyy-MM-dd HH:mm");
            formattedTime = mSimpleDateFormat.format(calendar.getTime());
        }

        return formattedTime;
    }

    /**
     * 生成单条view
     *
     * @return
     */
    private void initConvertView(View convertView, MsgViewHolder viewHolder) {
        viewHolder.time = (TextView) convertView.findViewById(R.id.time);
        viewHolder.contentItem = (ViewGroup) convertView.findViewById(R.id.content_item);
        viewHolder.leftHead = (NetworkImageView) convertView.findViewById(R.id.left_head);
        viewHolder.rightHead = (NetworkImageView) convertView.findViewById(R.id.right_head);
        viewHolder.leftName = (TextView) convertView.findViewById(R.id.left_name);
        viewHolder.sending = (ProgressBar) convertView.findViewById(R.id.send_progress);
        viewHolder.sendFailed = (ImageView) convertView.findViewById(R.id.right_failed_sendf);
        convertView.setTag(viewHolder);
    }

    //消息样式父布局
    protected View getConvertParentView(int viewType) {
        View convertView = null;
        if (viewType < ITEM_TYPE_RIGHT_TEXT) {
            convertView = mInflater.inflate(R.layout.gmacs_adapter_chat_notice_item, null);
        } else if (viewType < ITEM_TYPE_LEFT_TEXT) {
            convertView = mInflater.inflate(R.layout.gmacs_adapter_chat_right_item, null);
        } else if (viewType <= ITEM_TYPE_LEFT_MAP) {
            convertView = mInflater.inflate(R.layout.gmacs_adapter_chat_left_item, null);
        }
        return convertView;
    }

    protected boolean getViewType(MsgViewHolder viewHolder, int position, int viewType) {
        boolean notFindViewType = false;
        if (viewType < ITEM_TYPE_RIGHT_TEXT) {
        } else if (viewType < ITEM_TYPE_LEFT_TEXT) {
            initRightUser(viewHolder);
        } else if (viewType <= ITEM_TYPE_LEFT_MAP) {
            initLeftUser(viewHolder, position);
        } else {
            notFindViewType = true;
        }
        return notFindViewType;
    }

    public int getViewTypeCount() {
        return BASE_NUM;
    }

    /**
     * 判断是否需要显示时间
     */
    public boolean isNeed2ShowTime(int position) {
        long lastTime = 0;
        Message msg = mAllMessage.get(position);
        //发送失败的消息直接不显示时间
        if (msg.mMsgDetail.getSendStatus() == CommonPB.SendStatus.MSG_SEND_FAILED) {
            msg.hasSetShowTime = true;
            msg.shouldShowTime = false;
            return false;
        }
        //前一条消息发送失败，本条消息直接不显示时间
        if (position - 1 >= 0) {
            Message preMsg = mAllMessage.get(position - 1);
            if (preMsg.mMsgDetail.getSendStatus() == CommonPB.SendStatus.MSG_SEND_FAILED) {
                msg.hasSetShowTime = true;
                msg.shouldShowTime = false;
                return false;
            }
            lastTime = preMsg.mMsgDetail.mMsgUpdateTime;
        }
        //较上一条消息时间超过5分钟，本条消息显示时间。第一条成功的必显示时间。
        long time = msg.mMsgDetail.mMsgUpdateTime;
        boolean showTime = Math.abs(time - lastTime) > FIVE_MINUTE_SECOND;
        msg.hasSetShowTime = true;
        msg.shouldShowTime = showTime;
        return showTime;
    }

    //删除一条消息
    public void deleteMsg(int position) {
        Message delMsg = mAllMessage.remove(position);
        delMsg.isDeleted = true;
        notifyDataSetChanged();
        MessageLogic.getInstance().deleteMsgByMsgIds(new long[]{delMsg.mId});
    }

    //删除多条消息
    public void deleteMsgs(int[] position) {
        long[] msgIds = new long[position.length];
        for (int i = 0; i < position.length; i++) {
            Message delMsg = mAllMessage.remove(position[i]);
            delMsg.isDeleted = true;
            msgIds[i] = delMsg.mId;
        }
        notifyDataSetChanged();
        MessageLogic.getInstance().deleteMsgByMsgIds(msgIds);
    }

    //左侧默认头像
    protected int defaultLeftAvatarRes() {
        return R.drawable.gmacs_ic_default_avatar;
    }

    //右侧默认头像
    protected int defaultRightAvatarRes() {
        return R.drawable.gmacs_ic_default_avatar;
    }

    //左侧头像点击事件
    public void initLeftUser(MsgViewHolder viewHolder, int position) {
        Message.MessageUserInfo info = getSenderInfoForReceiveMsg(position);
        viewHolder.leftHead.setTag(info);

        String avatar = "";
        String name = "";

        if (info.gmacsUserInfo != null) {
            avatar = info.gmacsUserInfo.avatar;
            name = info.gmacsUserInfo.userName;
        }

        if (TextUtils.isEmpty(name)) {
            viewHolder.leftName.setText(GmacsEnvi.appContext.getResources().getString(R.string.default_user_name));
        } else {
            viewHolder.leftName.setText(name);
        }
        viewHolder.leftHead
                .setDefaultImageResId(defaultLeftAvatarRes())
                .setErrorImageResId(defaultLeftAvatarRes())
                .setImageUrl(ImageUtil.makeUpUrl(avatar, IMG_RESIZE, IMG_RESIZE));
        if (TalkType.isGroupTalk(mTalk)) {
            viewHolder.leftName.setVisibility(View.VISIBLE);
        } else {
            viewHolder.leftName.setVisibility(View.GONE);
        }

        if (null == info.gmacsUserInfo || TextUtils.isEmpty(info.gmacsUserInfo.userId)) {
            UserInfoCacheManager.getInstance().updateUserInfoFromCache(info, viewHolder.leftHead, viewHolder.leftName);
        }

        if (leftAvatarListener != null) {
            viewHolder.leftHead.setOnClickListener(leftAvatarListener);
        } else {
            viewHolder.leftHead.setOnClickListener(this);
        }
    }

    public void initRightUser(MsgViewHolder viewHolder) {
        viewHolder.rightHead
                .setDefaultImageResId(R.drawable.gmacs_ic_default_avatar)
                .setErrorImageResId(R.drawable.gmacs_ic_default_avatar)
                .setImageUrl(ImageUtil.makeUpUrl(Gmacs.getInstance().getGmacsUserInfo().avatar, IMG_RESIZE, IMG_RESIZE));
        if (rightAvatarListener != null) {
            viewHolder.rightHead.setOnClickListener(rightAvatarListener);
        } else {
            viewHolder.rightHead.setOnClickListener(this);
        }
    }

    /**
     * 为收到的消息获取发送消息者的信息
     *
     * @param position
     * @return
     */
    protected Message.MessageUserInfo getSenderInfoForReceiveMsg(int position) {
        return mAllMessage.get(position).mSenderInfo;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.right_head) {
            try {
                Intent intent = new Intent(mActivity, Class.forName(GmacsUiUtil.getContactDetailInfoActivityClassName()));
                GmacsUserInfo gmacsUserInfo = Gmacs.getInstance().getGmacsUserInfo();
                if (gmacsUserInfo != null) {
                    intent.putExtra(GmacsConstant.EXTRA_USER_ID, gmacsUserInfo.userId);
                    intent.putExtra(GmacsConstant.EXTRA_NAME, gmacsUserInfo.userName);
                    intent.putExtra(GmacsConstant.EXTRA_AVATAR, gmacsUserInfo.avatar);
                    intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, gmacsUserInfo.userSource);
                    intent.putExtra(GmacsConstant.EXTRA_DEVICEID, GmacsUser.getInstance().getDeviceId());
                    intent.putExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
                    mActivity.startActivity(intent);
                }
            } catch (ClassNotFoundException e) {
            }
        } else if (v.getId() == R.id.left_head) {
            Object o = v.getTag();
            if (o != null && o instanceof Message.MessageUserInfo) {
                Message.MessageUserInfo userInfo = (Message.MessageUserInfo) o;
                try {
                    Intent intent = new Intent(mActivity, Class.forName(GmacsUiUtil.getContactDetailInfoActivityClassName()));
                    intent.putExtra(GmacsConstant.EXTRA_USER_ID, userInfo.mUserId);
                    if (userInfo.gmacsUserInfo != null) {
                        intent.putExtra(GmacsConstant.EXTRA_NAME, userInfo.gmacsUserInfo.userName);
                        intent.putExtra(GmacsConstant.EXTRA_AVATAR, userInfo.gmacsUserInfo.avatar);
                    }
                    intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, userInfo.mUserSource);
                    intent.putExtra(GmacsConstant.EXTRA_DEVICEID, userInfo.mDeviceId);
                    intent.putExtra(GmacsConstant.EXTRA_OPENID, userInfo.mOpenId);
                    intent.putExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
                    mActivity.startActivity(intent);
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }

    private void setDataForView(IMMessage msg, MsgViewHolder viewHolder) {
        if (msg == null) {
            return;
        }
        if (viewHolder.sendFailed != null && viewHolder.sending != null) {
            switch (msg.parentMsg.getSendStatus()) {
                case MSG_SEND_FAILED:
                    viewHolder.sendFailed.setVisibility(View.VISIBLE);
                    viewHolder.sending.setVisibility(View.GONE);
                    break;
                case MSG_SENDING:
                    viewHolder.sendFailed.setVisibility(View.GONE);
                    viewHolder.sending.setVisibility(View.VISIBLE);
                    break;
                case MSG_UNSEND:
                case MSG_SENT:
                case MSG_FAKE_MSG:
                    viewHolder.sendFailed.setVisibility(View.GONE);
                    viewHolder.sending.setVisibility(View.GONE);
                    break;
            }
        }
    }

    /**
     * 发送失败的消息
     */
    protected void sendFailedIMMsg(MsgViewHolder viewHolder) {
        if (mActivity == null) {
            return;
        }
        if (viewHolder.sending != null) {
            viewHolder.sending.setVisibility(View.VISIBLE);
        }
        if (viewHolder.sendFailed != null) {
            final Message message = (Message) viewHolder.sendFailed.getTag();
            viewHolder.sendFailed.setVisibility(View.GONE);
            mActivity.reSendMsg(message);
        }
    }

    /**
     * 替换成消息card中拥有的独立发送中视图
     *
     * @param viewHolder
     * @param msgCardView
     */
    private void replaceAloneSendProgressBar(MsgViewHolder viewHolder, View msgCardView) {
        ProgressBar aloneSendProgressBar = (ProgressBar) msgCardView.findViewById(R.id.send_progress);
        if (aloneSendProgressBar != null && viewHolder.sending != null) {
            viewHolder.sending.setVisibility(View.GONE);
            viewHolder.sending = aloneSendProgressBar;
        }
    }

    private View.OnClickListener leftAvatarListener;

    /**
     * 供宿主适配器设置左边头像点击事件
     */
    public void onLeftAvatarClickListener(View.OnClickListener listener) {
        this.leftAvatarListener = listener;
    }

    private View.OnClickListener rightAvatarListener;

    /**
     * 供宿主适配器设置右边头像点击事件
     */
    public void onRightAvatarClickListener(View.OnClickListener listener) {
        this.rightAvatarListener = listener;
    }

    protected class MsgViewHolder {
        public TextView time;
        public TextView leftName;
        public NetworkImageView leftHead, rightHead;
        public ViewGroup contentItem;
        public ProgressBar sending;
        public ImageView sendFailed;
    }

    //消息重新发送监听
    private class SendFailedRetryListener implements View.OnClickListener {

        private MsgViewHolder viewHolder;

        public SendFailedRetryListener(MsgViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            final GmacsDialog.Builder dialog = new GmacsDialog.Builder(mActivity, GmacsDialog.Builder.DIALOG_TYPE_TEXT_NEG_POS_BUTTON);
            dialog.initDialog(mActivity.getText(R.string.retry_or_not), mActivity.getText(R.string.retry), mActivity.getText(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendFailedIMMsg(viewHolder);
                    dialog.dismiss();
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

}