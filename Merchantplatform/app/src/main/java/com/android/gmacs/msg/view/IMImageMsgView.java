package com.android.gmacs.msg.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.activity.GmacsImageActivity;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.MsgContentType;
import com.common.gmacs.msg.data.IMImageMsg;
import com.common.gmacs.parse.message.Message;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ImageUtil;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoshuang on 2015/12/4.
 * 图片消息对应的view
 */
public class IMImageMsgView extends IMMessageView {

    private IMImageMsg mImageMsg;
    private NetworkImageView picImage;
    private ProgressBar downloadProgress;
    private TextView upLoadProgress;
    public static final int ImgResize = GmacsEnvi.appContext.getResources().getDimensionPixelOffset(R.dimen.im_chat_msg_pic_msg_width);
    public static final int MinResize = GmacsEnvi.appContext.getResources().getDimensionPixelOffset(R.dimen.im_chat_msg_pic_min_size);
    public static final String IMAGE_INFO = "com.android.gmacs.msg.view.IMAGE_INFO";
    public static final String IMAGE_INDEX = "com.android.gmacs.msg.view.IMAGE_INDEX";
    private boolean doubleClickDelegate;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMImageMsg) {
            mImageMsg = (IMImageMsg) msg;
        }
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        if (null != mImageMsg.parentMsg && mImageMsg.parentMsg.mIsSelfSendMsg) {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_right_picture, null);
        } else {
            mContentView = inflater.inflate(R.layout.gmacs_adapter_talk_item_content_left_picture, null);
        }

        picImage = (NetworkImageView) mContentView.findViewById(R.id.pic);
        downloadProgress = (ProgressBar) mContentView.findViewById(R.id.progress);
        upLoadProgress = (TextView) mContentView.findViewById(R.id.tv_load_progress);
        picImage.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(getContentView().getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                dialog.setListTexts(new String[]{mChatActivity.getString(R.string.delete_message)}).initDialog(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:// 删除消息
                                deleteIMMessageView();
                                dialog.dismiss();
                        }
                    }
                }).create().show();
                return true;
            }
        });

        super.initView(inflater);
        //查看图片
        setListenerForView(picImage);
        return mContentView;
    }

    public void setDataForView() {
        super.setDataForView();

        if (upLoadProgress != null) {
            upLoadProgress.setText((mImageMsg.progress > 99 ? 99 : mImageMsg.progress) + "%");
        }
        int[] scaleSize = ImageUtil.getScaleSize(mImageMsg.mWidth, mImageMsg.mHeight, ImgResize, ImgResize, MinResize, MinResize);
        int mWidth = scaleSize[0];
        int mHeight = scaleSize[1];
        int requestWidth = scaleSize[2];
        int requestHeight = scaleSize[3];
        RelativeLayout.LayoutParams para = (RelativeLayout.LayoutParams) picImage.getLayoutParams();
        para.width = mWidth;
        para.height = mHeight;
        picImage.setLayoutParams(para);
        picImage.setViewHeight(mHeight).setViewWidth(mWidth);

        // 是自己发送的消息
        if (mImageMsg.parentMsg.mIsSelfSendMsg) {
            picImage.setDefaultImageResId(R.drawable.gmacs_img_msg_default_right)
                    .setErrorImageResId(R.drawable.gmacs_img_msg_default_right);
            if (mImageMsg.mUrl.startsWith("/")) {
                picImage.setImageUrl(mImageMsg.mUrl);
            } else {
                picImage.setImageUrl(ImageUtil.makeUpUrl(mImageMsg.mUrl, requestHeight, requestWidth));
            }
            // 消息处于发送中显示进度条
            if (mImageMsg.parentMsg.isMsgSending()) {
                if (upLoadProgress != null) {
                    upLoadProgress.setVisibility(View.VISIBLE);
                }
            } else {
                if (upLoadProgress != null) {
                    upLoadProgress.setVisibility(View.GONE);
                }
            }
        } else {
            picImage.setDefaultImageResId(R.drawable.gmacs_img_msg_default_left)
                    .setErrorImageResId(R.drawable.gmacs_img_msg_default_left)
                    .setImageUrl(ImageUtil.makeUpUrl(mImageMsg.mUrl, requestHeight, requestWidth));
            downloadProgress.setVisibility(View.GONE);
            if (upLoadProgress != null) {
                upLoadProgress.setVisibility(View.GONE);
            }
        }
    }


    private void setListenerForView(final View view) {
        final Context context = view.getContext();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) { // 点击查看图片
                LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_TPCHK);
                // In case of IME disappears after starting new activity, it will make the animation in mass.
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                final Handler handler = new Handler();
                // Waiting for ResizeLayout onSizeChanged
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mChatActivity.isSoftInputHidden()) {
                            gotoImageActivity(view);
                        } else {
                            mChatActivity.resetSoftInputHiddenFlag();
                            // Waiting for SoftInput disappear
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    gotoImageActivity(view);
                                }
                            }, 500);
                        }
                    }
                }, 50);
            }
        });
    }

    private void gotoImageActivity(View v) {
        ArrayList<ImageInfoWrapper> imageInfos = new ArrayList<>();
        int index = filterPictureMsgs(imageInfos);
        if (imageInfos.size() > 0 && index != -1 && !doubleClickDelegate) {
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            Intent intent = new Intent(v.getContext(), GmacsImageActivity.class);
            intent.putExtra(IMAGE_INFO, imageInfos);
            intent.putExtra(IMAGE_INDEX, index);
            intent.putExtra("x", location[0]);
            intent.putExtra("y", location[1]);
            intent.putExtra("width", v.getWidth());
            intent.putExtra("height", v.getHeight());
            doubleClickDelegate = true;
            v.getContext().startActivity(intent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleClickDelegate = false;
                }
            }, 500);
        }
    }

    private int filterPictureMsgs(List<ImageInfoWrapper> imageInfoWrappers) {
        int index = -1;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            Message message = mAdapter.getItem(i);
            if (message.mMsgDetail.getmMsgContent().mType.equals(MsgContentType.TYPE_IMAGE)) {
                IMImageMsg temp = (IMImageMsg) message.mMsgDetail.getmMsgContent();
                ImageInfoWrapper imageInfoWrapper = new ImageInfoWrapper(temp.mUrl, temp.mWidth, temp.mHeight);
                imageInfoWrappers.add(imageInfoWrapper);
                if (mImageMsg == temp) {
                    index = imageInfoWrappers.size() - 1;
                }
            }
        }
        return index;
    }

    public static class ImageInfoWrapper implements Parcelable {
        public String mUrl;
        public String mWidth;
        public String mHeight;

        private ImageInfoWrapper(Parcel in) {
            mUrl = in.readString();
            mWidth = in.readString();
            mHeight = in.readString();
        }

        private ImageInfoWrapper(String mUrl, String mWidth, String mHeight) {
            this.mUrl = mUrl;
            this.mWidth = mWidth;
            this.mHeight = mHeight;
        }

        public static final Creator<ImageInfoWrapper> CREATOR = new Creator<ImageInfoWrapper>() {
            @Override
            public ImageInfoWrapper createFromParcel(Parcel in) {
                return new ImageInfoWrapper(in);
            }

            @Override
            public ImageInfoWrapper[] newArray(int size) {
                return new ImageInfoWrapper[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mUrl);
            dest.writeString(mWidth);
            dest.writeString(mHeight);
        }
    }

}
