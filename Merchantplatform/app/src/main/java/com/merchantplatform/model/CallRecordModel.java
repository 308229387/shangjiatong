package com.merchantplatform.model;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.merchantplatform.R;
import com.merchantplatform.application.HyApplication;
import com.merchantplatform.bean.CallDetailResponse;
import com.utils.DateUtils;
import com.utils.UserUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;
import com.merchantplatform.adapter.CallRecordAdapter;
import com.merchantplatform.fragment.CallRecordFragment;
import com.xrecyclerview.ProgressStyle;
import com.xrecyclerview.XRecyclerView;

import org.greenrobot.greendao.query.WhereCondition;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
    private final int CALL_IN_TYPE = 1;
    private final int CALL_OUT_TYPE = 2;
    private final int CALL_RESULT_OK = 10;
    private final int CALL_RESULT_FALSE = 20;

    public CallRecordModel(CallRecordFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        initView(inflater, container);
        setListener();
    }

    private void initView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_call_record, container, false);
        xrv_callrecord = (XRecyclerView) view.findViewById(R.id.xrv_callrecord);
        emptyView = view.findViewById(R.id.layout_call_empty);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_callrecord.setLayoutManager(layoutManager);
        xrv_callrecord.setRefreshProgressStyle(ProgressStyle.BallPulse);
        xrv_callrecord.setLoadingMoreProgressStyle(ProgressStyle.SquareSpin);
        xrv_callrecord.setEmptyView(emptyView);
        Drawable dividerDrawable = ContextCompat.getDrawable(context.getContext(), R.drawable.divider_sample);
        xrv_callrecord.addItemDecoration(xrv_callrecord.new DividerItemDecoration(dividerDrawable));//Item之间添加分割线
    }

    private void setListener() {
        xrv_callrecord.setLoadingListener(new RecyclerViewLoadingListener());
        emptyView.setOnClickListener(new OnClickEmptyViewListener());
    }

    public void initAdapter() {
        initData();
        setAdapterListener();
    }

    private void initData() {
        listData = new ArrayList<>();
        mAdapter = new CallRecordAdapter(context.getContext(), listData);
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
            getRefreshData();
            listData.addAll(CallListDaoOperate.queryLimitData(context.getContext(), 20));
            mAdapter.notifyDataSetChanged();
            xrv_callrecord.refreshComplete();
        }

        @Override
        public void onLoadMore() {
//            getLoadMoreData();
        }
    }

    private class OnClickEmptyViewListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            xrv_callrecord.refresh();
        }
    }

    private void getRefreshData() {
        long maxBackTime = CallDetailDaoOperate.queryMaxBackTime(context.getContext());
        getNetResponseData(maxBackTime + "", "1");
    }

    private void getLoadMoreData() {
//        ArrayList<CallList> callLists = CallListDaoOperate.queryLimitData(context.getContext(), 20);
//        if (callLists != null && callLists.size() > 0) {
//            listData.addAll(callLists);
//        } else {
////            getNetResponseData();
//        }
//
//        if (listData.size() > 50) {
//            xrv_callrecord.setNoMore(true);
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                    getNetResponseData();
//                    mAdapter.notifyDataSetChanged();
//                    xrv_callrecord.loadMoreComplete();
//                }
//            }, 1000);
//        }
    }

    private void getNetResponseData(String backTime, String refreshType) {
        /*OkHttpUtils.get(Urls.PHONE_INCREASE_DATA)
                .params("backTime", backTime)
                .params("refreshType", refreshType)
                .execute(new getPhoneIncreaseDataResponse(context.getActivity()));*/
        onResponse();
    }

    private class getPhoneIncreaseDataResponse extends DialogCallback<CallDetailResponse> {

        public getPhoneIncreaseDataResponse(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, CallDetailResponse callDetailResponse, Request request, @Nullable Response response) {
            CallRecordModel.this.onResponse();
        }
    }

    private void onResponse() {
        String jsonString = getJsonFromAssets();
        List<CallDetailResponse.bean> newData = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jsonResult = jsonObject.optJSONObject("result");
            newData = jsonToObjectList(jsonResult.optString("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (CallDetailResponse.bean bean : newData) {
            saveNewDataToCallList(bean);
            saveNewDataToCallDetail(bean);
        }
    }

    private String getJsonFromAssets() {
        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = HyApplication.getApplication().getAssets();
        BufferedReader bf;
        try {
            bf = new BufferedReader(new InputStreamReader(assetManager.open("json.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public List<CallDetailResponse.bean> jsonToObjectList(String json) {
        try {
            return new Gson().fromJson(json, new TypeToken<List<CallDetailResponse.bean>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private void saveNewDataToCallList(CallDetailResponse.bean bean) {
        WhereCondition conditionId = CallDetailDao.Properties.Id.eq(bean.getId());
        ArrayList<CallDetail> dataInDetail = CallDetailDaoOperate.queryByCondition(context.getContext(), conditionId);
        if (dataInDetail.size() == 0) {
            String date = DateUtils.formatMillisToDateTime(bean.getCallTime());
            String date_Day = DateUtils.formatMillisToDate(bean.getCallTime());
            WhereCondition condition1 = CallListDao.Properties.UserId.eq(UserUtils.getUserId());
            WhereCondition condition2 = CallListDao.Properties.Phone.eq(bean.getPhone());
            WhereCondition condition3 = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
            ArrayList<CallList> result = CallListDaoOperate.queryByCondition(context.getContext(), condition1, condition2, condition3);
            if (result != null && result.size() > 0) {
                if (bean.getType() == CALL_OUT_TYPE) {
                    for (CallList callList : result) {
                        if (callList.getType() == CALL_OUT_TYPE) {
                            callList.setPhoneCount(callList.getPhoneCount() + 1);
                            CallListDaoOperate.updateData(context.getContext(), callList);
                            break;
                        }
                    }
                } else if (bean.getType() == CALL_IN_TYPE) {
                    if (bean.getCallResult() == CALL_RESULT_OK) {
                        for (CallList callList : result) {
                            if (callList.getCallResult() == CALL_RESULT_OK) {
                                callList.setPhoneCount(callList.getPhoneCount() + 1);
                                CallListDaoOperate.updateData(context.getContext(), callList);
                                break;
                            }
                        }
                    } else if (bean.getCallResult() == CALL_RESULT_FALSE) {
                        for (CallList callList : result) {
                            if (callList.getCallResult() == CALL_RESULT_FALSE) {
                                callList.setPhoneCount(callList.getPhoneCount() + 1);
                                CallListDaoOperate.updateData(context.getContext(), callList);
                                break;
                            }
                        }
                    }
                }
            } else {
                CallList callList = new CallList();
                callList.setUserId(UserUtils.getUserId());
                callList.setPhone(bean.getPhone());
                callList.setPhoneCount(0);
                callList.setCallResult(bean.getCallResult());
                callList.setType(bean.getType());
                callList.setLocal(bean.getLocal());
                callList.setCate(bean.getCate());
                callList.setCallTime(date);
                CallListDaoOperate.insertOrReplace(context.getContext(), callList);
            }
        }
    }

    private class OnCallItemClickListener implements BaseRecyclerViewAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(context.getContext(), "position" + position, Toast.LENGTH_SHORT).show();
        }
    }
}