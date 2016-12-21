package com.merchantplatform.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.push.bean.PushSqlBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 58 on 2016/12/15.
 */

public class SystemMessageAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater mInfalter;
    private List<PushSqlBean> beans = new ArrayList<>();

    public SystemMessageAdapter(Context context) {
        this.context = context;
        mInfalter = LayoutInflater.from(context);
    }

    public void setDataSources(List<PushSqlBean> beans){
        this.beans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder= new ViewHolder();
            convertView = mInfalter.inflate(R.layout.layout_message_item,parent,false);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.tv_qd_result = (TextView) convertView.findViewById(R.id.tv_qd_result);
            holder.tv_noouejd = (TextView) convertView.findViewById(R.id.tv_noouejd);
            holder.tv_bg_title = (TextView) convertView.findViewById(R.id.tv_bg_title);
            holder.tv_order_number = (TextView) convertView.findViewById(R.id.tv_order_number);
            holder.view   = convertView.findViewById(R.id.view);
            holder.rl_more = (RelativeLayout) convertView.findViewById(R.id.rl_more);
            holder.tv_more = (TextView) convertView.findViewById(R.id.tv_more);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        PushSqlBean  messageBean = beans.get(position);
        String msgCate = messageBean.getType();
        switch(msgCate){
            case "100":
                holder.tv_qd_result.setText("退单成功通知");
                holder.tv_noouejd.setVisibility(View.GONE);
                holder.tv_order_number.setVisibility(View.GONE);
                holder.tv_bg_title.setText("退单成功！Bigbang140申请的订单23456677已退单，退单在3个工作日内处理，金额会返还至您的账户。");
                holder.view.setVisibility(View.GONE);
                holder.rl_more.setVisibility(View.GONE);
                break;
            case "101":
//                holder.tv_time.setText(TimeUtils.formatDateTime(messageBean.getTime()));
                holder.tv_qd_result.setText("国庆不打烊，商机不间断");
                holder.tv_noouejd.setVisibility(View.GONE);
                holder.tv_order_number.setVisibility(View.GONE);
                holder.tv_bg_title.setVisibility(View.GONE);
                holder.view.setVisibility(View.VISIBLE);
                holder.tv_more.setText("查看详情");
                holder.rl_more.setVisibility(View.VISIBLE);
                break;
        }

        return convertView;
    }

    class ViewHolder{
        public TextView tv_time;
        public TextView tv_bg_title;
        public TextView tv_qd_result;
        public TextView tv_order_number;
        public TextView tv_noouejd;
        public RelativeLayout rl_more;
        public TextView tv_more;
        public View view;
    }
}
