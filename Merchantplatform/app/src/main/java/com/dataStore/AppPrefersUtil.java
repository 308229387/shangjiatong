package com.dataStore;

import android.content.Context;

import com.merchantplatform.application.HyApplication;

/**
 * Created by 58 on 2016/12/10.
 */

public class AppPrefersUtil extends BaseSharedPrefersStore{

    private  static final String PREFERS_FILE_NAME = "AppPrefersUtil";

    //用户反馈草稿内容
    private static final String FEEDBACK_CONTENT = "feedback_content_HISTORY";

    //版本更新
    private static final String CHECK_VERSION_UPDATE = "check_version_update";

    private static AppPrefersUtil mInstance;

    public AppPrefersUtil(Context context) {
        super(context, PREFERS_FILE_NAME);
    }

    public static AppPrefersUtil getInstance(){
        if(mInstance == null){
            synchronized (AppPrefersUtil.class){
                if(mInstance == null){
                    mInstance = new AppPrefersUtil(HyApplication.getApplication());
                }
            }
        }
        return mInstance;
    }



    /**
     * 保存用户反馈草稿
     *
     * @param content
     */
    public void saveFeedBackContent(String content) {
        saveString(FEEDBACK_CONTENT, content);
    }

    /**
     * 获取用户反馈草稿
     */
    public String getFeedBackContent() {
        return getString(FEEDBACK_CONTENT, "");
    }


    public String getCheckVersionUpdateFlag() {
        return getString(CHECK_VERSION_UPDATE, "");
    }

    public void saveCheckVersionUpdateFlag(String content) {
        saveString(CHECK_VERSION_UPDATE, content);
    }
}
