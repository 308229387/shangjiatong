package com.android.gmacs.view;

import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.android.gmacs.utils.IMConstant;
import com.merchantplatform.R;
import com.android.gmacs.activity.GmacsWebViewActivity;
import com.android.gmacs.view.emoji.FaceConversionUtil;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.data.IMTextMsg;
import com.common.gmacs.utils.CopyPasteContent;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.StringUtil;
import com.common.gmacs.utils.ToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本消息对应的view
 */
public class IMTextMsgView extends IMMessageView {

    private IMTextMsg textMsg;
    private TextView msgContentTv;

    /**
     * 三星note3 4.4系统，长按事件执行后，会执行单击事件。所以做特殊处理
     */
    private boolean mIsShowClickEvent = true;

    protected View initView(LayoutInflater inflater) {
        //自己发送消息文本展现的样式
        if (null != textMsg.parentMsg && textMsg.parentMsg.mIsSelfSendMsg) {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_right_content_text, null);
            msgContentTv = (TextView) mContentView.findViewById(R.id.msg);
            msgContentTv.setTextColor(getContentView().getContext().getResources().getColor(R.color.chat_right));
        } else {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_left_content_text, null);
            msgContentTv = (TextView) mContentView.findViewById(R.id.msg);
            msgContentTv.setTextColor(getContentView().getContext().getResources().getColor(R.color.chat_left));
        }
        msgContentTv.setOnLongClickListener(new View.OnLongClickListener() {//长按复制粘贴
            @Override
            public boolean onLongClick(View v) {
                mIsShowClickEvent = false;
                String str[];
                if (type == IMConstant.EXTRA_TYPE_CUSTOM) {
                    str = new String[]{mChatActivity.getString(R.string.copy_message)};
                } else {
                    str = new String[]{mChatActivity.getString(R.string.copy_message), mChatActivity.getString(R.string.delete_message)};
                }
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                dialog.initDialog(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:// 复制消息
                                CopyPasteContent.copy(textMsg.mMsg, mChatActivity);
                                ToastUtil.showToast(mChatActivity.getText(R.string.copied));
                                break;
                            case 1:// 删除消息
                                deleteIMMessageView();
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).setListTexts(str).create().show();

                return true;
            }
        });
        super.initView(inflater);
        return mContentView;
    }

    @Override
    public void setDataForView() {
        super.setDataForView();
        if (textMsg != null) {
            msgContentTv.setText(textMsg.mMsg);
        }
        if (extractUrlOrPhoneNumber(msgContentTv)) {//处理特殊文字内容
        }
    }

    private boolean extractUrlOrPhoneNumber(TextView v) {
        SpannableString builder = FaceConversionUtil.getInstace().getExpressionString(textMsg.mMsg, 20);

        // 匹配网络地址
        Matcher m = StringUtil.getUrlPattern().matcher(textMsg.mMsg);
        if (m.find()) {
            final String url = textMsg.mMsg.substring(m.start(), m.end());
            builder.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    if (textMsg.parentMsg.mIsSelfSendMsg) {
                        ds.setColor(getContentView().getContext().getResources().getColor(R.color.chat_right_super_link));
                    } else {
                        ds.setColor(getContentView().getContext().getResources().getColor(R.color.chat_left_super_link));
                    }
                }

                // 在onClick方法中可以编写单击链接时要执行的动作
                @Override
                public void onClick(View widget) {
                    if (mIsShowClickEvent) {
                        Intent intent;
                        try {
                            intent = new Intent(mChatActivity, Class.forName(GmacsUiUtil.getBrowserClassName()));
                            intent.putExtra(GmacsWebViewActivity.EXTRA_TITLE, getContentView().getContext().getResources().getString(R.string.webview_title));
                            intent.putExtra(GmacsWebViewActivity.EXTRA_URL, url);
                            mChatActivity.startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    mIsShowClickEvent = true;
                }
            }, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 匹配电话号码
        Pattern numberPattern = Pattern.compile("\\+?(\\d*\\-)?\\d*");
        m = numberPattern.matcher(textMsg.mMsg);
        while (m.find()) {
            final String subNumbers = m.group();
            if (StringUtil.getNumberPattern().matcher(subNumbers).matches()) {
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        if (textMsg.parentMsg.mIsSelfSendMsg) {
                            ds.setColor(getContentView().getContext().getResources().getColor(R.color.chat_right_super_link));
                        } else {
                            ds.setColor(getContentView().getContext().getResources().getColor(R.color.chat_left_super_link));
                        }
                        ds.setUnderlineText(false);
                    }

                    // 在onClick方法中可以编写单击链接时要执行的动作
                    public void onClick(View widget) {
                        if (mIsShowClickEvent) {
                            final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                            dialog.initDialog(new AdapterView.OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    switch (position) {
                                        case 0:
                                            // 呼叫,如果之有一个可用的号码，这直接使用这个号码拨出
                                            mChatActivity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + subNumbers)));
                                            break;
                                        case 1:
                                            CopyPasteContent.copy(textMsg.mMsg, mChatActivity);
                                            ToastUtil.showToast(mChatActivity.getText(R.string.copied));
                                            break;
                                        default:
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            }).setListTexts(new String[]{mChatActivity.getString(R.string.call), mChatActivity.getString(R.string.copy)}).create().show();

                        }
                        mIsShowClickEvent = true;
                    }
                }, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        if (builder != null) {
            v.setText(builder);
            v.setMovementMethod(LinkMovementMethod.getInstance());
            return true;
        }
        return false;
    }

    @Override
    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMTextMsg) {
            this.textMsg = (IMTextMsg) msg;
        }
    }

}
