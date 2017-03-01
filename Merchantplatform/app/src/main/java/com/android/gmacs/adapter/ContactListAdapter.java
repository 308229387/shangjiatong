package com.android.gmacs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.view.NetworkImageView;
import com.android.gmacs.view.PinnedHeaderListView;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

import static com.android.gmacs.view.NetworkImageView.IMG_RESIZE;


/**
 * Created by zhangxiaoshuang on 2015/11/19.
 */
public class ContactListAdapter extends BaseAdapter implements PinnedHeaderListView.PinnedHeaderListAdapter {

    private List<Contact> contacts = new ArrayList<>();
    private List<Contact> stars = new ArrayList<>();
    private Context mContext;

    public ContactListAdapter(Context context, List<Contact> contacts, List<Contact> stars) {
        mContext = context;
        this.contacts = contacts;
        this.stars = stars;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
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
            viewHolder.iv_contact_avatar = (NetworkImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.tv_contact_name = (TextView) convertView.findViewById(R.id.tv_contact_name);
            viewHolder.tvSeparator = (TextView) convertView.findViewById(R.id.tv_separator);
            viewHolder.contact_item_line = convertView.findViewById(R.id.contact_item_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contact contact = contacts.get(position);
        viewHolder.tv_contact_name.setText(contact.getNameToShow());

        viewHolder.iv_contact_avatar
                .setDefaultImageResId(R.drawable.gmacs_ic_default_avatar)
                .setErrorImageResId(R.drawable.gmacs_ic_default_avatar)
                .setImageUrl(ImageUtil.makeUpUrl(contact.getAvatar(), IMG_RESIZE, IMG_RESIZE));

        viewHolder.tvSeparator.setVisibility(View.VISIBLE);
        //星标客户
        if (contact.isStar() && (stars != null && position < stars.size())) {
            viewHolder.tvSeparator.setText(mContext.getText(R.string.starred_contact));
            if (position == 0) {
                viewHolder.tvSeparator.setVisibility(View.VISIBLE);
            } else {
                viewHolder.tvSeparator.setVisibility(View.GONE);
            }
            if (stars.size() > 1 && position != 0) {
                viewHolder.contact_item_line.setVisibility(View.VISIBLE);
            } else {
                viewHolder.contact_item_line.setVisibility(View.GONE);
            }
        } else { //全部联系人
            String currentStr = contact.getFirstLetter();// 当前联系人
            String previousStr = (position - 1) >= 0 ? contacts.get(position - 1).getFirstLetter() : "";// 上一个联系人
            if (position == 0 || (stars != null && position == stars.size()) || !currentStr.equals(previousStr)) {
                viewHolder.tvSeparator.setText(currentStr);
                viewHolder.tvSeparator.setVisibility(View.VISIBLE);//不同字母再次分组
            } else {
                viewHolder.tvSeparator.setVisibility(View.GONE);//同一个字母
            }
            if (!currentStr.equals(previousStr)) {
                viewHolder.contact_item_line.setVisibility(View.GONE);
            } else {
                viewHolder.contact_item_line.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }


    /**
     * @return 滑动时判断头部字母listview
     */
    @Override
    public int getPinnedHeaderState(int position) {
        if (contacts.size() == 0) {
            return PINNED_HEADER_GONE;
        }
        Contact currentContact = contacts.get(position);
        String currentStr = currentContact.getFirstLetter();// 当前联系人
        String nextStr = (position + 1) < contacts.size() ? contacts.get(position + 1).getFirstLetter() : "";// 下一个联系人
        if (currentContact.isStar() && stars != null && position < stars.size() - 1) {
            return PINNED_HEADER_VISIBLE;
        } else if (!currentStr.equals(nextStr) || (stars != null && position == stars.size() - 1)) {
            return PINNED_HEADER_PUSHED_UP;//挤压效果
        } else {
            return PINNED_HEADER_VISIBLE;
        }
    }

    /**
     * @param header   pinned header view.
     * @param position position of the first visible list item.
     * @param alpha    配置头部listview标题显示内容
     */
    @Override
    public void configurePinnedHeader(View header, int position, int alpha) {
        String title;
        Contact curContact = contacts.get(position);
        if (curContact.isStar() && stars != null && position < stars.size()) {
            title = mContext.getString(R.string.starred_contact);
        } else {
            title = curContact.getFirstLetter();
        }
        ((TextView) header.findViewById(R.id.tv_separator)).setText(title);
    }


    private class ViewHolder {
        NetworkImageView iv_contact_avatar;
        TextView tv_contact_name;
        TextView tvSeparator;
        View contact_item_line;
    }
}
