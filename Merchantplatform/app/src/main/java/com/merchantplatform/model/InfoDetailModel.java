package com.merchantplatform.model;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.callback.DialogCallback;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.PrecisionPromoteActivity;
import com.merchantplatform.activity.UpPromoteActivity;
import com.merchantplatform.bean.InfoDetailBean;
import com.merchantplatform.bean.InfoDetailResponse;
import com.merchantplatform.bean.InfoListResponse;
import com.okhttputils.OkHttpUtils;
import com.rollerview.RollPagerView;
import com.rollerview.Util;
import com.rollerview.adapter.LoopPagerAdapter;
import com.rollerview.hintview.TextHintView;
import com.ui.diyview.MoreTextView;
import com.utils.Constant;
import com.utils.InfoStateEnum;
import com.utils.PageSwitchUtils;
import com.utils.StringUtil;
import com.utils.Urls;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by linyueyang on 17/1/6.
 */

public class InfoDetailModel extends BaseModel {

    private Activity activity;
    private RollPagerView rollPagerView;

    private TextView tv_title_info_detail;
    private TextView tv_state_info_detail;
    private TextView tv_info_detail_isaccurate;
    private TextView tv_info_detail_istop;

    private TextView tv_date_info_detail;
    private TextView tv_readcount_info_detail;
    private TextView tv_info_detail_name;
    private TextView tv_info_detail_phone;
    private TextView tv_info_detail_cate_content;
    private TextView tv_info_detail_address_content;

    private MoreTextView mtv_info_detail;
    private View sv_info_detail;
    private View ll_bottom_bar;
    private View ll_info_detail_accurate;
    private View ll_info_detail_top;
    private View iv_info_detail_back;
    private String infoId;

    private InfoDetailAdapter adapter;
    private List<String> pics;


    public InfoDetailModel(Activity activity) {
        this.activity = activity;
    }

    public void initView() {
        rollPagerView = (RollPagerView) activity.findViewById(R.id.rpv_info_detail);
        adapter = new InfoDetailAdapter(rollPagerView);
        iv_info_detail_back = activity.findViewById(R.id.iv_info_detail_back);

        tv_title_info_detail = (TextView) activity.findViewById(R.id.tv_title_info_detail);
        tv_state_info_detail = (TextView) activity.findViewById(R.id.tv_state_info_detail);
        tv_info_detail_isaccurate = (TextView) activity.findViewById(R.id.tv_info_detail_isaccurate);
        tv_info_detail_istop = (TextView) activity.findViewById(R.id.tv_info_detail_istop);
        tv_date_info_detail = (TextView) activity.findViewById(R.id.tv_date_info_detail);
        tv_readcount_info_detail = (TextView) activity.findViewById(R.id.tv_readcount_info_detail);

        tv_info_detail_name = (TextView) activity.findViewById(R.id.tv_info_detail_name);
        tv_info_detail_phone = (TextView) activity.findViewById(R.id.tv_info_detail_phone);

        tv_info_detail_cate_content = (TextView) activity.findViewById(R.id.tv_info_detail_cate_content);
        tv_info_detail_address_content = (TextView) activity.findViewById(R.id.tv_info_detail_address_content);

        mtv_info_detail = (MoreTextView) activity.findViewById(R.id.mtv_info_detail);

        sv_info_detail = activity.findViewById(R.id.sv_info_detail);
        ll_bottom_bar = activity.findViewById(R.id.ll_bottom_bar);
        ll_info_detail_accurate = activity.findViewById(R.id.ll_info_detail_accurate);
        ll_info_detail_top = activity.findViewById(R.id.ll_info_detail_top);

    }

    public void initData() {

        infoId = activity.getIntent().getStringExtra(Constant.INFOID);

        OkHttpUtils.get(Urls.POST_DETAIL).params(Constant.INFOID, infoId).execute(
                new DialogCallback<InfoDetailResponse>(activity) {

                    @Override
                    public void onResponse(boolean isFromCache, InfoDetailResponse infoDetailResponse, Request request, @Nullable Response response) {

                        InfoDetailBean infoDetailBean = infoDetailResponse.getData();
                        bindDataToView(infoDetailBean);
                    }
                });
    }

