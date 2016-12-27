package com.merchantplatform.model;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utils.TitleBar;
import com.db.dao.CallDetail;
import com.db.dao.gen.CallDetailDao;
import com.db.helper.CallDetailDaoOperate;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.CallDetailActivity;
import com.merchantplatform.adapter.CallDetailAdapter;
import com.merchantplatform.bean.CallDetailListBean;
import com.merchantplatform.bean.CallListNotificationDetail;
import com.utils.DateUtils;
import com.utils.PermissionUtils;
import com.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/12/17.
 */

public class CallDetailModel extends BaseModel {

    private CallDetailActivity context;
    private TitleBar tb_call_detail_title;
    private TextView tv_call_detail_phone, tv_call_detail_local, tv_call_detail_cate, tv_call_detail_date;
    private LinearLayout ll_detail_call;
    private RecyclerView rv_call_detail;
    private CallDetailAdapter detailAdapter;
    private ArrayList<CallDetailListBean> detailList;
    private String phoneNum;
    private String date;

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
        ll_detail_call = (LinearLayout) context.findViewById(R.id.ll_detail_call);
    }

    public void setTitleBar() {
        tb_call_detail_title.setImmersive(true);
        tb_call_detail_title.setBackgroundColor(Color.WHITE);
        tb_call_detail_title.setLeftImageResource(R.mipmap.title_back);
        tb_call_detail_title.setTitle("通话详情");
        tb_call_detail_title.setTitleColor(Color.BLACK);
    }

    public void setRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        rv_call_detail.setLayoutManager(layoutManager);
    }

    public void setListener() {
        tb_call_detail_title.setLeftClickListener(new OnBackPressed());
        ll_detail_call.setOnClickListener(new OnCallClickListener());
    }

    public void initData(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("detailList");
            phoneNum = bundle.getString("phoneNum");
            String local = bundle.getString("local");
            String cate = bundle.getString("cate");
            date = bundle.getString("date");
            ArrayList<CallDetailListBean> detailListBeen = bundle.getParcelableArrayList("detailList");
            tv_call_detail_phone.setText(phoneNum);
            tv_call_detail_local.setText(local);
            tv_call_detail_cate.setText("咨询类别-" + cate);
            if (date != null) {
                tv_call_detail_date.setText(DateUtils.formatDateTimeToDate(date).replace("-", "/"));
            }
            detailList = detailListBeen;
        }
    }

    public void initAdapter() {
        detailAdapter = new CallDetailAdapter(context, detailList);
        rv_call_detail.setAdapter(detailAdapter);
    }

    public void registerEventBus() {
        EventBus.getDefault().register(context);
    }

    public void onEvent(CallListNotificationDetail callListNotificationDetail) {
        if (callListNotificationDetail.getJumpMsg().equals("refreshDetail")) {
            loadRefreshDataFromDB();
        }
    }

    private void loadRefreshDataFromDB() {
        detailList.clear();
        getNewDetailList();
        if (!date.equals(DateUtils.getCurrentDate()) && detailList != null && detailList.size() > 0) {
            String time = DateUtils.formatDateTimeToDate(date);
            tv_call_detail_date.setText(time.replace("-", "/"));
        }
    }

    private void getNewDetailList() {
        ArrayList<CallDetailListBean> newDetailList = getDetailList(getNewDetailDataFromDB());
        detailList.addAll(newDetailList);
        detailAdapter.notifyDataSetChanged();
    }

    private ArrayList<CallDetail> getNewDetailDataFromDB() {
        String date_Day = DateUtils.getCurrentDate();
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionPhone = CallDetailDao.Properties.Phone.eq(phoneNum);
        WhereCondition conditionCallTime = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionType = CallDetailDao.Properties.Type.eq(2);
        return CallDetailDaoOperate.queryByCondition(context, conditionUserId, conditionPhone, conditionCallTime, conditionType);
    }

    private ArrayList<CallDetailListBean> getDetailList(ArrayList<CallDetail> newDetailDataFromDB) {
        ArrayList<CallDetailListBean> detailList = new ArrayList<>();
        for (CallDetail lists : newDetailDataFromDB) {
            CallDetailListBean detailListBean = new CallDetailListBean();
            detailListBean.setTime(lists.getCallTime());
            detailListBean.setType(lists.getType());
            detailListBean.setDuration(lists.getEntryTime());
            detailList.add(detailListBean);
        }
        return detailList;
    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            LogUmengAgent.ins().log(LogUmengEnum.LOG_DIANHUAXQY_RETURN);
            context.onBackPressed();
        }
    }

    private class OnCallClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            LogUmengAgent.ins().log(LogUmengEnum.LOG_DIANHUAXQY_BDDH);
            makeACall();
        }
    }

    private void makeACall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.requestPermission(context, PermissionUtils.CODE_READ_CALL_LOG, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    if (requestCode == PermissionUtils.CODE_READ_CALL_LOG) {
                        invokeCall();
                    }
                }
            });
        } else {
            invokeCall();
        }
    }

    private void invokeCall() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    public void unRegisterEventBus() {
        EventBus.getDefault().unregister(context);
    }
}