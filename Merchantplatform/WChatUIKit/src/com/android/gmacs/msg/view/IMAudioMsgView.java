package com.android.gmacs.msg.view;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.logic.MessageLogic;
import com.android.gmacs.sound.SoundPlayer;
import com.android.gmacs.view.GmacsDialog;
import com.common.gmacs.core.MediaToolManager;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.MsgContentType;
import com.common.gmacs.msg.data.IMAudioMsg;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.utils.GLog;
import com.xxganji.gmacs.proto.CommonPB;

import java.lang.ref.WeakReference;

/**
 * Created by zhangxiaoshuang on 2015/12/7.
 * 声音消息对应的view
 */
public class IMAudioMsgView extends IMMessageView {

    private IMAudioMsg mAudioMsg;
    private ImageView downFailed;
    private ImageView msgVoiceIv;
    private ImageView noRead;
    private ProgressBar loadProgress;
    private TextView duration;
    private View playImageLayout;

    /**
     * 声音view每一份的增长长度，声音view的宽度是与声音时长有关系的
     */
    private static int sWidthOnce = -1;

    // 声音view的最大最小宽度
    private static int sVoiceViewMinWidth = -1, sVoiceViewMaxWidth = -1;

    @Override
    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMAudioMsg) {
            mAudioMsg = (IMAudioMsg) msg;
        }
    }

    private void setData(String url) {
        if (downFailed != null) {
            downFailed.setVisibility(View.GONE);
        }
        loadProgress.setVisibility(View.GONE);
        msgVoiceIv.setVisibility(View.VISIBLE);
        msgVoiceIv.setTag(url);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 把消息置为已经读过
                GLog.d("MsgPlayStatus", String.valueOf(mAudioMsg.parentMsg.getMsgPlayStatus()));
                if (!mAudioMsg.parentMsg.mIsSelfSendMsg && mAudioMsg.parentMsg.getMsgPlayStatus() == CommonPB.PlayStatus.MSG_NOT_PLAYED) {
                    noRead.setVisibility(View.GONE);
                    mAudioMsg.parentMsg.setMsgPlayStatus(CommonPB.PlayStatus.MSG_PLAYED);
                    mAudioMsg.style = null;
                    MessageLogic.getInstance().updatePlayStatusByMsgId(mAudioMsg.parentMsg.mMessage.mId, 1);
                }
                SoundPlayer.getInstance().startPlaying(msgVoiceIv.getTag().toString(),
                        new VoiceOnCompletionListener(mAudioMsg.parentMsg.mMessage.mId, IMAudioMsgView.this),
                        mAudioMsg.parentMsg.mMessage.mId);
                playOrStopAnimate();
            }
        });
        playOrStopAnimate();
    }

    private void playOrStopAnimate() {
        // 若是该item播放，则进行播放动画
        if (SoundPlayer.getInstance().currentPlayId() == mAudioMsg.parentMsg.mMessage.mId) {
            // 进行动画
            Drawable d = msgVoiceIv.getBackground();
            if (d != null) {
                final AnimationDrawable ani = (AnimationDrawable) d;
                ani.stop();
                ani.start();
            } else {
                if (mAudioMsg.parentMsg.mIsSelfSendMsg) {
                    msgVoiceIv.setBackgroundResource(R.drawable.gmacs_anim_right_sound);
                } else {
                    msgVoiceIv.setBackgroundResource(R.drawable.gmacs_anim_left_sound);
                }

                msgVoiceIv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        final AnimationDrawable ani = (AnimationDrawable) msgVoiceIv.getBackground();
                        if (ani != null) {
                            ani.start();
                        }
                        return true;
                    }
                });

            }
            msgVoiceIv.setImageDrawable(null);

        } else {
            Drawable d = msgVoiceIv.getBackground();
            if (d != null) {
                final AnimationDrawable ani = (AnimationDrawable) d;
                ani.stop();
                msgVoiceIv.setBackgroundResource(0);
            }
            setResourceForVoiceView();
        }
    }

    private void setResourceForVoiceView() {
        if (mAudioMsg.parentMsg.mIsSelfSendMsg) {
            msgVoiceIv.setImageResource(R.drawable.gmacs_ic_right_sound3);
        } else {
            msgVoiceIv.setImageResource(R.drawable.gmacs_ic_left_sound3);
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        if (mAudioMsg.parentMsg.mIsSelfSendMsg) {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_right_voice, null);
        } else {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_left_voice, null);
        }
        downFailed = (ImageView) mContentView.findViewById(R.id.left_failed_down);
        msgVoiceIv = (ImageView) mContentView.findViewById(R.id.play_img);
        noRead = (ImageView) mContentView.findViewById(R.id.voice_no_read);
        loadProgress = (ProgressBar) mContentView.findViewById(R.id.progress);
        duration = (TextView) mContentView.findViewById(R.id.duration);
        playImageLayout = mContentView.findViewById(R.id.play_img_layout);
        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                dialog.initDialog(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:// 删除消息
                                deleteIMMessageView();
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).setListTexts(new String[]{mChatActivity.getString(R.string.delete_message)}).create().show();
                return false;
            }
        });
        super.initView(inflater);
        setListenerForFailed();
        return mContentView;
    }

    @Override
    public void setDataForView() {
        super.setDataForView();
        if (mAudioMsg.parentMsg.mIsSelfSendMsg) {
            if (!TextUtils.isEmpty(mAudioMsg.mLocalUrl)) {
                setData(mAudioMsg.mLocalUrl);
            } else if (!TextUtils.isEmpty(mAudioMsg.mUrl)) {
                if (mAudioMsg.mUrl.startsWith("/")) {
                    setData(mAudioMsg.mUrl);
                } else {
                    download(mAudioMsg.mUrl);
                }
            }
        } else if (!TextUtils.isEmpty(mAudioMsg.mUrl)) {
            download(mAudioMsg.mUrl);
        }
        if (noRead != null) {
            if (mAudioMsg.parentMsg.getMsgPlayStatus() == CommonPB.PlayStatus.MSG_PLAYED) {
                noRead.setVisibility(View.GONE);
            } else {
                noRead.setVisibility(View.VISIBLE);
            }
        }
        setVoiceViewWidthByDuration();
    }

    /**
     * 根据时长设置声音view的宽度
     */
    private void setVoiceViewWidthByDuration() {
        // 先计算下每一份应增长的宽度
        if (sWidthOnce < 0) {
            if (sVoiceViewMaxWidth < 0) {
                sVoiceViewMaxWidth = mChatActivity.getResources().getDimensionPixelOffset(R.dimen.im_voice_max_width);
            }
            if (sVoiceViewMinWidth < 0) {
                sVoiceViewMinWidth = mChatActivity.getResources().getDimensionPixelOffset(R.dimen.im_voice_min_width);
            }
            sWidthOnce = (sVoiceViewMaxWidth - sVoiceViewMinWidth) / 13;
        }
        int voiceViewWidth;
        // 小于等于10以下的是没1s增长一份，1-2s定一个最小值
        if (mAudioMsg.mDuration <= 10) {
            if (mAudioMsg.mDuration > 0 && mAudioMsg.mDuration <= 2) {
                voiceViewWidth = sVoiceViewMinWidth;
            } else {
                voiceViewWidth = (int) (sVoiceViewMinWidth + ((mAudioMsg.mDuration - 2) * sWidthOnce));
            }
        } else if (mAudioMsg.mDuration > 10 && mAudioMsg.mDuration <= 60) {
            voiceViewWidth = (int) (sVoiceViewMinWidth + ((10 - 2) * sWidthOnce) + (mAudioMsg.mDuration / 10) * sWidthOnce);
        } else {
            voiceViewWidth = sVoiceViewMaxWidth;
        }

        ViewGroup.LayoutParams lp = playImageLayout.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(voiceViewWidth, mChatActivity.getResources().getDimensionPixelOffset(R.dimen.im_voice_height));
        } else {
            lp.width = voiceViewWidth;
        }
        playImageLayout.setLayoutParams(lp);
        duration.setVisibility(View.VISIBLE);
        duration.setText(mAudioMsg.mDuration + "''");
    }

    /**
     * 为下载失败设置views
     */
    private void setViewsForDownloadFailed() {
        msgVoiceIv.setVisibility(View.VISIBLE);
        msgVoiceIv.setTag("");
        loadProgress.setVisibility(View.GONE);
        duration.setVisibility(View.GONE);
        if (downFailed != null) {
            downFailed.setVisibility(View.VISIBLE);
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
                                    download(mAudioMsg.mUrl);
                                    dialog.dismiss();
                                }
                            }
                    ).create().show();
                }
            });
        }
    }

    static class VoiceOnCompletionListener implements SoundPlayer.VoiceCompletion {
        private WeakReference<IMAudioMsgView> imAudioMsgViewWeakReference;

        VoiceOnCompletionListener(long currentPlayId, IMAudioMsgView imAudioMsgView) {
            this.currentPlayId = currentPlayId;
            imAudioMsgViewWeakReference = new WeakReference<>(imAudioMsgView);
        }

        private long currentPlayId = -2;

        @Override
        public void onCompletion(MediaPlayer mp, final boolean isNormal) {
            final IMAudioMsgView imAudioMsgView = imAudioMsgViewWeakReference.get();
            if (imAudioMsgView != null) {
                final GmacsChatActivity mChatActivity = imAudioMsgView.mChatActivity;
                mChatActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isNormal) {
                            IMAudioMsg msg = getNextVoiceMsg();
                            if (msg != null) {
                                msg.parentMsg.setMsgPlayStatus(CommonPB.PlayStatus.MSG_PLAYED);
                                msg.style = null;
                                MessageLogic.getInstance().updatePlayStatusByMsgId(msg.parentMsg.mMessage.mId, 1);
                                currentPlayId = msg.parentMsg.mMessage.mId;
                                SoundPlayer.getInstance().autoStartPlaying(msg.mUrl, VoiceOnCompletionListener.this, msg.parentMsg.mMessage.mId);
                            } else {
                                currentPlayId = -2;
                            }
                        } else {
                            currentPlayId = -2;
                        }
                        imAudioMsgView.mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        private IMAudioMsg getNextVoiceMsg() {
            final IMAudioMsgView imAudioMsgView = imAudioMsgViewWeakReference.get();
            if (imAudioMsgView != null) {
                int count = imAudioMsgView.mAdapter.getCount();
                int current = 0;
                boolean isSelfSendMsg = false;
                for (int i = 0; i < count; i++) {
                    Message message = imAudioMsgView.mAdapter.getItem(i);
                    if (message.mMsgDetail.getmMsgContent().parentMsg.mMessage.mId == currentPlayId) {
                        current = i;
                        isSelfSendMsg = message.mMsgDetail.getmMsgContent().parentMsg.mIsSelfSendMsg;
                        break;
                    }
                }
                for (int i = current + 1; i < count; i++) {
                    Message message = imAudioMsgView.mAdapter.getItem(i);
                    if (message.mMsgDetail.getmMsgContent().mType.equals(MsgContentType.TYPE_AUDIO)) {
                        try {
                            IMAudioMsg temp = (IMAudioMsg) message.mMsgDetail.getmMsgContent();
                            if (isSelfSendMsg) {
                                if (temp.parentMsg.mIsSelfSendMsg) {
                                    return null;
                                }
                            } else {
                                if (temp.parentMsg.mIsSelfSendMsg) {
                                    continue;
                                }
                            }
                            if (temp.parentMsg.getMsgPlayStatus() == CommonPB.PlayStatus.MSG_NOT_PLAYED) {
                                return temp;
                            } else {
                                return null;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return null;
            } else {
                return null;
            }
        }
    }

    private void download(String url) {
        MediaToolManager.getInstance().downloadAudioFile(url, new DownloadAudioListener(this));
    }

    static class DownloadAudioListener implements MediaToolManager.DownloadAudioListener {
        WeakReference<IMAudioMsgView> imAudioMsgViewWeakReference;

        DownloadAudioListener(IMAudioMsgView imAudioMsgView) {
            imAudioMsgViewWeakReference = new WeakReference<>(imAudioMsgView);
        }

        @Override
        public void onDone(int errorCode, String errorMessage, String requestUrl, String filePath) {
            IMAudioMsgView imAudioMsgView = imAudioMsgViewWeakReference.get();
            if (imAudioMsgView != null && requestUrl.equals(imAudioMsgView.mAudioMsg.mUrl)) {
                GLog.d(imAudioMsgView.TAG, "downloadAudioFile.downloadAudioFileAsync.code=" + errorCode + ",downloadImageFile.downloadAsync.error=" + errorMessage + ",local_path=" + filePath);
                if (errorCode == 0) {
                    imAudioMsgView.setData(filePath);
                } else {
                    imAudioMsgView.setViewsForDownloadFailed();
                }
            }
        }
    }
}
