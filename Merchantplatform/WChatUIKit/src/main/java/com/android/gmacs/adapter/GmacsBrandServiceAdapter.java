package com.android.gmacs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wchatuikit.R;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.parse.pubcontact.PublicContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoshuang on 2015/11/27.
 * 公众号列表适配器
 */
public class GmacsBrandServiceAdapter extends BaseAdapter {

    private Context mContext;
    private List<PublicContactInfo> user = new ArrayList<>();

    public GmacsBrandServiceAdapter(Context context, List<PublicContactInfo> user) {
        this.mContext = context;
        this.user = user;
    }

    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int position) {
        return user.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gmacs_contact_list_item, parent, false);
            viewHolder.mTvContactName = (TextView) convertView.findViewById(R.id.tv_contact_name);
            viewHolder.mIvContactAvatar = (NetworkImageView) convertView.findViewById(R.id.iv_avatar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mIvContactAvatar
                .setDefaultImageResId(R.drawable.gmacs_ic_default_avatar)
                .setErrorImageResId(R.drawable.gmacs_ic_default_avatar)
                .setImageUrl(user.get(position).getAvatar());
        viewHolder.mTvContactName.setText(user.get(position).getUser_name());
        return convertView;
    }

    private class ViewHolder {
        NetworkImageView mIvContactAvatar;
        TextView mTvContactName;
    }
}
