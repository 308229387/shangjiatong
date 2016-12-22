package com.merchantplatform.model;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Utils.TitleBar;
import com.callback.DialogCallback;
import com.db.dao.CallDetail;
import com.db.dao.CallList;
import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.CallListDao;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.merchantplatform.R;
import com.merchantplatform.activity.CallDetailActivity;
import com.merchantplatform.adapter.CallDetailAdapter;
import com.merchantplatform.bean.CallDetailListBean;
import com.merchantplatform.bean.CallDetailResponse;
import com.merchantplatform.bean.UserCallRecordBean;
import com.merchantplatform.receiver.PhoneReceiver;
import com.okhttputils.OkHttpUtils;
import com.utils.DateUtils;
import com.utils.PermissionUtils;
import com.utils.Urls;
import com.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

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
    private static final int CALL_IN_TYPE = 1;
    private static final int CALL_OUT_TYPE = 2;
    private static final int CALL_RESULT_OK = 10;
    private static final int CALL_RESULT_FAILURE = 20;
    private String phoneNum;
    private String date;
    private CallList clickCallList;
    private boolean isCallOut = false;

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
            long clickCallId = bundle.getLong("clickCallId");
            clickCallList = getClickCallList(clickCallId);
            phoneNum = bundle.getString("phoneNum");
            String local = bundle.getString("local");
            String cate = bundle.getString("cate");
            date = bundle.getString("date");
            ArrayList<CallDetailListBean> detailListBeen = bundle.getParcelableArrayList("detailList");
            tv_call_detail_phone.setText(phoneNum);
            tv_call_detail_local.setText(local);
            tv_call_detail_cate.setText("咨询类别-" + cate);
            if (date != null) {
                tv_call_detail_date.setText(date.replace("-", "/"));
            }
            detailList = detailListBeen;
        }
    }

    private CallList getClickCallList(long clickCallId) {
        WhereCondition conditionId = CallListDao.Properties.Id.eq(clickCallId);
        ArrayList<CallList> arrayCallList = CallListDaoOperate.queryByCondition(context, conditionId);
        if (arrayCallList != null && arrayCallList.size() > 0) {
            return arrayCallList.get(0);
        } else {
            return null;
        }
    }

    public void initAdapter() {
        detailAdapter = new CallDetailAdapter(context, detailList);
        rv_call_detail.setAdapter(detailAdapter);
    }

    public void registPhoneBroadcast() {
        PhoneReceiver.addToMonitor(interaction);
    }

    PhoneReceiver.BRInteraction interaction = new PhoneReceiver.BRInteraction() {
        @Override
        public void sendAction(final String action) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    monitorCallOut(action);
                }
            }, 2000);
        }
    };

    private void monitorCallOut(String action) {
        if (action.equals(PhoneReceiver.CALL_OUT)) { //呼出
            isCallOut = true;
        }
        if (isCallOut && action.equals(PhoneReceiver.CALL_OVER)) {//挂机
            isCallOut = false;
            deleteThisRecord(clickCallList);
            upLoadUserCallLog(getUserCallLog(clickCallList));
        }
    }

    private void deleteThisRecord(CallList clickCallList) {
        if (clickCallList.getType() == 1 && clickCallList.getCallResult() == 20) {
            EventBus.getDefault().post(clickCallList);
            deleteFromCallList(clickCallList);
            deleteFromCallDetail(clickCallList);
        }
    }

    private void deleteFromCallList(CallList clickCallList) {
        CallListDaoOperate.deleteData(context, clickCallList);
    }

    private void deleteFromCallDetail(CallList clickCallList) {
        ArrayList<CallDetail> details = getDetailByList(clickCallList);
        for (CallDetail detail : details) {
            detail.setIsDeleted(true);
            CallDetailDaoOperate.updateData(context, detail);
        }
    }

    private ArrayList<CallDetail> getDetailByList(CallList callList) {
        String date_Day = DateUtils.formatDateTimeToDate(callList.getCallTime());
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionDate = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionPhone = CallDetailDao.Properties.Phone.eq(callList.getPhone());
        WhereCondition conditionType = CallDetailDao.Properties.Type.eq(callList.getType());
        if (callList.getType() == 1) {
            WhereCondition conditionResult = CallDetailDao.Properties.CallResult.eq(callList.getCallResult());
            return CallDetailDaoOperate.queryByCondition(context, conditionUserId, conditionDate, conditionPhone, conditionType, conditionResult);
        } else {
            return CallDetailDaoOperate.queryByCondition(context, conditionUserId, conditionDate, conditionPhone, conditionType);
        }
    }

    private UserCallRecordBean getUserCallLog(CallList clickCallList) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.NUMBER, CallLog.Calls.DATE, CallLog.Calls.DURATION}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            String numberInCursor = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            if (numberInCursor.equals(clickCallList.getPhone())) {
                UserCallRecordBean userCallRecordBean = new UserCallRecordBean();
                long beginTime = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE)));
                long duration = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))) * 1000;
                userCallRecordBean.setBackTime(CallDetailDaoOperate.queryMaxBackTime(context));
                userCallRecordBean.setIds(getIdsFromDetail(getDetailByList(clickCallList)));
                userCallRecordBean.setRecordState(duration == 0 ? 20 : 10);
                userCallRecordBean.setBeginTime(beginTime);
                userCallRecordBean.setEndTime(beginTime + duration);
                return userCallRecordBean;
            }
            cursor.close();
        }
        return null;
    }

    private String getIdsFromDetail(ArrayList<CallDetail> callDetails) {
        if (callDetails != null && callDetails.size() > 0) {
            String ids = "";
            for (int i = 0; i < callDetails.size(); i++) {
                ids += callDetails.get(i).getId() + "|";
            }
            return ids.substring(0, ids.length() - 1);
        } else {
            return "";
        }
    }

    private void upLoadUserCallLog(UserCallRecordBean usercallRecordBean) {
        if (usercallRecordBean != null) {
            OkHttpUtils.post(Urls.PHONE_UPLOAD_DATA)
                    .params("backTime", usercallRecordBean.getBackTime() + "")
                    .params("refreshType", "1")
                    .params("ids", usercallRecordBean.getIds())
                    .params("beginTime", usercallRecordBean.getBeginTime() + "")
                    .params("endTime", usercallRecordBean.getEndTime() + "")
                    .params("recordState", usercallRecordBean.getRecordState() + "")
                    .execute(new getPhoneIncreaseDataResponse(context));
        }
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneIncreaseDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            if (callDetailResponse != null && callDetailResponse.getData() != null) {
                saveNewDataToDB(callDetailResponse);
            }
            loadRefreshDataFromDB();
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            loadRefreshDataFromDB();
        }
    }

    private void saveNewDataToDB(CallDetailResponse callDetailResponse) {
        for (CallDetailResponse.bean bean : callDetailResponse.getData()) {
            saveNewDataToCallList(bean);
            saveNewDataToCallDetail(bean);
        }
    }

    private void saveNewDataToCallList(CallDetailResponse.bean bean) {
        if (!isExistInDetail(bean)) {
            ArrayList<CallList> result = getDataFromList(bean);
            if (result != null && result.size() > 0) {
                if (bean.getType() == CALL_OUT_TYPE) {
                    updateOutGoingCall(result);
                } else if (bean.getType() == CALL_IN_TYPE) {
                    if (bean.getCallResult() == CALL_RESULT_OK) {
                        updateInCallOk(result);
                    } else if (bean.getCallResult() == CALL_RESULT_FAILURE) {
                        updateInCallFailure(result);
                    }
                }
            } else {
                insertNewDataIntoList(bean);
            }
        }
    }

    private boolean isExistInDetail(CallDetailResponse.bean bean) {
        WhereCondition conditionId = CallDetailDao.Properties.Id.eq(bean.getId());
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        ArrayList<CallDetail> dataInDetail = CallDetailDaoOperate.queryByCondition(context, conditionId, conditionUserId);
        return dataInDetail != null && dataInDetail.size() > 0;
    }

    private ArrayList<CallList> getDataFromList(CallDetailResponse.bean bean) {
        String date_Day = DateUtils.formatMillisToDate(bean.getCallTime());
        WhereCondition conditionUserId = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionPhone = CallListDao.Properties.Phone.eq(bean.getPhone());
        WhereCondition conditionCallTime = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        return CallListDaoOperate.queryByCondition(context, conditionUserId, conditionPhone, conditionCallTime);
    }

    private void updateOutGoingCall(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_OUT_TYPE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context, callList);
                break;
            }
        }
    }

    private void updateInCallOk(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_IN_TYPE && callList.getCallResult() == CALL_RESULT_OK) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context, callList);
                break;
            }
        }
    }

    private void updateInCallFailure(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_IN_TYPE && callList.getCallResult() == CALL_RESULT_FAILURE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context, callList);
                break;
            }
        }
    }

    private void insertNewDataIntoList(CallDetailResponse.bean bean) {
        CallList callList = new CallList();
        callList.setUserId(UserUtils.getUserId());
        callList.setPhone(bean.getPhone());
        callList.setPhoneCount(1);
        callList.setCallResult(bean.getCallResult());
        callList.setType(bean.getType());
        callList.setLocal(bean.getLocal());
        callList.setCate(bean.getCate());
        callList.setCallTime(DateUtils.formatMillisToDateTime(bean.getCallTime()));
        CallListDaoOperate.insertOrReplace(context, callList);
    }

    private void saveNewDataToCallDetail(CallDetailResponse.bean bean) {
        CallDetail callDetail = new CallDetail();
        callDetail.setId(bean.getId());
        callDetail.setUserId(UserUtils.getUserId());
        callDetail.setPhone(bean.getPhone());
        callDetail.setLocal(bean.getLocal());
        callDetail.setCate(bean.getCate());
        callDetail.setCallTime(DateUtils.formatMillisToDateTime(bean.getCallTime()));
        callDetail.setBackTime(bean.getBackTime());
        callDetail.setEntryTime(bean.getEntryTime());
        callDetail.setCallResult(bean.getCallResult());
        callDetail.setType(bean.getType());
        CallDetailDaoOperate.insertOrReplace(context, callDetail);
    }

    private void loadRefreshDataFromDB() {
        if (date.equals(DateUtils.getCurrentDate())) {
            if (clickCallList.getType() == 1 && clickCallList.getCallResult() == 20) {
                detailList.clear();
                getNewDetailList();
            } else {
                getNewDetailList();
            }
        } else {
            detailList.clear();
            getNewDetailList();
            if (detailList != null && detailList.size() > 0) {
                String time = detailList.get(0).getTime();
                tv_call_detail_date.setText(DateUtils.formatTimeToDisplayTime(time));
            }
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
        return CallDetailDaoOperate.queryLimitDataByCondition(context, 1, conditionUserId, conditionPhone, conditionCallTime);
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

    public void releasePhoneMonitor() {
        PhoneReceiver.releaseMonitor(interaction);
    }

    private class OnBackPressed implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            context.onBackPressed();
        }
    }

    private class OnCallClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
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
}