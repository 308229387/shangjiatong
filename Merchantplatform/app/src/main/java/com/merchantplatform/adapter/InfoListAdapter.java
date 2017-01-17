package com.merchantplatform.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.merchantplatform.R;
import com.merchantplatform.activity.InfoDetailActivity;
import com.merchantplatform.activity.PrecisionPromoteActivity;
import com.merchantplatform.activity.UpPromoteActivity;
import com.merchantplatform.bean.InfoListBean;
import com.utils.Constant;
import com.utils.InfoStateEnum;
import com.utils.PageSwitchUtils;
import com.utils.StringUtil;
import com.xrecyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by linyueyang on 17/1/6.
 */

public class InfoListAdapter extends BaseRecyclerViewAdapter<InfoListBean, InfoListAdapter.InfoViewHolder> {


    private InfoViewHolder clickViewHolder;

    public InfoViewHolder getClickViewHolder() {
        return clickViewHolder;
    }

    public InfoListAdapter(final Context context, ArrayList<InfoListBean> postListBeen) {
        super(context, postListBeen);
    }


    @Override
    protected void bindDataToItemView(final InfoViewHolder InfoViewHolder, int position) {

        final InfoListBean infoListBean = getItem(position);
        //添加图片
        ImageView imageView = InfoViewHolder.getView(R.id.iv_info_list);
        Glide.with(context).load(infoListBean.getPic()).placeholder(R.mipmap.info_list_no_pic).error(R.mipmap.info_list_no_pic).into(imageView);
        //添加标题
        InfoViewHolder.setText(R.id.tv_title_info_list, infoListBean.getTitle());
        //设置状态
        InfoStateEnum infoStateEnum = InfoStateEnum.getEnumById(infoListBean.getAuditState());
        //审核未通过状态设置，未通过原因
        if (infoStateEnum == InfoStateEnum.REFUSED && StringUtil.isNotEmpty(infoListBean.getAuditFailMsg())) {
            String failMsg = infoListBean.getAuditFailMsg();
            if (failMsg.length() > 7) { //列表页审核不通过原因只显示7个字,多余...
                failMsg = failMsg.substring(0, 7) + "...";
            }
            failMsg = "(" + failMsg + ")";
            String state = infoStateEnum.getContent() + failMsg;
            Spannable string = new SpannableString(state);
            string.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.common_text_orange)), infoStateEnum.getContent().length(), state.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            ((TextView) InfoViewHolder.getView(R.id.tv_state_info_list)).setText(string);
        } else {
            InfoViewHolder.setText(R.id.tv_state_info_list, infoStateEnum.getContent());
        }

        //根据状态绑定不同的View和数据
        if (infoStateEnum == InfoStateEnum.SHOWING || infoStateEnum == InfoStateEnum.VIPSHOWING) {
            //设置状态字体颜色
            InfoViewHolder.setTextColor(R.id.tv_state_info_list, context.getResources().getColor(R.color.state_showing_green));
            //设置状态精准置顶是否显示
            if (infoListBean.getJzShow() == 1)//1为显示精准 0为不显示
                InfoViewHolder.setVisible(R.id.tv_info_list_isaccurate, true);
            else
                InfoViewHolder.setVisible(R.id.tv_info_list_isaccurate, false);
            if (infoListBean.getTopShow() == 1)//1为显示置顶 0为不显示
                InfoViewHolder.setVisible(R.id.tv_info_list_istop, true);
            else
                InfoViewHolder.setVisible(R.id.tv_info_list_istop, false);
            //设置PV
            InfoViewHolder.setVisible(R.id.tv_readcount_info_list, true);
            InfoViewHolder.setText(R.id.tv_readcount_info_list, infoListBean.getPv());
            //设置底部精准置顶入口
            InfoViewHolder.setVisible(R.id.ll_info_list_bottom, true);
            InfoViewHolder.setOnClickListener(R.id.ll_info_list_accurate, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickViewHolder = InfoViewHolder;
                    Map map = new HashMap<>();
                    map.put(Constant.INFOID, infoListBean.getInfoId());
                    PageSwitchUtils.goToActivityWithString(context, PrecisionPromoteActivity.class, map);
                }
            });
            InfoViewHolder.setOnClickListener(R.id.ll_info_list_top, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map map = new HashMap<>();
                    map.put(Constant.INFOID, infoListBean.getInfoId());
                    PageSwitchUtils.goToActivityWithString(context, UpPromoteActivity.class, map);
                }
            });
        } else {
            InfoViewHolder.setTextColor(R.id.tv_state_info_list, context.getResources().getColor(R.color.common_text_gray));
            InfoViewHolder.setVisible(R.id.tv_info_list_isaccurate, false);
            InfoViewHolder.setVisible(R.id.tv_info_list_istop, false);
            InfoViewHolder.setVisible(R.id.tv_readcount_info_list, false);
            InfoViewHolder.setVisible(R.id.ll_info_list_bottom, false);
        }


        //设置发布日期
        InfoViewHolder.setText(R.id.tv_date_info_list, infoListBean.getAddDate());

        InfoViewHolder.setOnClickListener(R.id.rl_infolist_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickViewHolder = InfoViewHolder;
                Map map = new HashMap<>();
                map.put(Constant.INFOID, infoListBean.getInfoId());
                PageSwitchUtils.goToActivityWithString(context, InfoDetailActivity.class, map);
            }
        });


    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(inflateItemView(parent, R.layout.item_info_list));
    }

    public class InfoViewHolder extends BaseRecyclerViewAdapter.SparseArrayViewHolder {
        public InfoViewHolder(View itemView) {
            super(itemView);
        }
    }
}
