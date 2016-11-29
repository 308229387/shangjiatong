package com.android.gmacs.msg.view;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsMapActivity;
import com.common.gmacs.msg.IMMessage;
import com.common.gmacs.msg.data.IMLocationMsg;
import com.common.gmacs.utils.GmacsEnvi;

/**
 * Created by zhangxiaoshuang on 2015/12/7.
 * 位置消息view
 */
public class IMLocationMsgView extends IMMessageView {
    private IMLocationMsg mLocationMsg;
    private TextView mTvLocation;
    private RelativeLayout mRlLocation;
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";
    public static final String ADDRESS = "address";
    private static final int sMapWidth = GmacsEnvi.appContext.getResources().getDimensionPixelOffset(R.dimen.im_chat_msg_pic_msg_width);
    private static final int sMapHeight = sMapWidth;

    @Override
    protected View initView(LayoutInflater inflater) {
        if(mLocationMsg.parentMsg.mIsSelfSendMsg){
            mContentView = inflater.inflate(
                    R.layout.gmacs_adapter_talk_item_content_right_map, null);
        }else{
            mContentView = inflater.inflate(
                    R.layout.gmacs_adapter_talk_item_content_left_map, null);
        }
        mContentView.setLayoutParams(new LinearLayout.LayoutParams(sMapWidth, sMapHeight));
        mRlLocation=(RelativeLayout)mContentView.findViewById(R.id.rl_location);
        mTvLocation=(TextView)mContentView.findViewById(R.id.tv_location);
        mRlLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mTvLocation.getContext(), GmacsMapActivity.class);
                intent.putExtra(LONGITUDE,mLocationMsg.mLongitude);
                intent.putExtra(LATITUDE,mLocationMsg.mLatitude);
                intent.putExtra(ADDRESS,mLocationMsg.mAddress);
                mTvLocation.getContext().startActivity(intent);
            }
        });
        super.initView(inflater);
        return mContentView;
    }

    @Override
    public void setDataForView() {
        super.setDataForView();
        if(mLocationMsg!=null){
            mTvLocation.setText(mLocationMsg.mAddress);
        }

    }

    @Override
    protected void setIMMessage(IMMessage msg) {
        if (msg instanceof IMLocationMsg) {
            this.mLocationMsg = (IMLocationMsg) msg;
        }


    }
}
