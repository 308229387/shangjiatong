package com.merchantplatform.model;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.Utils.TitleBar;
import com.merchantplatform.R;
import com.merchantplatform.activity.CallDetailActivity;
import com.merchantplatform.adapter.CallDetailAdapter;
import com.merchantplatform.bean.CallDetailListBean;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/12/17.
 */

public class CallDetailModel extends BaseModel {

    private CallDetailActivity context;
    private TitleBar tb_call_detail_title;
    private TextView tv_call_detail_phone, tv_call_detail_local, tv_call_detail_cate, tv_call_detail_date;
    private RecyclerView rv_call_detail;
    private CallDetailAdapter detailAdapter;
    private ArrayList<CallDetailListBean> detailList;

    public CallDetailModel(CallDetailActivity context) {
        this.context = context;
    }

    public void initView() {
        tb_call_detail_title = (TitleBar) context.findViewById(R.id.tb_call_detail_title);
        tv_call_detail_phone = (TextView) context.findViewById(R.id.tv_call_detail_phone);
        tv_call_detail_local = (TextView) context.findViewById(R.id.tv_call_detail_local);
        tv_call_detail_cate = (TextView) context.findViewById(R.id.tv_call_detail_cate);
        tv_call_detail_date = (TextView) context.findViewById(R.id.tv_call_detail_date);
        rv_call_detail = (RecyclerView) context.findViewById(R.id.rv_call_detail);
    }

    public void setTitleBar() {
        tb_call_detail_title.setImmersive(true);
        tb_call_detail_title.setBackgroundColor(Color.WHITE);
        tb_call_detail_title.setLeftImageResource(R.mipmap.title_back);
        tb_call_detail_title.setTitle("通话详情");
        tb_call_detail_title.setTitleColor(Color.BLACK);
    }

    public void setListener() {
        tb_call_detail_title.setLeftClickListener(new OnBackPressed());
    }

    public void initAdapter() {
        detailList = new ArrayList<>();
        detailAdapter = new CallDetailAdapter(context, detailList);
        rv_call_detail.setAdapter(detailAdapter);
    }

    public void initData(Intent intent) {
        if (intent != null) {
            String phoneNum = intent.getStringExtra("phoneNum");
            String local = intent.getStringExtra("local");
            String cate = intent.getStringExtra("cate");
            String date = intent.getStringExtra("date");
            Bundle bundle = intent.getBundleExtra("detailList");
            ArrayList<CallDetailListBean> detailListBeen = bundle.getParcelableArrayList("detailList");
            tv_call_detail_phone.setText(phoneNum);
            tv_call_detail_local.setText(local);
            tv_call_detail_cate.setText(cate);
            tv_call_detail_date.setText(date);
            detailList.addAll(detailListBeen);
            detailAdapter.notifyDataSetChanged();
        }
    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    }
}