    private void bindDataToView(final InfoDetailBean infoDetailBean) {

        //图片的处理
        pics = infoDetailBean.getPic();
        if (null != pics && pics.size() > 0) {
            rollPagerView.setHintView(new TextHintView(activity));
            rollPagerView.setHintPadding(Util.dip2px(activity, 5), Util.dip2px(activity, 2), Util.dip2px(activity, 5), Util.dip2px(activity, 2));
            rollPagerView.setAdapter(adapter);
            if(pics.size()==1){
                rollPagerView.setNoScroll(true);
            }
        } else {
            rollPagerView.setHintView(null);
        }

        //回退按钮
        iv_info_detail_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_TZXQ_FH);
                activity.finish();
            }
        });

        tv_title_info_detail.setText(infoDetailBean.getTitle());
        InfoStateEnum infoStateEnum = InfoStateEnum.getEnumById(infoDetailBean.getState());
        if (infoStateEnum == InfoStateEnum.REFUSED && StringUtil.isNotEmpty(infoDetailBean.getAuditFailMsg())) {
            String failMsg = "(" + infoDetailBean.getAuditFailMsg() + ")";
            String state = infoStateEnum.getContent() + failMsg;
            Spannable string = new SpannableString(state);
            string.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.common_text_orange)), infoStateEnum.getContent().length(), state.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

            tv_state_info_detail.setText(string);
        } else {
            tv_state_info_detail.setText(infoStateEnum.getContent());
        }

        //根据状态绑定不同的View和数据
        if (infoStateEnum == InfoStateEnum.SHOWING) {
            //设置状态字体颜色
            tv_state_info_detail.setTextColor(activity.getResources().getColor(R.color.state_showing_green));
            //设置状态精准置顶是否显示
            if (infoDetailBean.getJzShow() == 1)
                tv_info_detail_isaccurate.setVisibility(View.VISIBLE);
            else
                tv_info_detail_isaccurate.setVisibility(View.GONE);
            if (infoDetailBean.getTopShow() == 1)
                tv_info_detail_istop.setVisibility(View.VISIBLE);
            else
                tv_info_detail_istop.setVisibility(View.GONE);
            //设置PV
            tv_readcount_info_detail.setVisibility(View.VISIBLE);
            tv_readcount_info_detail.setText(infoDetailBean.getPv());
            //设置底部精准置顶入口
            ll_bottom_bar.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, 0, Util.dip2px(activity, 49));
            sv_info_detail.setLayoutParams(params);
            ll_info_detail_accurate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUmengAgent.ins().log(LogUmengEnum.LOG_TZXQ_JZ);
                    Map map = new HashMap<>();
                    map.put(Constant.INFOID, infoDetailBean.getInfoId());
                    PageSwitchUtils.goToActivityWithString(activity, PrecisionPromoteActivity.class, map);
                }
            });
            ll_info_detail_top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUmengAgent.ins().log(LogUmengEnum.LOG_TZXQ_ZD);
                    Map map = new HashMap<>();
                    map.put(Constant.INFOID, infoDetailBean.getInfoId());
                    PageSwitchUtils.goToActivityWithString(activity, UpPromoteActivity.class, map);
                }
            });
        } else {
            tv_state_info_detail.setTextColor(activity.getResources().getColor(R.color.common_text_gray));
            tv_info_detail_isaccurate.setVisibility(View.GONE);
            tv_info_detail_istop.setVisibility(View.GONE);
            tv_readcount_info_detail.setVisibility(View.INVISIBLE);
            ll_bottom_bar.setVisibility(View.GONE);
        }

        //设置发布日期
        tv_date_info_detail.setText(infoDetailBean.getAddDate());
        tv_info_detail_phone.setText(infoDetailBean.getPhone());
        tv_info_detail_name.setText(infoDetailBean.getName());
        tv_info_detail_cate_content.setText(infoDetailBean.getCatename());
        tv_info_detail_address_content.setText(infoDetailBean.getAddress());
        mtv_info_detail.setContent(infoDetailBean.getServiceintroduce());

    }

    private class InfoDetailAdapter extends LoopPagerAdapter {

        public InfoDetailAdapter(RollPagerView viewPager) {
            super(viewPager);
        }

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            Glide.with(activity).load(pics.get(position)).placeholder(R.mipmap.info_detail_no_pic).error(R.mipmap.info_detail_no_pic).into(view);

            return view;
        }

        @Override
        public int getRealCount() {
            if (null != pics)
                return pics.size();
            return 0;
        }
    }

    public void registerEventBus(){
        EventBus.getDefault().register(activity);
    }

    public void unRegisterEventBus(){
        EventBus.getDefault().unregister(activity);
    }

}
