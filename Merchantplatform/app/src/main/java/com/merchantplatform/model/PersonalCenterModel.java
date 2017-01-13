package com.merchantplatform.model;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Utils.CircleImageView;
import com.Utils.UserUtils;
import com.callback.DialogCallback;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.bean.UserInfoResponse;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.okhttputils.OkHttpUtils;
import com.ta.utdid2.android.utils.StringUtils;
import com.utils.PageSwitchUtils;
import com.utils.StringUtil;
import com.utils.Urls;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class PersonalCenterModel extends BaseModel implements View.OnClickListener{
    private PersonalCenterFragment context;
    private View view;
    private TextView tv_personal_userPhone;
    private CircleImageView iv_personal_user;
    private RelativeLayout rl_personal_funding, rl_personal_setting;


    public PersonalCenterModel(PersonalCenterFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_personal_center, container, false);
    }

    public void initView(){
        tv_personal_userPhone = (TextView) view.findViewById(R.id.tv_personal_userPhone);
        iv_personal_user = (CircleImageView) view.findViewById(R.id.iv_personal_user);
        rl_personal_funding = (RelativeLayout) view.findViewById(R.id.rl_personal_funding);
        rl_personal_setting = (RelativeLayout) view.findViewById(R.id.rl_personal_setting);
    }

    public void initData(){
        getUserInfo();
        judgePayView();
    }

    public void setListener(){
        rl_personal_funding.setOnClickListener(this);
        rl_personal_setting.setOnClickListener(this);
    }

    public View getView() {

        return view;
    }

    private void getUserInfo(){
        OkHttpUtils.get(Urls.PERSONAL_CENTER)
                .execute(new userInfoCallback(context.getActivity()));
    }

    private void judgePayView(){
        String isPayOpen = UserUtils.getPay(context.getActivity());
        String isUserFundsOpen = UserUtils.getFundsOpen(context.getActivity());
        if(!StringUtils.isEmpty(isPayOpen) && !StringUtils.isEmpty(isUserFundsOpen) ){
            if("1".equals(isPayOpen) && "1".equals(isUserFundsOpen)){
                rl_personal_funding.setVisibility(View.VISIBLE);
            }else{
                rl_personal_funding.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rl_personal_funding:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_WODELBY_WDZJ);
                goToFundingActivity();
                break;
            case R.id.rl_personal_setting:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_WODELBY_SZ);
                goToSettingActivity();
                break;
        }
    }

    private void goToFundingActivity(){
        PageSwitchUtils.goToActivity(context.getActivity(),FundingManageActivity.class);
    }

    private void goToSettingActivity(){
        PageSwitchUtils.goToActivity(context.getActivity(),SettingActivity.class);
    }

    private class userInfoCallback extends DialogCallback<UserInfoResponse>{

        public userInfoCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, UserInfoResponse userInfoResponse, Request request, @Nullable Response response) {
            if(userInfoResponse != null){
                String username = userInfoResponse.getData().getUsername();
                String phone = userInfoResponse.getData().getPhone();
                String sex = userInfoResponse.getData().getSex();

                if(!StringUtil.isEmpty(phone)){
                    tv_personal_userPhone.setText(phone);
                    UserUtils.setMobile(context.getActivity(),phone);
                }else{
                   if(!StringUtil.isEmpty(username)){
                       tv_personal_userPhone.setText(username);
                   }
                }

                if(!StringUtil.isEmpty(sex)){
                    if(StringUtil.isEqual("2",sex)){
                        iv_personal_user.setImageResource(R.mipmap.iv_girl_user);
                    }else{
                        iv_personal_user.setImageResource(R.mipmap.iv_boy_user);
                    }
                }
            }
        }
    }

}
