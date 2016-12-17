package com.merchantplatform.model;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.gmacs.fragment.ConversationListFragment;
import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.bean.GlobalResponse;
import com.merchantplatform.fragment.BaseFragment;
import com.merchantplatform.fragment.Fragment1;
import com.merchantplatform.fragment.CallMessageFragment;
import com.merchantplatform.fragment.Fragment3;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.merchantplatform.service.AppDownloadService;
import com.okhttputils.OkHttpUtils;
import com.ui.HomepageBottomButton;
import com.utils.AppInfoUtils;
import com.utils.StringUtil;
import com.utils.Urls;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton4;
    private BaseFragment fragment1, fragment2, fragment3, fragment4;
    private ConversationListFragment conversationListFragment;
    private Fragment mFragment;

    public HomepageModel(HomepageActivity context) {
        this.context = context;
    }

    public void init() {
        bottomButton1 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button1);
        bottomButton2 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button2);
        bottomButton4 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button4);
        setListener();
        setInfo();
    }

    private void setListener() {
        bottomButton1.setOnClickListener(this);
        bottomButton2.setOnClickListener(this);
        bottomButton4.setOnClickListener(this);
    }

    public void setInfo() {
        bottomButton1.setSelectedState();
        bottomButton1.setTextInfo("消息");
        bottomButton2.setTextInfo("电话");
        bottomButton4.setTextInfo("我");
        bottomButton1.setDrawableInfo(R.drawable.tab_menu_setting);
        bottomButton2.setDrawableInfo(R.drawable.tab_menu_call);
        bottomButton4.setDrawableInfo(R.drawable.tab_menu_mine);
    }

    private void selectThis(HomepageBottomButton v) {
        registerState();
        v.dismissRedHot();
        if (!v.isSelected())
            v.setSelectedState();
    }

    private void registerState() {
        bottomButton1.registerState();
        bottomButton2.registerState();
        bottomButton4.registerState();
    }

    public void createFragment() {
        conversationListFragment = new ConversationListFragment();
        fragment2 = new CallMessageFragment();
        fragment3 = new Fragment3();
        fragment4 = new PersonalCenterFragment();
        mFragment = conversationListFragment;
    }

    public void createFragmentManagerAndShow() {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, mFragment).commit();
    }

    private void switchFragment(Fragment fragment) {
        if (mFragment != fragment)
            isNotShowing(fragment);
    }

    private void isNotShowing(Fragment fragment) {
        judgeFragmentAdded(fragment);
        mFragment = fragment;
    }

    private void judgeFragmentAdded(Fragment fragment) {
        if (!fragment.isAdded())
            context.getSupportFragmentManager().beginTransaction().hide(mFragment)
                    .add(R.id.main_fragment, fragment).commit();
        else
            context.getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();
    }

    private void dealWithClick(HomepageBottomButton button, Fragment fragment) {
        selectThis(button);
        switchFragment(fragment);
    }

    public void getGlobalParams(){
        OkHttpUtils.get(Urls.GLOBAL_PARAMS)
                .execute(new globalCallback(context));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_button1:
                dealWithClick(bottomButton1, conversationListFragment);
                break;
            case R.id.homepage_bottom_button2:
                dealWithClick(bottomButton2, fragment2);
                break;
            case R.id.homepage_bottom_button4:
                dealWithClick(bottomButton4, fragment4);
                break;
            default:
                break;
        }
    }

    private class globalCallback extends DialogCallback<GlobalResponse>{

        public globalCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, GlobalResponse globalResponse, Request request, @Nullable Response response) {
            if(globalResponse != null){
                String appUrl = globalResponse.getData().getAppUrl();
                String version = globalResponse.getData().getVersion();
                try {
                   int  currentVersionNum = Integer.parseInt(AppInfoUtils.getVersionCode(context));
                   int  versionNum = Integer.parseInt(version);
                    boolean isUpdate = StringUtil.compareVersion(versionNum,currentVersionNum);
                    if(isUpdate){
                        AppDownloadService.startService(context, appUrl);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
