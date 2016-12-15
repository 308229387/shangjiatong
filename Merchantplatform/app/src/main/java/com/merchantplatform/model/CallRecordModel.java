package com.merchantplatform.model;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.callback.DialogCallback;
import com.db.dao.CallDetail;
import com.db.dao.CallList;
import com.db.dao.gen.CallDetailDao;
import com.db.dao.gen.CallListDao;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.merchantplatform.R;
import com.merchantplatform.bean.CallDetailResponse;
import com.okhttputils.OkHttpUtils;
import com.utils.DateUtils;
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
    private XRecyclerView xrv_callrecord;
    private View emptyView;
    private CallRecordAdapter mAdapter;
    private ArrayList<CallList> listData;
    private int tabIndex;
    private static final int CALL_IN_TYPE = 1;
    private static final int CALL_OUT_TYPE = 2;
    private static final int CALL_RESULT_OK = 10;
    private static final int CALL_RESULT_FAILURE = 20;

    public CallRecordModel(CallRecordFragment context) {
        this.context = context;
    }

    public void initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_call_record, container, false);
        xrv_callrecord = (XRecyclerView) view.findViewById(R.id.xrv_callrecord);
        emptyView = view.findViewById(R.id.layout_call_empty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_callrecord.setLayoutManager(layoutManager);
        xrv_callrecord.setRefreshProgressStyle(ProgressStyle.BallPulse);
        xrv_callrecord.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        xrv_callrecord.setEmptyView(emptyView);
    }

    public void setListener() {
        xrv_callrecord.setLoadingListener(new RecyclerViewLoadingListener());
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
        xrv_callrecord.setAdapter(mAdapter);
        xrv_callrecord.refresh();//刚进来的时候可以直接刷新
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
            listData.addAll(getRefreshData());
            mAdapter.notifyDataSetChanged();
            xrv_callrecord.refreshComplete();
        }

        @Override
        public void onLoadMore() {
            listData.addAll(loadMoreData());
            mAdapter.notifyDataSetChanged();
            xrv_callrecord.loadMoreComplete();
        }
    }

    private class OnClickEmptyViewListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            xrv_callrecord.refresh();
        }
    }

    private ArrayList<CallList> getRefreshData() {
        getNewData();
        if (tabIndex == 0) {
            WhereCondition condition = CallListDao.Properties.CallResult.eq("20");
            return CallListDaoOperate.queryLimitDataByCondition(context.getContext(), 20, condition);
        } else {
            return CallListDaoOperate.queryLimitData(context.getContext(), 20);
        }
    }

    private void getNewData() {
        long maxBackTime = CallDetailDaoOperate.queryMaxBackTime(context.getContext());
        if (maxBackTime == 0) {
            getNetResponseData("0", "1");
        } else {
            getNetResponseData(maxBackTime + "", "1");
        }
    }

    private ArrayList<CallList> loadMoreData() {
        ArrayList<CallList> moreList;
        if (tabIndex == 0) {
            WhereCondition condition = CallListDao.Properties.CallResult.eq("20");
            moreList = CallListDaoOperate.queryOffsetLimitDataByCondition(context.getContext(), listData.size(), 20, condition);
        } else {
            moreList = CallListDaoOperate.queryOffsetLimitData(context.getContext(), listData.size(), 20);
        }
        if (moreList != null && moreList.size() > 0) {
            return moreList;
        } else {
            getLoadMoreData();
            return loadMoreData();
        }
    }

    private void getLoadMoreData() {
        long minBackTime = CallDetailDaoOperate.queryMinBackTime(context.getContext());
        getNetResponseData(minBackTime + "", "0");
    }

    private void getNetResponseData(String backTime, String refreshType) {
        OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", refreshType)
                .execute(new getPhoneIncreaseDataResponse(context.getActivity()));
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneIncreaseDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            for (CallDetailResponse.bean bean : callDetailResponse.getData()) {
                saveNewDataToCallList(bean);
                saveNewDataToCallDetail(bean);
            }
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
        callList.setPhoneCount(0);
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
            Toast.makeText(context.getContext(), "position" + position, Toast.LENGTH_SHORT).show();
        }
    }
}