package com.android.gmacs.msg.view;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.merchantplatform.R;
import com.android.gmacs.gif.GifDrawable;
import com.android.gmacs.gif.GifImageView;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.emoji.GifUtil;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.data.IMGifMsg;
import com.common.gmacs.utils.FileUtil;
import com.common.gmacs.utils.NetworkUtil;

/**
 * Created by caotongjun on 2016/4/12.
 * Gif消息视图
 */
public class IMGifMsgView extends IMMessageView {

    private IMGifMsg mGifMsg;
    public GifImageView gifView;
    private ImageView downFailed;
    private ProgressBar downloadProgress;

    protected View initView(LayoutInflater inflater) {
        if (mGifMsg.parentMsg.mIsSelfSendMsg) {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_right_gif, null);
        } else {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_left_gif, null);
        }
        downFailed = (ImageView) mContentView.findViewById(R.id.left_failed_down);
        downloadProgress = (ProgressBar) mContentView.findViewById(R.id.progress);
        gifView = (GifImageView) mContentView.findViewById(R.id.gif_view);
        gifView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                dialog.initDialog(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:// 删除消息
                                deleteIMMessageView();
                                break;
                            default:
                                break;
                        }
                    }
                }).setListTexts(new String[]{mChatActivity.getString(R.string.delete_message)}).create().show();
                return false;
            }
        });
        setListenerForFailed();
        super.initView(inflater);
        return mContentView;
    }

    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMGifMsg) {
            mGifMsg = (IMGifMsg) msg;
        }
    }

    /**
     * 为失败按钮加监听
     */
    private void setListenerForFailed() {
        if (downFailed != null) {
            downFailed.setVisibility(View.GONE);
            downFailed.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_TEXT_NEG_POS_BUTTON);
                    dialog.initDialog(R.string.retry_download_or_not, R.string.no, R.string.yes,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.cancel();
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 重新下载
//                                    download();
                                    dialog.dismiss();
                                }
                            }
                    ).create().show();
                }
            });
        }
    }

    public void setDataForView() {
        super.setDataForView();
        if (downFailed != null) {
            downFailed.setVisibility(View.GONE);
        }
        GifDrawable gifDrawable = GifUtil.getGifDrawable(mGifMsg.gifId);
        if (gifDrawable != null) {
            gifView.setGifImageDrawable(gifDrawable);
            // 是自己发送的消息
            if (mGifMsg.parentMsg.mIsSelfSendMsg) {
                // 消息处于发送中显示进度条
                if (mGifMsg.parentMsg.isMsgSending()) {
                    downloadProgress.setVisibility(View.VISIBLE);
                } else {
                    downloadProgress.setVisibility(View.GONE);
                }
            } else {
                downloadProgress.setVisibility(View.GONE);
            }

            if (downFailed != null) {
                downFailed.setVisibility(View.GONE);
            }

        } else {
            if (mGifMsg.parentMsg.mIsSelfSendMsg) {
                if (NetworkUtil.isNetworkAvailable()) {
                    download();
                } else {
                    gifView.setImageResource(R.drawable.gmacs_default_gif);
                    downloadProgress.setVisibility(View.GONE);
                }
            } else {
                download();
            }
        }
    }

    private void download() {
        gifView.setImageResource(R.drawable.gmacs_default_gif);
        if (!FileUtil.sdcardAvailable()) {
            // ToastUtil.showToast("sd卡不存在,请检查sd卡!");
            downloadProgress.setVisibility(View.GONE);
            if (downFailed != null) {
                downFailed.setVisibility(View.VISIBLE);
            }
            return;
        }
        downloadProgress.setVisibility(View.VISIBLE);
        if (downFailed != null) {
            downFailed.setVisibility(View.GONE);
        }
    }

}
