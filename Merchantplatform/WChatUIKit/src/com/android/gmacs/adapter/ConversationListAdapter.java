package com.android.gmacs.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.core.UserInfoCacheManager;
import com.android.gmacs.utils.DateUtils;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.parse.talk.Talk;
import com.common.gmacs.utils.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;

/**
 * Created by YanQi on 2015/12/9.
 * 会话列表适配器
 */
public class ConversationListAdapter extends BaseAdapter {

    private LayoutInflater li;
    protected ArrayList<Talk> talkList;
    private Context context;


    public ConversationListAdapter(Context context, ArrayList<Talk> talkList) {
        this.context = context;
        li = LayoutInflater.from(context);
        this.talkList = talkList;
    }



    /**
     * 用以子类重写该方法，当昵称未空时来设置默认昵称,若子类不重写则默认显示userId
     *
     * @return
     */
    protected String defaultName() {
        return null;
    }

    /**
     * 用以子类重写该方法，根据不同性别获取默认头像
     *
     * @return
     */
    protected int defaultAvatarRes(int gender) {
        return R.drawable.iv_boy_user;
    }

    public void updateOtherInfoLayout(View otherInfoLayout, Talk talk) {

    }

    protected class ViewHolder {
        public NetworkImageView iv_user_avatar;
        public ImageView iv_msg_status;
        public TextView tv_name, tv_msg_text, tv_msg_time, tv_msg_count;
        public View v_divider;
        public View other_info_layout;
    }

    @Override
    public int getCount() {
        if (talkList == null) {
            return 0;
        } else {
            return talkList.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (talkList != null) {
            return talkList.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = li.inflate(R.layout.gmacs_new_conversation_list_item, null);
            vh = new ViewHolder();
            vh.iv_user_avatar = (NetworkImageView) convertView.findViewById(R.id.iv_avatar);
            vh.tv_name = (TextView) convertView.findViewById(R.id.tv_conversation_name);
            vh.iv_msg_status = (ImageView) convertView.findViewById(R.id.iv_conversation_msg_status);
            vh.tv_msg_text = (TextView) convertView.findViewById(R.id.tv_conversation_msg_text);
            vh.tv_msg_time = (TextView) convertView.findViewById(R.id.tv_conversation_msg_time);
            vh.tv_msg_count = (TextView) convertView.findViewById(R.id.tv_conversation_msg_count);
            vh.v_divider = convertView.findViewById(R.id.v_conversation_divider);
            vh.other_info_layout = convertView.findViewById(R.id.conversation_list_other_info);
            convertView.setTag(vh);
            convertView.setTag(R.id.conversation_list_other_info, true);
        } else {
            vh = (ViewHolder) convertView.getTag();
            convertView.setTag(R.id.conversation_list_other_info, false);
        }

        Talk talk = talkList.get(position);

        Message lastMessage = talk.getmLastMessage();
        Message.MessageUserInfo messageUserInfo;
        String avatar = "";

        //根据最后一条消息的对方信息显示头像
        if (lastMessage != null) {
            messageUserInfo = lastMessage.getTalkOtherUserInfo();
            if (messageUserInfo != null) {
                vh.iv_user_avatar.setTag(messageUserInfo);
                if (null == talk.mTalkOtherUserRemark || null == messageUserInfo.gmacsUserInfo || TextUtils.isEmpty(messageUserInfo.gmacsUserInfo.userId)) {
                    UserInfoCacheManager.getInstance().updateTalkFromCache(talk, vh.iv_user_avatar, vh.tv_name, vh.other_info_layout);
                }
                if (messageUserInfo.gmacsUserInfo != null) {
                    avatar = messageUserInfo.gmacsUserInfo.avatar;
                }
            } else {
                vh.iv_user_avatar.setTag(null);
            }
        } else {
            vh.iv_user_avatar.setTag(null);
        }

        if (TextUtils.isEmpty(avatar)) {
            avatar = talk.mTalkOtherUserAvatar;
        }
        vh.iv_user_avatar.setDefaultImageResId(defaultAvatarRes(talk.mTalkOtherGender))
                .setErrorImageResId(defaultAvatarRes(talk.mTalkOtherGender))
                .setImageUrl(ImageUtil.makeUpUrl(avatar, IMG_RESIZE, IMG_RESIZE));
        vh.tv_name.setText(talk.getOtherName(context, defaultName()));

        // 消息发送状态
        int sendStateImgId = getSendStateImageSrcId(talk);
        if (sendStateImgId != -1) {
            vh.iv_msg_status.setVisibility(ImageView.VISIBLE);
            vh.iv_msg_status.setImageResource(sendStateImgId);
        } else {
            vh.iv_msg_status.setVisibility(ImageView.GONE);
        }

        vh.tv_msg_text.setText(talk.getNewestMsgContent(context));

        vh.tv_msg_time.setText(DateUtils.messageTimeFormat(talk.getTalkUpdatetime()));

        // 未读消息数
        if (talk.mNoReadMsgCount > 99) {
            vh.tv_msg_count.setText("99+");
            vh.tv_msg_count.setVisibility(TextView.VISIBLE);
        } else if (talk.mNoReadMsgCount <= 0) {
            vh.tv_msg_count.setVisibility(TextView.GONE);
        } else {
            vh.tv_msg_count.setText(String.valueOf(talk.mNoReadMsgCount));
            vh.tv_msg_count.setVisibility(TextView.VISIBLE);
        }

        if (position == talkList.size() - 1) {
            vh.v_divider.setBackgroundResource(R.color.transparent);
        } else {
            vh.v_divider.setBackgroundResource(R.color.conversation_list_divider);
        }
        return convertView;
    }

    private int getSendStateImageSrcId(Talk talk) {
        if (!TextUtils.isEmpty(talk.mDraftBoxMsg)) {
            return -1;
        } else if (talk.getmLastMessage() == null || talk.getmLastMessage().mMsgDetail == null) {
            return -1;
        } else if (talk.getmLastMessage().mMsgDetail.mIsSelfSendMsg) {
            if (talk.getmLastMessage().mMsgDetail.isMsgSending()) {
                return R.drawable.gmacs_ic_msg_sending_state;
            } else if (talk.getmLastMessage().mMsgDetail.isMsgSendFailed()) {
                return R.drawable.gmacs_ic_msg_sended_failed;
            }
        }
        return -1;
    }

}
