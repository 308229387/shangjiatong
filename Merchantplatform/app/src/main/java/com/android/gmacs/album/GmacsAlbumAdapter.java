package com.android.gmacs.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.merchantplatform.R;
import com.android.gmacs.view.NetworkImageView;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 相册的适配器
 */
public class GmacsAlbumAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> dataList;
    private ArrayList<String> selectedDataList;
    private final int ITEM_WIDTH;
    private int mMaxCount;

    public GmacsAlbumAdapter(Context c, ArrayList<String> dataList, ArrayList<String> selectedDataList,int maxCount) {
        mContext = c;
        this.dataList = dataList;
        this.selectedDataList = selectedDataList;
        mMaxCount = maxCount;
        ITEM_WIDTH = GmacsEnvi.screenWidth / 3;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        public ImageView checkBox;
        public NetworkImageView imageView;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (position > dataList.size() || position < 0) {
            return null;
        }

        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gmacs_item_album_select_img, parent, false);
            convertView.getLayoutParams().height = ITEM_WIDTH;
            convertView.getLayoutParams().width = ITEM_WIDTH;
            viewHolder.imageView = (NetworkImageView) convertView.findViewById(R.id.image_view);
            viewHolder.checkBox = (ImageView) convertView.findViewById(R.id.checkbox);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    String path = dataList.get(position);
                    if (!selectedDataList.contains(path)) {
                        if (selectedDataList.size() >= mMaxCount) {
                            ToastUtil.showToast(String.format(mContext.getString(R.string.reach_upload_max), mMaxCount));
                            return;
                        }
                        selectedDataList.add(path);
                        viewHolder.checkBox.setImageResource(R.drawable.gmacs_btn_checkbox_checked);
                    } else {
                        selectedDataList.remove(path);
                        viewHolder.checkBox.setImageResource(R.drawable.gmacs_btn_checkbox_unchecked);
                    }
                    if (dataList != null && mOnItemClickListener != null && position < dataList.size()) {
                        mOnItemClickListener.onItemClick(selectedDataList.size());
                    }
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.checkBox.setTag(position);
        String path = dataList.get(position);
        viewHolder.imageView.setDefaultImageResId(R.color.gray_ebebeb)
                .setErrorImageResId(R.color.gray_ebebeb).setImageUrl(path);
        viewHolder.checkBox.setImageResource(selectedDataList.contains(path) ? R.drawable.gmacs_btn_checkbox_checked : R.drawable.gmacs_btn_checkbox_unchecked);

        return convertView;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener {
        void onItemClick(int size);
    }
}
