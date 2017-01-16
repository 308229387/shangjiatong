package com.merchantplatform.model;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
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
import com.loadview.ShapeLoadingDialog;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.bean.CallDetailResponse;
import com.merchantplatform.bean.CallListNotificationDetail;
import com.merchantplatform.bean.UserCallRecordBean;
import com.merchantplatform.receiver.PhoneReceiver;
import com.okhttputils.OkHttpUtils;
import com.okhttputils.request.BaseRequest;
import com.tablayout.SlidingTabLayout;
import com.utils.DateUtils;
import com.utils.PermissionUtils;
import com.utils.Urls;
import com.utils.UserUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;
import com.merchantplatform.adapter.CallRecordAdapter;
import com.merchantplatform.fragment.CallRecordFragment;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

import okhttp3.Call;
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
    private SlidingTabLayout mTabLayout;
    private ShapeLoadingDialog shapeLoadingDialog;
    private CallRecordAdapter mAdapter;
    private ArrayList<CallList> listData;
    private static final int CALL_IN_TYPE = 1;
    private static final int CALL_OUT_TYPE = 2;
    private static final int CALL_RESULT_OK = 10;
    private static final int CALL_RESULT_FAILURE = 20;
    private int tabIndex;
    private boolean isCallOut = false;
    public static boolean isAppCallOut = false;
    public static CallList clickCallList;
    private int callOutTimes = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    dismissDialog();
                    loadRefreshDataFromDB();
                    EventBus.getDefault().post(new CallListNotificationDetail("refreshDetail"));
                    break;
                case 1:
                    dismissDialog();
                    loadMoreDataFromDB();
                    break;
            }
        }
    };

    public CallRecordModel(CallRecordFragment context) {
        this.context = context;
    }

    public void initView(LayoutInflater inflater, ViewGroup container) {
        mTabLayout = (SlidingTabLayout) context.getActivity().findViewById(R.id.tb_switch_callType);
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

    public void registPhoneBroadcast() {
        PhoneReceiver.addToMonitor(interaction);
    }

    public void registEventBus() {
        EventBus.getDefault().register(context);
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
        mAdapter = new CallRecordAdapter(context.getContext(), listData);
        mXRecyclerView.setAdapter(mAdapter);
    }

    public void firstRefreshData() {
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
        ArrayList<CallList> moreList = getMoreListDataFromDB();
        if (moreList != null && moreList.size() > 0) {
            listData.addAll(moreList);
            mAdapter.notifyDataSetChanged();
            mXRecyclerView.loadMoreComplete();
        } else {
            getLoadMoreData();
        }
    }

    private ArrayList<CallList> getMoreListDataFromDB() {
        ArrayList<CallList> moreList;
        WhereCondition conditionUserId = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
        if (tabIndex == 0) {
            WhereCondition conditionType = CallListDao.Properties.Type.eq(1);
            WhereCondition conditionCallResult = CallListDao.Properties.CallResult.eq(20);
            moreList = CallListDaoOperate.queryOffsetLimitDataByCondition(context.getContext(), listData.size(), 20, conditionUserId, conditionType, conditionCallResult);
        } else {
            moreList = CallListDaoOperate.queryOffsetLimitDataByCondition(context.getContext(), listData.size(), 20, conditionUserId);
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
                .execute(new getPhoneLoadMoreDataResponse(context.getActivity(), false));
    }

    private class getPhoneLoadMoreDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneLoadMoreDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onBefore(BaseRequest request) {
            super.onBefore(request);
            initDialogAndShow(context.getActivity());
        }

        @Override
        public void onResponse(boolean isFromCache, final CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            if (callDetailResponse.getData() == null || callDetailResponse.getData().size() == 0) {
                dismissDialog();
                mXRecyclerView.setNoMore(true);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveNewDataToDB(callDetailResponse);
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            loadMoreDataFromDB();
        }
    }

    private void loadMoreDataFromDB() {
        listData.addAll(getMoreListDataFromDB());
        mAdapter.notifyDataSetChanged();
        mXRecyclerView.loadMoreComplete();
    }

    private void getRefreshResponseData(String backTime) {
        OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", "1")
                .execute(new getPhoneIncreaseDataResponse(context.getActivity(), false));
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneIncreaseDataResponse(Activity activity, boolean isShowDialog) {
            super(activity, isShowDialog);
        }

        @Override
        public void onBefore(BaseRequest request) {
            super.onBefore(request);
            initDialogAndShow(context.getActivity());
        }

        @Override
        public void onResponse(boolean isFromCache, final CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (callDetailResponse != null && callDetailResponse.getData() != null) {
                        saveNewDataToDB(callDetailResponse);
                        mHandler.sendEmptyMessage(0);
                    }
                }
            }).start();
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            loadRefreshDataFromDB();
            EventBus.getDefault().post(new CallListNotificationDetail("refreshDetail"));
        }
    }

    private void initDialogAndShow(Activity activity) {
        shapeLoadingDialog = new ShapeLoadingDialog(activity);
        shapeLoadingDialog.show();
    }

    private void dismissDialog() {
        if (shapeLoadingDialog != null && shapeLoadingDialog.isShowing()) {
            shapeLoadingDialog.dismiss();
            shapeLoadingDialog = null;
        }
    }

    private void loadRefreshDataFromDB() {
        listData.clear();
        listData.addAll(getNewListDataFromDB());
        mAdapter.notifyDataSetChanged();
        mXRecyclerView.refreshComplete();
    }

    private ArrayList<CallList> getNewListDataFromDB() {
        WhereCondition conditionUserId = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
        if (tabIndex == 0) {
            WhereCondition conditionType = CallListDao.Properties.Type.eq(1);
            WhereCondition conditionCallResult = CallListDao.Properties.CallResult.eq(20);
            return CallListDaoOperate.queryLimitDataByCondition(context.getContext(), 20, conditionUserId, conditionType, conditionCallResult);
        } else {
            return CallListDaoOperate.queryLimitDataByCondition(context.getContext(), 20, conditionUserId);
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
                String newCallTime = DateUtils.formatMillisToDateTime(bean.getCallTime());
                if (bean.getType() == CALL_OUT_TYPE) {
                    updateOutGoingCall(result, newCallTime);
                } else if (bean.getType() == CALL_IN_TYPE) {
                    if (bean.getCallResult() == CALL_RESULT_OK) {
                        updateInCallOk(result, newCallTime);
                    } else if (bean.getCallResult() == CALL_RESULT_FAILURE) {
                        updateInCallFailure(result, newCallTime);
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
        ArrayList<CallDetail> dataInDetail = CallDetailDaoOperate.queryByCondition(context.getContext(), conditionId, conditionUserId);
        return dataInDetail != null && dataInDetail.size() > 0;
    }

    private ArrayList<CallList> getDataFromList(CallDetailResponse.bean bean) {
        String date_Day = DateUtils.formatMillisToDate(bean.getCallTime());
        WhereCondition conditionUserId = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionPhone = CallListDao.Properties.Phone.eq(bean.getPhone());
        WhereCondition conditionCallTime = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionType = CallListDao.Properties.Type.eq(bean.getType());
        if (bean.getType() == 1) {
            WhereCondition conditionResult = CallListDao.Properties.CallResult.eq(bean.getCallResult());
            return CallListDaoOperate.queryByCondition(context.getContext(), conditionUserId, conditionPhone, conditionCallTime, conditionType, conditionResult);
        } else {
            return CallListDaoOperate.queryByCondition(context.getContext(), conditionUserId, conditionPhone, conditionCallTime, conditionType);
        }
    }

    private void updateOutGoingCall(ArrayList<CallList> result, String newCallTime) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_OUT_TYPE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                callList.setCallTime(newCallTime);
                CallListDaoOperate.updateData(context.getContext(), callList);
                break;
            }
        }
    }

    private void updateInCallOk(ArrayList<CallList> result, String newCallTime) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_IN_TYPE && callList.getCallResult() == CALL_RESULT_OK) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                callList.setCallTime(newCallTime);
                CallListDaoOperate.updateData(context.getContext(), callList);
                break;
            }
        }
    }

    private void updateInCallFailure(ArrayList<CallList> result, String newCallTime) {
        for (CallList callList : result) {
            if (callList.getType() == CALL_IN_TYPE && callList.getCallResult() == CALL_RESULT_FAILURE) {
                callList.setPhoneCount(callList.getPhoneCount() + 1);
                callList.setCallTime(newCallTime);
                CallListDaoOperate.updateData(context.getContext(), callList);
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
        CallListDaoOperate.insert(context.getContext(), callList);
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
        callDetail.setIsDeleted(false);
        CallDetailDaoOperate.insertOrReplace(context.getContext(), callDetail);
    }

    private class OnCallItemClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            LogUmengAgent.ins().log(LogUmengEnum.LOG_TONGHUALBY_DJBDDH);
            makeACall(position);
        }
    }

    private void makeACall(final int position) {
        clickCallList = listData.get(position);
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
        CallRecordModel.isAppCallOut = true;
        String phoneNum = listData.get(position).getPhone();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    PhoneReceiver.BRInteraction interaction = new PhoneReceiver.BRInteraction() {
        @Override
        public void sendAction(final String action) {
            if (mTabLayout.getCurrentTab() == tabIndex) {
                if (action.equals(PhoneReceiver.NEW_OUTGOING_CALL)) { //呼出
                    isCallOut = true;
                }
                if (CallRecordModel.isAppCallOut && isCallOut && action.equals(PhoneReceiver.CALL_OVER)) {//挂机
                    isCallOut = false;
                    CallRecordModel.isAppCallOut = false;
                    if (clickCallList != null && callOutTimes == 0) {
                        callOutTimes++;
                        monitorCallOut();
                    }
                }
            }
        }
    };

    private void monitorCallOut() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserCallRecordBean userCallRecord = getUserCallLog(clickCallList);
                if (userCallRecord != null) {
                    deleteThisRecord(clickCallList);
                    upLoadUserCallLog(userCallRecord);
                }
            }
        }, 2000);
    }

    private void deleteThisRecord(CallList clickCallList) {
        if (clickCallList.getType() == 1 && clickCallList.getCallResult() == 20) {
            EventBus.getDefault().post(clickCallList);
            deleteFromCallDetail(clickCallList);
            deleteFromCallList(clickCallList);
        }
    }

    public void deleteFromAdapterList(CallList clickCallList) {
        if (clickCallList != null) {
            listData.remove(clickCallList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteFromCallDetail(CallList clickCallList) {
        ArrayList<CallDetail> details = getDetailByList(clickCallList);
        for (CallDetail detail : details) {
            detail.setIsDeleted(true);
            CallDetailDaoOperate.updateData(context.getContext(), detail);
        }
    }

    private void deleteFromCallList(CallList clickCallList) {
        CallListDaoOperate.deleteData(context.getContext(), clickCallList);
    }

    private void upLoadUserCallLog(UserCallRecordBean usercallRecordBean) {
        OkHttpUtils.post(Urls.PHONE_UPLOAD_DATA)
                .params("backTime", usercallRecordBean.getBackTime() + "")
                .params("refreshType", "1")
                .params("ids", usercallRecordBean.getIds())
                .params("beginTime", usercallRecordBean.getBeginTime() + "")
                .params("endTime", usercallRecordBean.getEndTime() + "")
                .params("recordState", usercallRecordBean.getRecordState() + "")
                .execute(new getPhoneIncreaseDataResponse(context.getActivity(), false));
        callOutTimes = 0;
    }

    private UserCallRecordBean getUserCallLog(CallList clickCallList) {
        if (ContextCompat.checkSelfPermission(context.getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Cursor cursor = context.getContext().getContentResolver().query(Calls.CONTENT_URI, new String[]{Calls.NUMBER, Calls.DATE, Calls.DURATION}, null, null, Calls.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            String numberInCursor = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
            if (numberInCursor.equals(clickCallList.getPhone())) {
                UserCallRecordBean userCallRecordBean = new UserCallRecordBean();
                long beginTime = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE)));
                long duration = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DURATION))) * 1000;
                userCallRecordBean.setBackTime(CallDetailDaoOperate.queryMaxBackTime(context.getContext()));
                userCallRecordBean.setIds(getIdsFromDetail(getDetailByList(clickCallList)));
                userCallRecordBean.setRecordState(duration == 0 ? 20 : 10);
                userCallRecordBean.setBeginTime(beginTime);
                userCallRecordBean.setEndTime(beginTime + duration);
                cursor.close();
                return userCallRecordBean;
            }
        }
        return null;
    }

    private ArrayList<CallDetail> getDetailByList(CallList callList) {
        String date_Day = DateUtils.formatDateTimeToDate(callList.getCallTime());
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId());
        WhereCondition conditionDate = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionPhone = CallDetailDao.Properties.Phone.eq(callList.getPhone());
        WhereCondition conditionType = CallDetailDao.Properties.Type.eq(callList.getType());
        if (callList.getType() == 1) {
            WhereCondition conditionResult = CallDetailDao.Properties.CallResult.eq(callList.getCallResult());
            return CallDetailDaoOperate.queryByCondition(context.getContext(), conditionUserId, conditionDate, conditionPhone, conditionType, conditionResult);
        } else {
            return CallDetailDaoOperate.queryByCondition(context.getContext(), conditionUserId, conditionDate, conditionPhone, conditionType);
        }
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

    public void releasePhoneMonitor() {
        PhoneReceiver.releaseMonitor(interaction);
    }

    public void unregistEventBus() {
        EventBus.getDefault().unregister(context);
    }
}