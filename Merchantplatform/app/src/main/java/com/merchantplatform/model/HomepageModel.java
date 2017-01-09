package com.merchantplatform.model;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.Utils.SystemGetNotificationInfoAction;
import com.Utils.SystemNotificationInfoAction;
import com.android.gmacs.fragment.ConversationListFragment;
import com.callback.DialogCallback;
import com.common.gmacs.utils.ToastUtil;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.bean.GlobalResponse;
import com.merchantplatform.fragment.BaseFragment;
import com.merchantplatform.fragment.CallMessageFragment;
import com.merchantplatform.fragment.Fragment3;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.merchantplatform.service.AppDownloadService;
import com.okhttputils.OkHttpUtils;
import com.ui.HomepageBottomButton;
import com.utils.AppInfoUtils;
import com.utils.StringUtil;
import com.utils.Urls;
import com.utils.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton4;
    private ConversationListFragment conversationListFragment;
    private CallMessageFragment callMessageFragment;
    private PersonalCenterFragment personalCenterFragment;
    private Fragment mFragment;
    private FragmentManager fragmentManager;

    public HomepageModel(HomepageActivity context) {
        this.context = context;
    }

    public void init() {
        bottomButton1 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button1);
        bottomButton2 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button2);
        bottomButton4 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button4);
        setListener();
        setInfo();
        registerEventBus();
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

    private void registerEventBus() {
        EventBus.getDefault().register(context);
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

    public void createFragmentManager() {
        fragmentManager = context.getSupportFragmentManager();
    }

    public void createFragment() {
        if (fragmentManager.findFragmentByTag("ConversationListFragment") == null)
            conversationListFragment = new ConversationListFragment();
        else
            conversationListFragment = (ConversationListFragment) fragmentManager.findFragmentByTag("ConversationListFragment");

        if (fragmentManager.findFragmentByTag("CallMessageFragment") == null)
            callMessageFragment = new CallMessageFragment();
        else
            callMessageFragment = (CallMessageFragment) fragmentManager.findFragmentByTag("CallMessageFragment");

        if (fragmentManager.findFragmentByTag("PersonalCenterFragment") == null)
            personalCenterFragment = new PersonalCenterFragment();
        else
            personalCenterFragment = (PersonalCenterFragment) fragmentManager.findFragmentByTag("PersonalCenterFragment");

        mFragment = conversationListFragment;
    }

    public void createFragmentManagerAndShow() {
        judgeFragmentAdded(conversationListFragment);

        if (callMessageFragment.isAdded())
            context.getSupportFragmentManager().beginTransaction().hide(callMessageFragment).commit();

        if (personalCenterFragment.isAdded())
            context.getSupportFragmentManager().beginTransaction().hide(personalCenterFragment).commit();

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
        if (!fragment.isAdded()) {
            String tag = "";
            if (fragment.equals(conversationListFragment))
                tag = "ConversationListFragment";
            if (fragment.equals(callMessageFragment))
                tag = "CallMessageFragment";
            if (fragment.equals(personalCenterFragment))
                tag = "PersonalCenterFragment";
            context.getSupportFragmentManager().beginTransaction().hide(mFragment)
                    .add(R.id.main_fragment, fragment, tag).show(fragment).commit();
        } else
            context.getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();

    }

    private void dealWithClick(HomepageBottomButton button, Fragment fragment) {
        selectThis(button);
        switchFragment(fragment);
    }

    public void getGlobalParams() {
        OkHttpUtils.get(Urls.GLOBAL_PARAMS)
                .execute(new globalCallback(context));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_button1:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_DY_XX);//添加埋点信息
                dealWithClick(bottomButton1, conversationListFragment);
                break;
            case R.id.homepage_bottom_button2:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_DY_DH);//添加埋点信息
                dealWithClick(bottomButton2, callMessageFragment);
                break;
            case R.id.homepage_bottom_button4:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_DY_WD);//添加埋点信息
                dealWithClick(bottomButton4, personalCenterFragment);
                break;
            default:
                break;
        }
    }

    public void getSystemNotificationInfo() {
        new Thread() {
            public void run() {
                ArrayList<SystemNotificationDetial> temp = SystemNotificationOperate.queryAll(context);
                if (temp.size() > 0)
                    EventBus.getDefault().post(new SystemNotificationInfoAction(temp.get(0).getTitle(), temp.get(0).getIsReaded()));
            }
        }.start();

    }

    private class globalCallback extends DialogCallback<GlobalResponse> {

        public globalCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, GlobalResponse globalResponse, Request request, @Nullable Response response) {
            if (globalResponse != null) {
                String appUrl = globalResponse.getData().getAppUrl();
                String isPayOpen = globalResponse.getData().getIsPayOpen();
                String version = globalResponse.getData().getVersion();
                UserUtils.setPay(context,isPayOpen);
                updateVersion(appUrl, version);
            }
        }
    }

    private void updateVersion(String appUrl, String version) {
        try {
            int currentVersionNum = Integer.parseInt(AppInfoUtils.getVersionCode(context));
            int versionNum = Integer.parseInt(version);
            boolean isUpdate = StringUtil.compareVersion(versionNum, currentVersionNum);
            if (isUpdate) {
                AppDownloadService.startService(context, appUrl);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void unregusterEventBus(){
        EventBus.getDefault().unregister(context);
    }


}
