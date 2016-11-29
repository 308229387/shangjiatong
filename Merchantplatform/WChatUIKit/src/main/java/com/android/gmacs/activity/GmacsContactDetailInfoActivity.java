package com.android.gmacs.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wchatuikit.R;
import com.android.gmacs.event.AddContactMsgEvent;
import com.android.gmacs.event.RemarkEvent;
import com.android.gmacs.event.StarEvent;
import com.android.gmacs.event.UpdateInsertChatMsgEvent;
import com.android.gmacs.logic.ContactLogic;
import com.android.gmacs.logic.TalkLogic;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.core.MessageManager;
import com.common.gmacs.msg.data.IMTextMsg;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.ImageUtil;
import com.common.gmacs.utils.ToastUtil;
import com.xxganji.gmacs.proto.CommonPB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;

/**
 * Created by zhangxiaoshuang on 2015/11/23.
 */

/**
 * 联系人详细资料
 */
public class GmacsContactDetailInfoActivity extends BaseActivity implements OnClickListener {
    private TextView mTvContactDetailName, mTvContactRemarkContent;
    protected String userId;
    protected int userSource;
    protected String deviceId = "";
    protected String openId = "";
    protected Contact userInfo;
    protected boolean mStar;
    private TextView mTvContactDetailPhoneNum;
    private TextView mTvContactDetailRename;
    private TextView mTvContactChatbtn;
    private NetworkImageView mIvAvatar;
    private RelativeLayout mRlContactDetailRemarkContentInfo;
    private RelativeLayout mRlContactDetailRemark;
    private LinearLayout mLlContactDetailPhoneAll;
    public int talkType;
    private View mContactLine;

