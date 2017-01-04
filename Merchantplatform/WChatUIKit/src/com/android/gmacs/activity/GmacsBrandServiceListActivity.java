package com.android.gmacs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.gmacs.R;
import com.android.gmacs.adapter.GmacsBrandServiceAdapter;
import com.android.gmacs.event.PublicAccountListEvent;
import com.android.gmacs.logic.ContactLogic;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.parse.contact.GmacsUser;
import com.common.gmacs.parse.pubcontact.PublicContactInfo;
import com.common.gmacs.utils.GmacsUiUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoshuang on 2015/11/27.
 */
public class GmacsBrandServiceListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView mLvServiceBrand;
    private GmacsBrandServiceAdapter mAdapter;
    private List<PublicContactInfo> user = new ArrayList<>();

    @Override
    protected void initView() {
        setTitle(getString(R.string.public_account));
        mLvServiceBrand = (ListView) findViewById(R.id.lv_service_brand);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initData() {
        ContactLogic.getInstance().getPublicAccount(GmacsUser.getInstance().getSource());
        mAdapter = new GmacsBrandServiceAdapter(this, user);
        mLvServiceBrand.setAdapter(mAdapter);
        mLvServiceBrand.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_brand_service_list);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent intent = new Intent(GmacsBrandServiceListActivity.this, Class.forName(GmacsUiUtil.getContactDetailInfoActivityClassName()));
            intent.putExtra(GmacsConstant.EXTRA_USER_ID, user.get(position).getUser_id());
            intent.putExtra(GmacsConstant.EXTRA_USER_SOURCE, user.get(position).getUser_source());
            intent.putExtra(GmacsConstant.EXTRA_TALK_TYPE, Gmacs.TalkType.TALKETYPE_OFFICIAL.getValue());
            startActivity(intent);
        } catch (ClassNotFoundException e){
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPubAccountsEvent(PublicAccountListEvent event) {
        user.addAll(event.getPublicContactInfos());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

