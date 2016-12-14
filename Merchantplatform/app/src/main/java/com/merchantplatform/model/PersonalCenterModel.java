package com.merchantplatform.model;

import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.merchantplatform.activity.FundingManageActivity;
import com.merchantplatform.activity.SettingActivity;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.ui.TitleBar;
import com.utils.DisplayUtils;
import com.utils.PageSwitchUtils;

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

    public void setListener(){
        rl_personal_funding.setOnClickListener(this);
        rl_personal_setting.setOnClickListener(this);
    }

    public View getView() {

        return view;
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

}