    @Override
    protected void initView() {
        setTitle(getText(R.string.contact_detail));
        mIvAvatar = (NetworkImageView) findViewById(R.id.iv_avatar);
        mTvContactDetailName = (TextView) findViewById(R.id.tv_contact_detail_name);
        mTvContactDetailRename = (TextView) findViewById(R.id.tv_contact_detail_rename);
        mTvContactChatbtn = (TextView) findViewById(R.id.tv_contact_chatbtn);
        RelativeLayout mLlContactDetailPhone = (RelativeLayout) findViewById(R.id.rl_contact_detail_phone);
        mTvContactDetailPhoneNum = (TextView) findViewById(R.id.tv_contact_detail_phone_num);
        mTvContactRemarkContent = (TextView) findViewById(R.id.tv_contact_remark_content);
        mLlContactDetailPhoneAll = (LinearLayout) findViewById(R.id.ll_contact_detail_phone_all);
        mRlContactDetailRemarkContentInfo = (RelativeLayout) findViewById(R.id.rl_contact_detail_remark_content_info);
        RelativeLayout mRlContactDetailRemarkContent = (RelativeLayout) findViewById(R.id.rl_contact_detail_remark_content);
        mRlContactDetailRemark = (RelativeLayout) findViewById(R.id.rl_contact_detail_remark);
        mContactLine = findViewById(R.id.contact_line);
        if (mTvContactChatbtn != null) {
            mTvContactChatbtn.setOnClickListener(this);
        }
        mLlContactDetailPhone.setOnClickListener(this);
        mRlContactDetailRemarkContent.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_contact_detail_info);
    }

    @Override
    protected void initData() {
        userId = getIntent().getStringExtra(GmacsConstant.EXTRA_USER_ID);
        userSource = getIntent().getIntExtra(GmacsConstant.EXTRA_USER_SOURCE, 0);
        deviceId = getIntent().getStringExtra(GmacsConstant.EXTRA_DEVICEID);
        openId = getIntent().getStringExtra(GmacsConstant.EXTRA_OPENID);
        if (userId == null) {
            mRlContactDetailRemark.setVisibility(View.GONE);
        }
        talkType = getIntent().getIntExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
        mTvContactDetailName.setText(userInfo == null ? "" : userInfo.getUserName());
        EventBus.getDefault().register(this);
        ContactLogic.getInstance().getUserInfo(userId, userSource);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_contact_chatbtn) { // 聊天
            Message.MessageUserInfo messageUserInfo = new Message.MessageUserInfo();
            messageUserInfo.mUserId = userId;
            messageUserInfo.mUserSource = userSource;
            messageUserInfo.mTalkType = talkType;
            messageUserInfo.mDeviceId = deviceId;
            messageUserInfo.mOpenId = openId;
//            messageUserInfo.mUserName = userInfo == null ? "" : userInfo.getUserName();
//            messageUserInfo.mAvatar = userInfo == null ? "" : userInfo.getAvatar();
//            GLog.d(TAG, messageUserInfo.toString());
            Intent intent = new Intent(GmacsUiUtil.createToChatActivity(this, "", messageUserInfo));
            startActivity(intent);
            overridePendingTransition(R.anim.gmacs_push_left_in, R.anim.gmacs_push_left_out);
        } else if (i == R.id.rl_contact_detail_phone) { // 打电话
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mTvContactDetailPhoneNum.getText()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (i == R.id.rl_contact_detail_remark_content) { // 备注
            Intent intent = new Intent(this, GmacsContactRemarkActivity.class);
            intent.putExtra(GmacsConstant.EXTRA_USER_ID, userId);
            intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, userSource);
            if (userInfo != null) {
                intent.putExtra(GmacsConstant.EXTRA_REMARK, userInfo.remark);
            }
            startActivity(intent);
        }
    }

    protected void updateTitleBar(final boolean isContact) {
        mTitleBar.setRightImageView(R.drawable.gmacs_ic_contact_edit);
        mTitleBar.setSubRightImageView(isContact ? (mStar ? R.drawable.gmacs_ic_stars : R.drawable.gmacs_ic_unstars) : 0);
        mTitleBar.setRightImageViewListener(new OnClickListener() {//编辑
            @Override
            public void onClick(View v) {
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(v.getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                if (isContact) {
                    dialog.initDialog(new AdapterView.OnItemClickListener() {
                        Intent intent;

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0: // 备注
                                    intent = new Intent(GmacsContactDetailInfoActivity.this, GmacsContactRemarkActivity.class);
                                    intent.putExtra(GmacsConstant.EXTRA_USER_ID, userId);
                                    intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, userSource);
                                    if (userInfo != null) {
                                        intent.putExtra("remark", userInfo.remark);
                                    }
                                    startActivity(intent);
                                    dialog.dismiss();
                                    break;
                                case 1: // 举报
                                    intent = new Intent(GmacsContactDetailInfoActivity.this, GmacsContactReportActivity.class);
                                    intent.putExtra(GmacsConstant.EXTRA_USER_ID, userId);
                                    intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, userSource);
                                    startActivity(intent);
                                    dialog.dismiss();
                                    break;
                                case 2: // 删除
                                    ContactLogic.getInstance().delContact(userId, userSource);
                                    dialog.dismiss();
                                    finish();
                                    break;
                            }
                        }
                    }).setListTexts(new String[]{
                            getString(com.wchatuikit.R.string.edit_remark), getString(com.wchatuikit.R.string.report),
                            getString(com.wchatuikit.R.string.delete_contact)}).create().show();
                } else {
                    dialog.initDialog(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0: // 添加
                                    ContactLogic.getInstance().addContact(userId, userSource);
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    }).setListTexts(new String[]{getString(com.wchatuikit.R.string.add_contact)}).create().show();
                }
            }
        });
        mTitleBar.setSubRightImageViewListener(new OnClickListener() {//星标
            @Override
            public void onClick(View v) {
                if (ClientManager.getInstance().getConnectionStatus() == CommonPB.ConnectionStatus.STATUS_CONNECTED_VALUE) {
                    if (mStar) {
                        ContactLogic.getInstance().unStar(userId, userSource);
                    } else {
                        ContactLogic.getInstance().star(userId, userSource);
                    }
                } else {
                    ToastUtil.showToast(getText(R.string.connection_error_or_kickedoff));
                }
            }
        });
    }

    private void updateRemarkInfo() {
        if (TextUtils.isEmpty(userInfo.remark.remark_telephone) && TextUtils.isEmpty(userInfo.remark.remark_info)) {
            mRlContactDetailRemark.setVisibility(View.GONE);
        } else {
            mRlContactDetailRemark.setVisibility(View.VISIBLE);
        }
        // 备注名字
        if (!TextUtils.isEmpty(userInfo.remark.remark_name)) {
            mTvContactDetailRename.setVisibility(View.VISIBLE);
            mTvContactDetailRename.setText(userInfo.remark.remark_name);
        } else {
            mTvContactDetailRename.setVisibility(View.GONE);
        }
        // 备注电话
        if (!TextUtils.isEmpty(userInfo.remark.remark_telephone)) {
            mLlContactDetailPhoneAll.setVisibility(View.VISIBLE);
            mTvContactDetailPhoneNum.setText(userInfo.remark.remark_telephone);
        } else {
            mLlContactDetailPhoneAll.setVisibility(View.GONE);
        }
        // 备注信息
        if (!TextUtils.isEmpty(userInfo.remark.remark_info)) {
            mRlContactDetailRemarkContentInfo.setVisibility(View.VISIBLE);
            mTvContactRemarkContent.setText(userInfo.remark.remark_info);
            mContactLine.setVisibility(TextUtils.isEmpty(userInfo.remark.remark_telephone) ? View.GONE : View.VISIBLE);
        } else {
            mRlContactDetailRemarkContentInfo.setVisibility(View.GONE);
            mContactLine.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStarEvent(StarEvent event) {
        if (userId.equals(event.getUserId()) && userSource == event.getUserSource()) {
            if (event.isStar()) {
                ToastUtil.showToast(getText(R.string.starred_ok));
                mTitleBar.setSubRightImageView(R.drawable.gmacs_ic_stars);
                mStar = true;
            } else {
                ToastUtil.showToast(getText(R.string.unstarred_ok));
                mTitleBar.setSubRightImageView(R.drawable.gmacs_ic_unstars);
                mStar = false;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOtherUserInfo(Contact contact) {
        if (userId.equals(contact.getUserId())
                && userSource == contact.getUserSource()) {
            userInfo = contact;
            mStar = userInfo.isStar();
            String showName = userInfo.getUserName();
            mTvContactDetailName.setText(showName);
            mIvAvatar.setDefaultImageResId(R.drawable.gmacs_ic_default_avatar)
                    .setErrorImageResId(R.drawable.gmacs_ic_default_avatar)
                    .setImageUrl(ImageUtil.makeUpUrl(userInfo.getAvatar(), IMG_RESIZE, IMG_RESIZE));

            if (!userId.equals(Gmacs.getInstance().getGmacsUserInfo().userId)
                    || userSource != Gmacs.getInstance().getGmacsUserInfo().userSource) {
                updateTitleBar(userInfo.isContact());
                mTvContactChatbtn.setVisibility(View.VISIBLE);
            } else {
                mTitleBar.setSubRightImageView(0);
                mTitleBar.setRightImageView(0);
                mTvContactChatbtn.setVisibility(View.GONE);
            }

            if (Gmacs.TalkType.TALKETYPE_OFFICIAL.getValue() == talkType) {
                mTvContactDetailRename.setVisibility(View.GONE);
                mRlContactDetailRemark.setVisibility(View.GONE);
            } else {
                updateRemarkInfo();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemark(RemarkEvent event) {
        if (userId.equals(event.getUserId()) && userSource == event.getUserSource()) {
            if (Gmacs.TalkType.TALKETYPE_OFFICIAL.getValue() != talkType) {
                userInfo.remark = event.getRemark();
                updateRemarkInfo();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddContactMsg(AddContactMsgEvent event) {
        ToastUtil.showToast(getText(R.string.add_contact_ok));
        updateTitleBar(true);
        mTitleBar.setSubRightImageView(R.drawable.gmacs_ic_unstars);
        int talkType = Gmacs.TalkType.TALKETYPE_NORMAL.getValue();
        Message.MessageUserInfo sendInfo = new Message.MessageUserInfo();
        sendInfo.mUserId = event.getContactId();
        sendInfo.mUserSource = event.getContactSource();
        sendInfo.mTalkType = talkType;
        sendInfo.mDeviceId = "";
        Message.MessageUserInfo receiverInfo = Message.MessageUserInfo.createLoginUserInfo();
        IMTextMsg imTextMsg = new IMTextMsg();
        imTextMsg.mMsg = getString(R.string.add_contact_accept_msg);

        MessageManager.getInstance().insertLocalMessage(talkType, sendInfo, receiverInfo, "", imTextMsg, false,
                new MessageManager.InsertLocalMessageCb() {
                    @Override
                    public void onInsertLocalMessage(int errorCode, String errorMessage, Message message) {
                        if (errorCode == 0) {
                            TalkLogic.getInstance().getRecentTalks();
                            EventBus.getDefault().post(new UpdateInsertChatMsgEvent(message));
                        }
                    }
                });
    }
}

