package com.merchantplatform.model;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;

import com.Utils.SystemNotificationInfoAction;
import com.Utils.UserUtils;
import com.android.gmacs.fragment.ConversationListFragment;
import com.callback.DialogCallback;
import com.dataStore.AppPrefersUtil;
import com.db.dao.SystemNotificationDetial;
import com.db.helper.SystemNotificationOperate;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.bean.GlobalResponse;
import com.merchantplatform.fragment.CallMessageFragment;
import com.merchantplatform.fragment.InfoListFragment;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.merchantplatform.service.AppDownloadService;
import com.okhttputils.OkHttpUtils;
import com.ui.HomepageBottomButton;
import com.ui.dialog.UpdateDialog;
import com.utils.AppInfoUtils;
import com.utils.StringUtil;
import com.utils.UpdateUtils;
import com.utils.Urls;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton3, bottomButton4;
    private ConversationListFragment conversationListFragment;
    private CallMessageFragment callMessageFragment;
    private PersonalCenterFragment personalCenterFragment;
    private InfoListFragment infoListFragment;
    private Fragment mFragment;
    private FragmentManager fragmentManager;

    private UpdateDialog mUpdateDialog;

    public HomepageModel(HomepageActivity context) {
        this.context = context;
    }

    public void init() {
        bottomButton1 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button1);
        bottomButton2 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button2);
        bottomButton3 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button3);
        bottomButton4 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button4);
        setListener();
        setInfo();
        registerEventBus();
    }

    private void setListener() {
        bottomButton1.setOnClickListener(this);
        bottomButton2.setOnClickListener(this);
        bottomButton3.setOnClickListener(this);
        bottomButton4.setOnClickListener(this);
    }

    public void setInfo() {
        bottomButton1.setSelectedState();
        bottomButton1.setTextInfo("消息");
        bottomButton2.setTextInfo("电话");
        bottomButton3.setTextInfo("帖子");
        bottomButton4.setTextInfo("我");
        bottomButton1.setDrawableInfo(R.drawable.tab_menu_setting);
        bottomButton2.setDrawableInfo(R.drawable.tab_menu_call);
        bottomButton3.setDrawableInfo(R.drawable.tab_menu_info);
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
        bottomButton3.registerState();
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


        if (fragmentManager.findFragmentByTag("InfoListFragment") == null)
            infoListFragment = new InfoListFragment();
        else
            infoListFragment = (InfoListFragment) fragmentManager.findFragmentByTag("InfoListFragment");


        mFragment = conversationListFragment;
    }

    public void createFragmentManagerAndShow() {
        judgeFragmentAdded(conversationListFragment);

        if (callMessageFragment.isAdded())
            fragmentManager.beginTransaction().hide(callMessageFragment).commit();

        if (personalCenterFragment.isAdded())
            fragmentManager.beginTransaction().hide(personalCenterFragment).commit();

        if (infoListFragment.isAdded())
            fragmentManager.beginTransaction().hide(infoListFragment).commit();

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
            if(fragment.equals(infoListFragment))
                tag = "InfoListFragment";
            fragmentManager.beginTransaction().hide(mFragment)
                    .add(R.id.main_fragment, fragment, tag).show(fragment).commit();
        } else
            fragmentManager.beginTransaction().hide(mFragment).show(fragment).commit();

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
            case R.id.homepage_bottom_button3:
                LogUmengAgent.ins().log(LogUmengEnum.LOG_DH_TZ);//添加埋点信息
                dealWithClick(bottomButton3, infoListFragment);
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
                saveGlobalParams(globalResponse);
                updateVersion(globalResponse);
            }
        }
    }

    private void saveGlobalParams(GlobalResponse globalResponse) {
        String isPayOpen = globalResponse.getData().getIsPayOpen();
        String isUserFundsOpen = globalResponse.getData().getIsUserFundsOpen();
        String staffContactPhone = globalResponse.getData().getStaffContactPhone();
        if(!TextUtils.isEmpty(isPayOpen))
        UserUtils.setPay(context,isPayOpen);
        if(!TextUtils.isEmpty(isUserFundsOpen))
        UserUtils.setFundsOpen(context,isUserFundsOpen);
        if(!TextUtils.isEmpty(staffContactPhone))
        UserUtils.setStaffPhone(context,staffContactPhone);
    }

    private void updateVersion(GlobalResponse globalResponse) {
        final String version = globalResponse.getData().getVersion();
        String appUrl = globalResponse.getData().getAppUrl();
        String isForceUpdate = globalResponse.getData().getIsForceUpdate();

        try {
            if(!TextUtils.isEmpty(version) && !TextUtils.isEmpty(appUrl) && !TextUtils.isEmpty(isForceUpdate)){
                int currentVersionNum = Integer.parseInt(AppInfoUtils.getVersionCode(context));
                int versionNum = Integer.parseInt(version);
                boolean isUpdate = StringUtil.compareVersion(versionNum, currentVersionNum);
                String  saveVersion =AppPrefersUtil.getInstance().getCheckVersionUpdateFlag();
                if(!TextUtils.isEmpty(saveVersion)){
                    int saveVersionFlag = Integer.parseInt(saveVersion);
                    boolean isAlertUpdate = StringUtil.compareVersion(versionNum, saveVersionFlag);
                    if(isAlertUpdate){
                        checkUpdate(version, appUrl, isForceUpdate, isUpdate);
                    }

                }else{
                    checkUpdate(version, appUrl, isForceUpdate, isUpdate);
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkUpdate(final String version, String appUrl, String isForceUpdate, boolean isUpdate) {
        if (isUpdate) {
            mUpdateDialog = UpdateUtils.getInstance().showUpateDialog(context,appUrl,isForceUpdate);
            mUpdateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    AppPrefersUtil.getInstance().saveCheckVersionUpdateFlag(version);
                }
            });
        }
    }


    public void unregusterEventBus() {
        EventBus.getDefault().unregister(context);
    }


}
