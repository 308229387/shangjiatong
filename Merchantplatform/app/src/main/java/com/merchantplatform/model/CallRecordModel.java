package com.merchantplatform.model;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog.Calls;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.callback.DialogCallback;
import com.db.dao.CallDetail;
import com.db.dao.CallList;
import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.CallListDao;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.merchantplatform.R;
import com.merchantplatform.bean.CallDetailResponse;
import com.merchantplatform.bean.UserCallRecordBean;
import com.merchantplatform.receiver.PhoneReceiver;
import com.okhttputils.OkHttpUtils;
import com.orhanobut.logger.Logger;
import com.utils.DateUtils;
import com.utils.PermissionUtils;
import com.utils.Urls;
import com.utils.UserUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;
import com.merchantplatform.adapter.CallRecordAdapter;
import com.merchantplatform.fragment.CallRecordFragment;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordModel extends BaseModel {

    private CallRecordFragment context;
    private View view;
    private XRecyclerView mXRecyclerView;
    private View emptyView;
    private CallRecordAdapter mAdapter;
    private ArrayList<CallList> listData;
    private int tabIndex;
    private static final int CALL_IN_TYPE = 1;
    private static final int CALL_OUT_TYPE = 2;
    private static final int CALL_RESULT_OK = 10;
    private static final int CALL_RESULT_FAILURE = 20;
    private int clickPosition;

    public CallRecordModel(CallRecordFragment context) {
        this.context = context;
    }

    public void initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_call_record, container, false);
        mXRecyclerView = (XRecyclerView) view.findViewById(R.id.xrv_callrecord);
        emptyView = view.findViewById(R.id.layout_call_empty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mXRecyclerView.setLayoutManager(layoutManager);
        mXRecyclerView.setRefreshProgressStyle(ProgressStyle.BallPulse);
        mXRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        mXRecyclerView.setEmptyView(emptyView);
    }

    public void setListener() {
        mXRecyclerView.setLoadingListener(new RecyclerViewLoadingListener());
        emptyView.setOnClickListener(new OnClickEmptyViewListener());
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public void initAdapter() {
        initData();
        setAdapterListener();
    }

    private void initData() {
        listData = new ArrayList<>();
        mAdapter = new CallRecordAdapter(context.getContext(), listData, tabIndex);
        mXRecyclerView.setAdapter(mAdapter);
        mXRecyclerView.refresh();
    }

    private void setAdapterListener() {
        mAdapter.setOnItemClickListener(new OnCallItemClickListener());
    }

    public View getView() {
        return view;
    }

    private class RecyclerViewLoadingListener implements XRecyclerView.LoadingListener {

        @Override
        public void onRefresh() {
            listData.clear();
            getNewData();
        }

        @Override
        public void onLoadMore() {
            loadMoreData();
        }
    }

    private class OnClickEmptyViewListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            mXRecyclerView.refresh();
        }
    }

    private void getNewData() {
        long maxBackTime = CallDetailDaoOperate.queryMaxBackTime(context.getContext());
        getRefreshResponseData(maxBackTime + "");
    }

    private void loadMoreData() {
        if (!canGetMoreListDataFromDB()) {
            getLoadMoreData();
        }
    }

    private boolean canGetMoreListDataFromDB() {
        ArrayList<CallList> moreList = getMoreListDataFromDB();
        if (moreList != null && moreList.size() > 0) {
            listData.addAll(moreList);
            mAdapter.notifyDataSetChanged();
            mXRecyclerView.loadMoreComplete();
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<CallList> getMoreListDataFromDB() {
        ArrayList<CallList> moreList;
        if (tabIndex == 0) {
            WhereCondition condition = CallListDao.Properties.CallResult.eq("20");
            moreList = CallListDaoOperate.queryOffsetLimitDataByCondition(context.getContext(), listData.size(), 20, condition);
        } else {
            moreList = CallListDaoOperate.queryOffsetLimitData(context.getContext(), listData.size(), 20);
        }
        return moreList;
    }

    private void getLoadMoreData() {
        long minBackTime = CallDetailDaoOperate.queryMinBackTime(context.getContext());
        getLoadMoreResponseData(minBackTime + "");
    }

    private void getLoadMoreResponseData(String backTime) {
        OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", "0")
                .execute(new getPhoneLoadMoreDataResponse(context.getActivity()));
    }

    private class getPhoneLoadMoreDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneLoadMoreDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            if (callDetailResponse.getData() == null || callDetailResponse.getData().size() == 0) {
                mXRecyclerView.setNoMore(true);
            } else {
                saveNewDataToDB(callDetailResponse);
                listData.addAll(getMoreListDataFromDB());
                mAdapter.notifyDataSetChanged();
                mXRecyclerView.loadMoreComplete();
            }
        }
    }

    private void getRefreshResponseData(String backTime) {
        OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", "1")
                .execute(new getPhoneIncreaseDataResponse(context.getActivity()));
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneIncreaseDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            saveNewDataToDB(callDetailResponse);
            listData.addAll(getNewListDataFromDB());
            mAdapter.notifyDataSetChanged();
            mXRecyclerView.refreshComplete();
        }
    }

    private ArrayList<CallList> getNewListDataFromDB() {
        if (tabIndex == 0) {
            WhereCondition condition = CallListDao.Properties.CallResult.eq("20");
            return CallListDaoOperate.queryLimitDataByCondition(context.getContext(), 20, condition);
        } else {
            return CallListDaoOperate.queryLimitData(context.getContext(), 20);
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
                insertNewData(bean);
            }
        }
    }

    private boolean isExistInDetail(CallDetailResponse.bean bean) {
        WhereCondition conditionId = CallDetailDao.Properties.Id.eq(bean.getId());
        ArrayList<CallDetail> dataInDetail = CallDetailDaoOperate.queryByCondition(context.getContext(), conditionId);
        return dataInDetail.size() > 0;
    }

    private ArrayList<CallList> getDataFromList(CallDetailResponse.bean bean) {
        String date_Day = DateUtils.formatMillisToDate(bean.getCallTime());
        WhereCondition condition1 = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition condition2 = CallListDao.Properties.Phone.eq(bean.getPhone());
        WhereCondition condition3 = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        return CallListDaoOperate.queryByCondition(context.getContext(), condition1, condition2, condition3);
    }

    private void updateOutGoingCall(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_OUT_TYPE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context.getContext(), callList);
                break;
            }
        }
    }

    private void updateInCallOk(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getCallResult() == CALL_RESULT_OK) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context.getContext(), callList);
                break;
            }
        }
    }

    private void updateInCallFailure(ArrayList<CallList> result) {
        for (CallList callList : result) {
            if (callList.getCallResult() == CALL_RESULT_FAILURE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                CallListDaoOperate.updateData(context.getContext(), callList);
                break;
            }
        }
    }

    private void insertNewData(CallDetailResponse.bean bean) {
        CallList callList = new CallList();
        callList.setUserId(UserUtils.getUserId());
        callList.setPhone(bean.getPhone());
        callList.setPhoneCount(1);
        callList.setCallResult(bean.getCallResult());
        callList.setType(bean.getType());
        callList.setLocal(bean.getLocal());
        callList.setCate(bean.getCate());
        callList.setCallTime(DateUtils.formatMillisToDateTime(bean.getCallTime()));
        CallListDaoOperate.insertOrReplace(context.getContext(), callList);
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
        CallDetailDaoOperate.insertOrReplace(context.getContext(), callDetail);
    }

    private class OnCallItemClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            makeACall(position);
        }
    }

    private void makeACall(final int position) {
        clickPosition = position;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionUtils.requestPermission(context.getActivity(), PermissionUtils.CODE_READ_CALL_LOG, new PermissionUtils.PermissionGrant() {
                @Override
                public void onPermissionGranted(int requestCode) {
                    if (requestCode == PermissionUtils.CODE_READ_CALL_LOG) {
                        invokeCall(position);
                    }
                }
            });
        } else {
            invokeCall(position);
        }
    }

    private void invokeCall(int position) {
        PhoneReceiver.addToMonitor(interaction);
        upLoadUserCallLog(getUserCallLog(clickPosition));
        String phoneNum = listData.get(position).getPhone();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    PhoneReceiver.BRInteraction interaction = new PhoneReceiver.BRInteraction() {
        @Override
        public void sendAction(String action) { //监听电话状态
            Logger.e("监听到了事件");
            if (action.equals(PhoneReceiver.CALL_OVER)) {//挂机
                Logger.e("监听到了挂机");
            } else if (action.equals(PhoneReceiver.CALL_UP)) {//接听
                Logger.e("监听到了接听");
            } else if (action.equals(PhoneReceiver.CALL_OUT)) { //呼出
                Logger.e("监听到了呼出");
            }
        }
    };

    private void upLoadUserCallLog(UserCallRecordBean usercallRecordBean) {
        if (usercallRecordBean != null) {
            OkHttpUtils.post(Urls.PHONE_UPLOAD_DATA)
                    .params("backTime", usercallRecordBean.getBackTime() + "")
                    .params("refreshType", "1")
                    .params("ids", usercallRecordBean.getIds())
                    .params("beginTime", usercallRecordBean.getBeginTime() + "")
                    .params("endTime", usercallRecordBean.getEndTime() + "")
                    .params("recordState", usercallRecordBean.getRecordState() + "")
                    .execute(new getPhoneIncreaseDataResponse(context.getActivity()));
        }
    }

    private UserCallRecordBean getUserCallLog(int position) {
        if (ContextCompat.checkSelfPermission(context.getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Cursor cursor = context.getContext().getContentResolver().query(Calls.CONTENT_URI, new String[]{Calls.NUMBER, Calls.DATE, Calls.DURATION}, null, null, Calls.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            int i = 0;
            do {
                String numberInCursor = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
                if (numberInCursor.equals(listData.get(position).getPhone())) {
                    UserCallRecordBean userCallRecordBean = new UserCallRecordBean();
                    long beginTime = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE)));
                    long duration = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION))) * 1000;
                    userCallRecordBean.setBackTime(CallDetailDaoOperate.queryMaxBackTime(context.getContext()));
                    userCallRecordBean.setIds(getIdsFromDetail(getDetailByList(listData.get(position))));
                    userCallRecordBean.setRecordState(duration == 0 ? 20 : 10);
                    userCallRecordBean.setBeginTime(beginTime);
                    userCallRecordBean.setEndTime(beginTime + duration);
                    return userCallRecordBean;
                }
            } while (i++ < 2 && cursor.moveToNext());
            cursor.close();
        }
        return null;
    }

    private ArrayList<CallDetail> getDetailByList(CallList callList) {
        String date_Day = DateUtils.formatDateTimeToDate(callList.getCallTime());
        WhereCondition conditionId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionDate = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionIsDeleted = CallDetailDao.Properties.IsDeleted.eq(false);
        WhereCondition conditionPhone = CallDetailDao.Properties.Phone.eq(callList.getPhone());
        return CallDetailDaoOperate.queryByCondition(context.getContext(), conditionId, conditionDate, conditionIsDeleted, conditionPhone);
    }

    private String getIdsFromDetail(ArrayList<CallDetail> callDetails) {
        String ids = "";
        for (int i = 0; i < callDetails.size(); i++) {
            ids += callDetails.get(i).getId() + "|";
        }
        return ids.substring(0, ids.length() - 1);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_READ_CALL_LOG) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokeCall(clickPosition);
            }
        }
    }

    public void destroyFragment() {
        PhoneReceiver.releaseMonitor(interaction);
    }
}