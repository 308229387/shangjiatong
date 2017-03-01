package com.android.gmacs.album;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.album.GmacsImageDao.ImgDir;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUtils;

import java.util.List;

/**
 * 照片目录 适配器
 */
public class GridImgDirAdapter extends BaseAdapter {

    private Context mContext;
    private List<ImgDir> dataList;

    private int itemWidth = 0;

    GridImgDirAdapter(Context c, List<ImgDir> dataList) {
        mContext = c;
        this.dataList = dataList;
        itemWidth = GmacsEnvi.screenWidth / 2;
    }

    @Override
    public int getCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    @Override
    public ImgDir getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gmacs_item_album_dir, parent, false);
            holder = new ViewHolder();
            holder.dirIcon = (NetworkImageView) convertView.findViewById(R.id.dirIcon);
            holder.dirIcon.getLayoutParams().width = itemWidth;
            holder.dirIcon.getLayoutParams().height = itemWidth - GmacsUtils.dipToPixel(16 + 8);
            holder.dirName = (TextView) convertView.findViewById(R.id.dirName);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = dataList.get(position).coverPath;
        holder.dirIcon.setDefaultImageResId(R.drawable.gmacs_bg_album_dir)
                .setErrorImageResId(R.drawable.gmacs_bg_album_dir)
                .setImageUrl(path);
        holder.count.setText(String.valueOf(dataList.get(position).dataList.size()));
        String name = dataList.get(position).dirName;
        if (TextUtils.isEmpty(name)) {
            holder.dirName.setText("未知");
        } else {
            holder.dirName.setText(name);
        }
        return convertView;
    }

    private class ViewHolder {
        NetworkImageView dirIcon;
        TextView dirName, count;
    }
}
