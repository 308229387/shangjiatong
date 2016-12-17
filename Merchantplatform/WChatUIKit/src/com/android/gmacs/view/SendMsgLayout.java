package com.android.gmacs.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.observer.OnInputSoftListener;
import com.android.gmacs.sound.SoundPlayer;
import com.android.gmacs.sound.SoundRecord;
import com.android.gmacs.view.emoji.FaceConversionUtil;
import com.android.gmacs.view.emoji.FaceRelativeLayout;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ToastUtil;

/**
 * 聊天页面底部工具view
 */
public class SendMsgLayout extends LinearLayout implements OnClickListener, OnInputSoftListener, TextWatcher, OnTouchListener {

    private EditText sendMessageEditText;
    private Button sendTextButton;
    private ImageView mSendVoice;
    private ImageView mSendMoreButton;
    private ImageView mSendEmojiButton;
    private ImageView mQuickButton;
    private Button mRecordVoice;
    public SendMoreLayout mSendMoreLayout;
    private FaceRelativeLayout mEmojiLayout;
    private ListView mQuickMsgListView;
    protected QuickMsgAdapter mQuickMsgAdapter;
    private View mQuickMsgLayout;
    public boolean inputSoftIsShow = false;
    private GmacsChatActivity gmacsChatActivity;
    private SoundRecord mRecord;
    public ImageView mPublicAccountMenuBtn;
    private boolean needShowSendMoreLayoutInOnHide;
    private boolean needShowEmojiLayoutInOnHide;

    public SendMsgLayout(Context context) {
        super(context);
    }

    public SendMsgLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SendMsgLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        gmacsChatActivity = (GmacsChatActivity) getContext();
        // 输入框
        sendMessageEditText = (EditText) findViewById(R.id.send_msg_edittext);
        sendMessageEditText.clearFocus();
        // 发送按钮
        sendTextButton = (Button) findViewById(R.id.send_text);
        mSendVoice = (ImageView) findViewById(R.id.send_voice_button);
        // 发送更多按钮
        mSendMoreButton = (ImageView) findViewById(R.id.send_more_button);
        mSendEmojiButton = (ImageView) findViewById(R.id.send_emoji_button);
        mRecordVoice = (Button) findViewById(R.id.record_voice);
        mSendMoreLayout = (SendMoreLayout) findViewById(R.id.send_more_layout);
        mEmojiLayout = (FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout);
        // 快速发送
        mQuickButton = (ImageView) findViewById(R.id.send_quick_button);
        mQuickMsgListView = (ListView) findViewById(R.id.quick_msg);
        mQuickMsgLayout = findViewById(R.id.send_quick_msg_layout);
        // 公众号菜单切换
        mPublicAccountMenuBtn = (ImageView) findViewById(R.id.iv_public_account_keyboard_down);

        mEmojiLayout.setMessageEditView(sendMessageEditText);
        mEmojiLayout.setGmacsChatActivity(gmacsChatActivity);

        mSendMoreLayout.setGmacsChatActivity(gmacsChatActivity);

