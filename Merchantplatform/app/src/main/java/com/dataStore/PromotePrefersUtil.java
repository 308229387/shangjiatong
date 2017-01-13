package com.dataStore;

import android.content.Context;

import com.Utils.UserUtils;
import com.merchantplatform.application.HyApplication;

/**
 * Created by 58 on 2017/1/12.
 */

public class PromotePrefersUtil extends BaseSharedPrefersStore{

    private static final String PREFERS_FILE_NAME = "PromotePrefersUtil";

    private String USER_ID = "";

    private static PromotePrefersUtil mInstance;

    public PromotePrefersUtil(Context context) {
        super(context, PREFERS_FILE_NAME);
        USER_ID = UserUtils.getUserId(context);
    }

    public static PromotePrefersUtil getInstance(){
        if(mInstance == null){
            synchronized (PromotePrefersUtil.class){
                if(mInstance == null){
                    mInstance = new PromotePrefersUtil(HyApplication.getApplication());
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存置顶推广成功的标志
     * @param flag
     */
    public void saveUpPromote(String flag){
        saveString( USER_ID + "_up_promote",flag);
    }

    /**
     * 获取置顶推广成功的标志
     * @return
     */
    public String getUpPromote(){
        return getString( USER_ID + "_up_promote","");
    }


    /**
     * 保存精准推广成功的标志
     * @param flag
     */
    public void savePercisionPromote(String flag){
        saveString(USER_ID + "_precision_promote",flag);
    }

    /**
     * 获取精准推广成功的标志
     * @return
     */
    public String getPercisionPromote(){
        return getString(USER_ID + "_precision_promote","");
    }

}
