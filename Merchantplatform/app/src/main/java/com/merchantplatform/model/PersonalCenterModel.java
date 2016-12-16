package com.merchantplatform.model;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.okhttputils.OkHttpUtils;
import com.utils.DisplayUtils;
import com.utils.PageSwitchUtils;
import com.utils.Urls;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class PersonalCenterModel extends BaseModel implements View.OnClickListener{
    private PersonalCenterFragment context;
    private View view;
    private TextView tv_Personal_center,tv_personal_userPhone;
    private RelativeLayout rl_personal_funding, rl_personal_setting;


    public PersonalCenterModel(PersonalCenterFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_personal_center, container, false);
    }

    public void initView(){
        tv_Personal_center = (TextView) view.findViewById(R.id.tv_Personal_center);
        tv_personal_userPhone = (TextView) view.findViewById(R.id.tv_personal_userPhone);
        rl_personal_funding = (RelativeLayout) view.findViewById(R.id.rl_personal_funding);
        rl_personal_setting = (RelativeLayout) view.findViewById(R.id.rl_personal_setting);
    }


    public void setHeaderHeight() {
            int height = DisplayUtils.getStatusBarHeight(context.getActivity());
            int more = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
            if (tv_Personal_center != null) {
                tv_Personal_center.setPadding(0, height + more, 0, 0);
            }
    }

    public void initData(){
        getUserInfo();
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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.rl_personal_funding:
                goToFundingActivity();
                break;
            case R.id.rl_personal_setting:
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

    private class userInfoCallback extends DialogCallback<String>{

        public userInfoCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {

        }
    }

}
