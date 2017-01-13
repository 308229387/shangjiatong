package com.merchantplatform.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.Utils.UserUtils;
import com.db.dao.CallDetail;
import com.db.dao.CallList;
import com.db.dao.gen.CallDetailDao;
import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.CallDetailActivity;
import com.merchantplatform.bean.CallDetailListBean;
import com.merchantplatform.model.CallRecordModel;
import com.utils.DateUtils;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

/**
 * Created by 58 on 2016/11/29.
 */

public class CallRecordAdapter extends BaseRecyclerViewAdapter<CallList, CallRecordAdapter.CallRecordViewHolder> {

    public CallRecordAdapter(Context context, ArrayList<CallList> mList) {
        super(context, mList);
    }

    @Override
    protected void bindDataToItemView(final CallRecordViewHolder callRecordViewHolder, final int position) {
        CallList callList = getItem(position);
        callRecordViewHolder
                .setText(R.id.tv_phoneNum, callList.getPhone())
                .setTextColor(R.id.tv_phoneNum, (callList.getType() == 1 && callList.getCallResult() == 20) ? context.getResources().getColor(R.color.item_call_delete) : context.getResources().getColor(R.color.item_call_phone))
                .setText(R.id.tv_phone_city, callList.getLocal())
                .setText(R.id.tv_call_cate, "咨询类别-" + callList.getCate())
                .setText(R.id.tv_call_time, DateUtils.displayByDateTime(callList.getCallTime()))
                .setTag(R.id.iv_call_detail, position)
                .setOnClickListener(R.id.iv_call_detail, new OnDetailClickListener())
                .setTag(R.id.tv_delete, R.id.delete_tag_vh, callRecordViewHolder)
                .setTag(R.id.tv_delete, R.id.delete_tag_position, position)
                .setOnClickListener(R.id.tv_delete, new OnDeleteItemClickListener());
        if (callList.getPhoneCount() > 1) {
            callRecordViewHolder
                    .setText(R.id.tv_call_count, "(" + getItem(position).getPhoneCount() + ")")
                    .setTextColor(R.id.tv_call_count, (callList.getType() == 1 && callList.getCallResult() == 20) ? context.getResources().getColor(R.color.item_call_delete) : context.getResources().getColor(R.color.item_call_phone))
                    .setVisible(R.id.tv_call_count, true);
        } else {
            callRecordViewHolder.setVisible(R.id.tv_call_count, false);
        }
        if (callList.getType() == 1 && callList.getCallResult() == 20) {
            callRecordViewHolder.setVisible(R.id.iv_phoneState, false);
        } else {
            callRecordViewHolder
                    .setVisible(R.id.iv_phoneState, true)
                    .setImageResource(R.id.iv_phoneState, callList.getType() == 1 ? R.mipmap.item_call_in : R.mipmap.item_call_out);
        }
    }

    @Override
    public CallRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CallRecordViewHolder(inflateItemView(parent, R.layout.item_callrecord));
    }

    public class CallRecordViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {
        public CallRecordViewHolder(View itemView) {
            super(itemView);
        }
    }

    private class OnDetailClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            CallRecordModel.clickCallList = mList.get(position);
            LogUmengAgent.ins().log(LogUmengEnum.LOG_TONGHULBY_DJJRXQY);
            goToCallDetail(position);
        }
    }

    private void goToCallDetail(int position) {
        String phoneNum = mList.get(position).getPhone();
        String local = mList.get(position).getLocal();
        String cate = mList.get(position).getCate();
        String date = mList.get(position).getCallTime();
        ArrayList<CallDetail> detailData = getDetailByList(mList.get(position));
        ArrayList<CallDetailListBean> detailList = getDetailList(detailData);
        Bundle bundle = new Bundle();
        bundle.putString("phoneNum", phoneNum);
        bundle.putString("local", local);
        bundle.putString("cate", cate);
        bundle.putString("date", date);
        bundle.putParcelableArrayList("detailList", detailList);
        Intent intent = new Intent(context, CallDetailActivity.class);
        intent.putExtra("detailList", bundle);
        context.startActivity(intent);
    }

    private ArrayList<CallDetail> getDetailByList(CallList callList) {
        String date_Day = DateUtils.formatDateTimeToDate(callList.getCallTime());
        WhereCondition conditionUserId = CallDetailDao.Properties.UserId.eq(UserUtils.getUserId(context));
        WhereCondition conditionDate = new WhereCondition.StringCondition("date(CALL_TIME)='" + date_Day + "'");
        WhereCondition conditionPhone = CallDetailDao.Properties.Phone.eq(callList.getPhone());
        WhereCondition conditionType = CallDetailDao.Properties.Type.eq(callList.getType());
        WhereCondition conditionIsDeleted = CallDetailDao.Properties.IsDeleted.eq(false);
        if (callList.getType() == 1) {
            WhereCondition conditionResult = CallDetailDao.Properties.CallResult.eq(callList.getCallResult());
            return CallDetailDaoOperate.queryByCondition(context, conditionUserId, conditionDate, conditionPhone, conditionType, conditionIsDeleted, conditionResult);
        } else {
            return CallDetailDaoOperate.queryByCondition(context, conditionUserId, conditionDate, conditionPhone, conditionType, conditionIsDeleted);
        }
    }

    private ArrayList<CallDetailListBean> getDetailList(ArrayList<CallDetail> detailData) {
        if (detailData != null && detailData.size() > 0) {
            ArrayList<CallDetailListBean> detailList = new ArrayList<>();
            for (CallDetail callDetail : detailData) {
                CallDetailListBean listBean = new CallDetailListBean();
                listBean.setTime(callDetail.getCallTime());
                listBean.setType(callDetail.getType());
                listBean.setCallResult(callDetail.getCallResult());
                listBean.setDuration(callDetail.getEntryTime());
                detailList.add(listBean);
            }
            return detailList;
        }
        return null;
    }

    private class OnDeleteItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            LogUmengAgent.ins().log(LogUmengEnum.LOG_TONGHUALBY_CHASHCH);
            int position = (Integer) v.getTag(R.id.delete_tag_position);
            setDeletedFlagInDB(position);
            deleteFromCallList(position);
            closeDelMenu((CallRecordViewHolder) v.getTag(R.id.delete_tag_vh), position);
            EventBus.getDefault().post(mList.get(position));
        }
    }

    private void setDeletedFlagInDB(int position) {
        ArrayList<CallDetail> arrayList = getDetailByList(mList.get(position));
        for (CallDetail callDetail : arrayList) {
            callDetail.setIsDeleted(true);
            CallDetailDaoOperate.updateData(context, callDetail);
        }
    }

    private void deleteFromCallList(int position) {
        CallListDaoOperate.deleteData(context, mList.get(position));
    }
}