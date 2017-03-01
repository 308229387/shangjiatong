package com.android.gmacs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.activity.GmacsBrandServiceListActivity;
import com.android.gmacs.adapter.ContactListAdapter;
import com.android.gmacs.event.ContactsEvent;
import com.android.gmacs.event.RemarkEvent;
import com.android.gmacs.logic.ContactLogic;
import com.android.gmacs.view.FastLetterIndexView;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.PinnedHeaderListView;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.parse.contact.Contact;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsUiUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoshuang on 2015/11/19.
 */

/**
 * 联系人
 */
public class ContactListFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private FastLetterIndexView mFastLetterIndexView;
    private TextView mTvToastIndex, mTvNoContact;
    private final long DELAY_MILLIS_GONE_TOAST_INDEX = 500;
    private final int FAST_LETTER_TOAST_INDEX = 1;
    protected PinnedHeaderListView mLvContactList;
    private ContactListAdapter mContactListAdapter;
    private List<Contact> contacts = new ArrayList<>();
    private List<Contact> stars = new ArrayList<>();
    private AdapterView.OnItemClickListener contactListListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setContentView(R.layout.gmacs_contact_list);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        mLvContactList = (PinnedHeaderListView) getView().findViewById(R.id.pinnedheaderlistview_contacts);
        mFastLetterIndexView = (FastLetterIndexView) getView().findViewById(R.id.fastLetterIndexView);
        mTvToastIndex = (TextView) getView().findViewById(R.id.tv_toast_index);
        mTvNoContact = (TextView) getView().findViewById(R.id.tv_no_contact);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mLvContactList.setPinnedHeaderView(inflater.inflate(R.layout.gmacs_item_list_separators, mLvContactList, false));
        mLvContactList.setEnabledPinnedHeaderDynamicAlphaEffect(true);
        mLvContactList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLvContactList != null) {
                    mLvContactList.onPinnedHeaderScroll(firstVisibleItem - mLvContactList.getHeaderViewsCount());
                }
            }
        });

        if (contactListListener != null) {
            mLvContactList.setOnItemClickListener(contactListListener);
        } else {
            mLvContactList.setOnItemClickListener(this);
        }

        View mHeadView = setHeaderView();

        if (mHeadView != null) {
            mLvContactList.addHeaderView(mHeadView);
        }
        mContactListAdapter = new ContactListAdapter(getActivity(), contacts, stars);
        mLvContactList.setAdapter(mContactListAdapter);

        mLvContactList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                final int realPosition = pos - mLvContactList.getHeaderViewsCount();
                if (contacts == null || realPosition >= contacts.size()) {
                    return false;
                }
                final GmacsDialog.Builder dialog = new GmacsDialog.Builder(view.getContext(), GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
                dialog.initDialog(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                ContactLogic.getInstance().delContact(contacts.get(realPosition).getUserId(), contacts.get(realPosition).getUserSource());
                                dialog.dismiss();
                        }
                    }
                }).setListTexts(new String[]{getString(R.string.delete_contact)}).create().show();
                return true;
            }
        });

        //右侧字母滑动事件
        mFastLetterIndexView.setOnTouchLetterListener(new FastLetterIndexView.OnTouchLetterListener() {

            @Override
            public void onTouchLetter(MotionEvent event, int index, String letterIndex) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mHandler.removeMessages(FAST_LETTER_TOAST_INDEX);
                        mTvToastIndex.setVisibility(View.VISIBLE);
                        break;
                    case MotionEvent.ACTION_UP:
                        mHandler.sendEmptyMessageDelayed(FAST_LETTER_TOAST_INDEX, DELAY_MILLIS_GONE_TOAST_INDEX);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mHandler.sendEmptyMessageDelayed(FAST_LETTER_TOAST_INDEX, DELAY_MILLIS_GONE_TOAST_INDEX);
                        break;
                }
                if (View.VISIBLE == mTvToastIndex.getVisibility()) {//显示中间检索大字母
                    mTvToastIndex.setText(letterIndex);
                }

                /**
                 *   检索字母条与首字母相对应
                 */
                if ("☆".equals(letterIndex)) {
                    mLvContactList.setSelection(mLvContactList.getHeaderViewsCount());
                } else {
                    for (int i = stars.size(); i < contacts.size(); i++) {
                        Contact contact = contacts.get(i);

                        String mNameSpell;
                        if (!TextUtils.isEmpty(contact.remark.remark_spell)) {
                            mNameSpell = contact.remark.remark_spell;
                        } else {
                            mNameSpell = contact.getNameSpell();
                        }

                        if (getAlpha(mNameSpell).equals(letterIndex)) {
                            mLvContactList.setSelection(i + mLvContactList.getHeaderViewsCount());
                            break;
                        }
                    }
                }

            }
        });

    }

    /**
     * 提取英文的首字母，非英文字母用#代替
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        if (TextUtils.isEmpty(str)) {
            return "#";
        }
        String c = str.substring(0, 1).toUpperCase();
        if (c.matches("[A-Z]")) {
            return c;
        } else {
            return "#";
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        ContactLogic.getInstance().getContacts();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FAST_LETTER_TOAST_INDEX:
                    mTvToastIndex.setVisibility(View.GONE);
                    break;
            }
        }

    };


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int realPosition = position - mLvContactList.getHeaderViewsCount();
        if (realPosition < 0 || realPosition >= contacts.size()) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setClass(getActivity(), Class.forName(GmacsUiUtil.getContactDetailInfoActivityClassName()));
            intent.putExtra(GmacsConstant.EXTRA_USER_ID, contacts.get(realPosition).getUserId());
            intent.putExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_NORMAL.getValue());
            intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, contacts.get(realPosition).getUserSource());
            intent.putExtra(GmacsConstant.EXTRA_DEVICEID, contacts.get(realPosition).getDeviceId());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
        }
    }

    /**
     * 判断列表是否有数据
     */
    private void isHavePerson() {
        GLog.d(TAG, "contacts.size:" + contacts.size());
        if (contacts.size() > 0) {
            mTvNoContact.setVisibility(View.GONE);
            mFastLetterIndexView.setVisibility(View.VISIBLE);
            mLvContactList.getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
            mLvContactList.requestLayout();
        } else {
            mLvContactList.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            mLvContactList.requestLayout();
            mTvNoContact.setVisibility(View.VISIBLE);
            mFastLetterIndexView.setVisibility(View.GONE);
        }
    }

    protected View setHeaderView() {//子类设置头部视图
        return null;
    }

    /**
     * 跳转至公众号列表
     */
    protected void gotoGmacsBrandServiceListActivity() {
        startActivity(new Intent(getActivity(), GmacsBrandServiceListActivity.class));
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 供宿主程序监听联系人列表点击事件
     *
     * @param listener
     */
    public void onContactListItemClick(AdapterView.OnItemClickListener listener) {
        this.contactListListener = listener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnContactListChanged(ContactsEvent event) {
        stars.clear();
        if (event.getStars() != null) {
            stars.addAll(event.getStars());
        }
        contacts.clear();
        contacts.addAll(stars);
        if (event.getContactList() != null) {
            contacts.addAll(event.getContactList());
        }
        isHavePerson();
        mContactListAdapter.notifyDataSetChanged();
    }

    /**
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemark(RemarkEvent event) {
        ContactLogic.getInstance().getContacts();
    }
}