        sendMessageEditText.addTextChangedListener(this);
        sendMessageEditText.setOnClickListener(this);
        mSendEmojiButton.setOnClickListener(this);
        mSendMoreButton.setOnClickListener(this);
        mSendVoice.setOnClickListener(this);
        sendTextButton.setOnClickListener(this);
        mRecordVoice.setOnTouchListener(this);
        mPublicAccountMenuBtn.setOnClickListener(this);
        switchSendText(false);
        if (gmacsChatActivity.sendAudioEnable()) {
            mSendVoice.setVisibility(View.VISIBLE);
        } else {
            mSendVoice.setVisibility(View.GONE);
        }
        if (gmacsChatActivity.sendEmojiEnable()) {
//            mSendEmojiButton.setVisibility(View.VISIBLE);
            mSendEmojiButton.setVisibility(View.GONE);
        } else {
            mSendEmojiButton.setVisibility(View.GONE);
        }
        if (gmacsChatActivity.sendMoreEnable()) {
            mSendMoreButton.setVisibility(View.VISIBLE);
        } else {
            mSendMoreButton.setVisibility(View.GONE);
        }
    }

    public void onClick(View v) {
        if (v == sendMessageEditText) {
            mSendMoreLayout.setVisibility(View.GONE);
            mEmojiLayout.hidden();
        } else if (v == mSendEmojiButton) {
            switchSendEmoji();
        } else if (v == mSendMoreButton) {
            switchSendMore();
        } else if (v == mSendVoice) {
            switchSendVoice();
        } else if (v == mQuickButton) {
            switchSendQuickMsg();
        } else if (v == sendTextButton) {
            sendTextMsg();
        } else if (v == mPublicAccountMenuBtn) {
            if (gmacsChatActivity.publicAccountMenu.isShown()) {
                gmacsChatActivity.publicAccountMenu.setVisibility(GONE);
                setVisibility(VISIBLE);
            } else {
                if (inputSoftIsShow) {
                    hiddenInputSoft();
                }
                gmacsChatActivity.publicAccountMenu.setVisibility(VISIBLE);
                setVisibility(GONE);
            }
        }
    }


    /**
     * 点击发送按钮处理逻辑
     */
    private void sendTextMsg() {
        String message = sendMessageEditText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            ToastUtil.showToast(getResources().getText(R.string.message_cannot_be_empty));
        } else if (isBlankOrCRLF(message)) {
            ToastUtil.showToast(getResources().getText(R.string.message_cannot_be_space_or_enter));
        } else {
            gmacsChatActivity.sendTextMsg(message);
            sendMessageEditText.setText("");
        }
    }

    /**
     * 录音按钮触摸事件处理方法
     */
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                GLog.d("audio_msg", "down");
                // 开始录音
                mRecordVoice.setText(getContext().getString(R.string.record_stop));
                mRecordVoice.setSelected(true);
                if (SoundPlayer.getInstance().ismIsSoundPlaying()) {
                    SoundPlayer.getInstance().stopPlayAndAnimation();
                }
                boolean hasPermission = true;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    boolean findMethod = true;
                    try {
                        ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        findMethod = false;
                    }
                    if (findMethod && ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                GmacsConstant.REQUEST_CODE_RECORD_AUDIO);
                        hasPermission = false;
                    } else {
                        hasPermission = true;
                    }
                    if (findMethod && ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getContext(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                GmacsConstant.REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                        hasPermission = false;
                    }
                }
                if (hasPermission) {
                    mRecord.startRecord(getContext(), true, new SoundRecord.RecordListener() {
                        public void onSuccessRecord(final String filePath, final int duration) {
                            post(new Runnable() {
                                public void run() {
                                    mRecordVoice.setText(getContext().getString(R.string.record_start));
                                    mRecordVoice.setSelected(false);
                                    gmacsChatActivity.sendAudioMsg(filePath, duration);
                                }
                            });
                        }
                        public void onFailedRecord() {
                        }
                    });
                }
                break;
            case MotionEvent.ACTION_MOVE:
                GLog.d("audio_msg", "move");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                GLog.d("audio_msg", "up");
                mRecordVoice.setText(getContext().getString(R.string.record_start));
                mRecordVoice.setSelected(false);
                if (!mRecord.isUserCancelRecord()) {
                    mRecord.stopRecord();
                }
                break;
        }
        return true;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * 文本转换成表情
     */
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        FaceConversionUtil.getInstace().replaceAllEmojis(GmacsEnvi.appContext, sendMessageEditText.getText());
    }

    /**
     * 处理输入框右边按钮显示状态。
     */
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s) && sendTextButton.getVisibility() == View.GONE) {
            sendTextButton.setVisibility(View.VISIBLE);
            mSendMoreButton.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(s) && sendTextButton.getVisibility() == View.VISIBLE) {
            sendTextButton.setVisibility(View.GONE);
            mSendMoreButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mRecord != null && mRecord.isRecording()) {
            if (mRecordVoice != null) {
                mRecordVoice.setText(getContext().getString(R.string.record_start));
                mRecordVoice.setSelected(false);
            }
            mRecord.stopRecord();
        }
    }

    /**
     * 是否包含空格或者换行符
     *
     * @param message
     * @return
     */
    public boolean isBlankOrCRLF(String message) {
        return TextUtils.isEmpty(message.trim());
    }

    /**
     * 隐藏键盘，表情框，更多框，快捷消息框。
     */
    public void collapseMoreAndInputMethod() {
        if (mSendMoreLayout.isShown())
            mSendMoreLayout.setVisibility(View.GONE);

        if (mEmojiLayout.faceViewShown())
            mEmojiLayout.hidden();

        mQuickMsgLayout.setVisibility(View.GONE);
        hiddenInputSoft();
    }

    /**
     * 当页面点击返回按钮处理逻辑
     *
     * @return 返回事件是否被处理过。
     */
    public boolean onBackPress() {
        if (mSendMoreLayout != null && mSendMoreLayout.isShown()) {
            mSendMoreLayout.setVisibility(View.GONE);
        } else if (mQuickMsgLayout != null && mQuickMsgLayout.isShown()) {
            mQuickMsgLayout.setVisibility(View.GONE);
        } else if (mEmojiLayout != null && mEmojiLayout.faceViewShown()) {
            mEmojiLayout.hidden();
        } else {
            return false;
        }
        return true;
    }

    public void setSendAudioEnable(boolean enable) {
        if (enable) {
            mSendVoice.setVisibility(View.VISIBLE);
        } else {
            mSendVoice.setVisibility(View.GONE);
        }
    }

    public void setSendEmojiEnable(boolean enable) {
        if (enable) {
//            mSendEmojiButton.setVisibility(View.VISIBLE);
            mSendEmojiButton.setVisibility(View.GONE);
        } else {
            mSendEmojiButton.setVisibility(View.GONE);
        }
    }
    public void setSendMoreEnable(boolean enable) {
        if (enable) {
            mSendMoreButton.setVisibility(View.VISIBLE);
        } else {
            mSendMoreButton.setVisibility(View.GONE);
        }
    }

    public void switchSendText() {
        switchSendText(true);
    }

    /**
     * 转换为发送文本
     */
    public void switchSendText(boolean showInpusoft) {
        mSendVoice.setImageResource(R.drawable.gmacs_ic_voice);
        mRecordVoice.setVisibility(View.GONE);
        sendMessageEditText.setVisibility(View.VISIBLE);
        mSendMoreLayout.setVisibility(View.GONE);
        mQuickMsgLayout.setVisibility(View.GONE);
        String contents = sendMessageEditText.getText().toString();
        if (!TextUtils.isEmpty(contents)) {
            sendTextButton.setVisibility(View.VISIBLE);
            mSendMoreButton.setVisibility(View.GONE);
        }
        mEmojiLayout.hidden();
        sendMessageEditText.requestFocus();
        if (showInpusoft) {
            showInputSoft();
        }
    }

    /**
     * 转换为发送语音
     */
    public void switchSendVoice() {
        if (mRecordVoice.isShown()) {
            switchSendText();
        } else {
            mSendVoice.setImageResource(R.drawable.gmacs_ic_keyboard);
            mRecordVoice.setVisibility(View.VISIBLE);
            mSendVoice.setVisibility(View.VISIBLE);
            sendMessageEditText.setVisibility(View.GONE);
            sendTextButton.setVisibility(View.GONE);
            mSendMoreLayout.setVisibility(View.GONE);
            mQuickMsgLayout.setVisibility(View.GONE);
            mEmojiLayout.hidden();
            hiddenInputSoft();
        }
    }

    /**
     * 切换到表情发送模式
     */
    public void switchSendEmoji() {
        mSendVoice.setImageResource(R.drawable.gmacs_ic_voice);
        mRecordVoice.setVisibility(View.GONE);
        sendMessageEditText.setVisibility(View.VISIBLE);
        mSendMoreLayout.setVisibility(View.GONE);
        mQuickMsgLayout.setVisibility(View.GONE);
        String contents = sendMessageEditText.getText().toString();
        if (!TextUtils.isEmpty(contents)) {
            sendTextButton.setVisibility(View.VISIBLE);
            mSendMoreButton.setVisibility(View.GONE);
        }
        hiddenInputSoft();
        if (!mEmojiLayout.faceViewShown() && inputSoftIsShow) {
            needShowEmojiLayoutInOnHide = true;
        } else if (!mEmojiLayout.faceViewShown()) {
            mEmojiLayout.show();
        } else {
            mEmojiLayout.hidden();
        }
    }

    /**
     * 切换到发送更多模式
     */
    public void switchSendMore() {
        sendMessageEditText.setVisibility(View.VISIBLE);
        mSendVoice.setImageResource(R.drawable.gmacs_ic_voice);
        mRecordVoice.setVisibility(View.GONE);
        mQuickMsgLayout.setVisibility(View.GONE);
        String contents = sendMessageEditText.getText().toString();
        if (!TextUtils.isEmpty(contents)) {
            sendTextButton.setVisibility(View.VISIBLE);
            mSendMoreButton.setVisibility(View.GONE);
        }
        mEmojiLayout.hidden();
        hiddenInputSoft();
        if (!mSendMoreLayout.isShown() && inputSoftIsShow) {
            needShowSendMoreLayoutInOnHide = true;
        } else if (!mSendMoreLayout.isShown()) {
            mSendMoreLayout.setVisibility(View.VISIBLE);
        } else {
            mSendMoreLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 切换到快捷回复模式
     */
    public void switchSendQuickMsg() {
        mRecordVoice.setVisibility(View.GONE);
        sendMessageEditText.setVisibility(View.VISIBLE);
        mSendMoreLayout.setVisibility(View.GONE);
        mEmojiLayout.hidden();
        String contents = sendMessageEditText.getText().toString();
        if (!TextUtils.isEmpty(contents)) {
            sendTextButton.setVisibility(View.VISIBLE);
            mSendMoreButton.setVisibility(View.GONE);
        }
        hiddenInputSoft();
        if (mQuickMsgLayout.getVisibility() == View.VISIBLE) {
            mQuickMsgLayout.setVisibility(View.GONE);
        } else {
            mQuickMsgLayout.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 隐藏键盘
     */
    public void hiddenInputSoft() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(sendMessageEditText.getApplicationWindowToken(), 0);
    }

    /**
     * 打开键盘
     */
    public void showInputSoft() {
        InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 键盘弹出
     */
    public void onShow() {
        inputSoftIsShow = true;
        switchSendText(false);
        gmacsChatActivity.popMsgUpOfSendMsgLayout();
    }

    /**
     * 键盘隐藏
     */
    public void onHide() {
        inputSoftIsShow = false;
        gmacsChatActivity.popMsgUpOfSendMsgLayout();
        if (needShowSendMoreLayoutInOnHide) {
            mSendMoreLayout.setVisibility(VISIBLE);
            needShowSendMoreLayoutInOnHide = false;
        } else if (needShowEmojiLayoutInOnHide){
            mEmojiLayout.show();
            needShowEmojiLayoutInOnHide = false;
        } else {
            gmacsChatActivity.hideSendMsgLayout();
        }
    }

    public void setRecord(SoundRecord record) {
        mRecord = record;
    }

    public void setGmacsChatActivity(GmacsChatActivity gmacsChatActivity) {
        this.gmacsChatActivity = gmacsChatActivity;
    }

    public void setMsgEditText(String msg) {
        sendMessageEditText.setText(msg);
        if (!TextUtils.isEmpty(msg)){
            sendMessageEditText.setSelection(msg.length());
        }
    }


    public String getMsgEditText() {
        return sendMessageEditText.getText().toString();
    }

    public View getRecordVoice() {
        return mRecordVoice;
    }


    public void registerOnMoreItemClick(SendMoreLayout.OnMoreItemClickListener onMoreItemClickListener){
        mSendMoreLayout.registerOnMoreItemClick(onMoreItemClickListener);
    }

    /**
     * There are only 3 default buttons.
     * <br><b>We support more than eight buttons, there is a ViewPager will be shown if possible.</b></br>
     * @param imgResId The resource id of image of buttons.
     * @param text The unique texts of buttons, which can identify buttons definitely.
     * @param isShowItemsSingleLinePreferred
     */
    public void setSendMoreItemResources(int[] imgResId, String[] text, boolean isShowItemsSingleLinePreferred) {
        mSendMoreLayout.setBtnImgResIds(imgResId);
        mSendMoreLayout.setBtnTexts(text);
        mSendMoreLayout.showItemsSingleLinePreferred(isShowItemsSingleLinePreferred);
        mSendMoreLayout.notifyData();
    }

    /**
     * 初始化快捷回复view
     */
    public void initQuickMsgView(String[] quickMsgs) {
        // 快捷回复
        if (quickMsgs != null) {
            // 若超了3个限制高度
            if (quickMsgs.length > 3) {
                ViewGroup.LayoutParams lp = mQuickMsgLayout.getLayoutParams();
                if (lp == null) {
                    lp = new LayoutParams(LayoutParams.FILL_PARENT, getResources().getDimensionPixelSize(R.dimen.msg_quick_view_max_height));
                } else {
                    lp.height = getResources().getDimensionPixelSize(R.dimen.msg_quick_view_max_height);
                }
                mQuickMsgLayout.setLayoutParams(lp);

            }
            mQuickButton.setOnClickListener(this);
            mQuickButton.setVisibility(View.VISIBLE);
            mQuickMsgAdapter = new QuickMsgAdapter();
            mQuickMsgListView.setAdapter(mQuickMsgAdapter);
            mQuickMsgAdapter.quickMsgContents = quickMsgs;
        } else {
            mQuickButton.setVisibility(View.GONE);
        }
        mQuickMsgLayout.setVisibility(View.GONE);
    }

    /**
     * 快捷回复adapter
     */
    protected class QuickMsgAdapter extends BaseAdapter {
        // 快捷回复内容
        public String[] quickMsgContents;

        public int getCount() {
            if (quickMsgContents != null) {
                return quickMsgContents.length;
            }
            return 0;
        }

        public Object getItem(int arg0) {
            return arg0;
        }

        public long getItemId(int arg0) {
            return arg0;
        }

        public View getView(final int arg0, View contentView, ViewGroup arg2) {
            if (contentView == null) {
                contentView = inflate(getContext(), R.layout.gmacs_item_quick_msg, null);
                ViewHold hold = new ViewHold();
                hold.quickMsgTV = (TextView) contentView.findViewById(R.id.quick_content_tv);
                contentView.setTag(hold);
            }
            ViewHold hold = (ViewHold) contentView.getTag();
            // 设置数据
            hold.quickMsgTV.setText(quickMsgContents[arg0]);
            hold.quickMsgTV.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (gmacsChatActivity.justPutQuickMsgToInput()) {
                        setMsgEditText(quickMsgContents[arg0]);
                    } else {
                        gmacsChatActivity.sendTextMsg(quickMsgContents[arg0]);
                    }
                }
            });
            return contentView;
        }

        private class ViewHold {
            TextView quickMsgTV;
        }

    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            gmacsChatActivity.setShouldShowInputSoftAuto(gmacsChatActivity.getShouldShowInputSoftAutoConfig());
        } else {
            gmacsChatActivity.setShouldShowInputSoftAuto(false);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                gmacsChatActivity.stopScroll();
                gmacsChatActivity.popMsgUpOfSendMsgLayout();
                break;
        }
        return !gmacsChatActivity.sendMsgLayoutTouchable;
    }
}
