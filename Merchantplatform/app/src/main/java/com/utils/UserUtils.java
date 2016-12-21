package com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.merchantplatform.application.HyApplication;

/**
 * Created by SongYongmeng on 2016/11/30.
 */

public class UserUtils {
    private static final String USER_SP_NAME = "user";
    private static final String USER_ID = "userId";
    private static final String USER_VALIDATE = "validate";
    private static final String USER_FACE = "face";
    private static final String USER_NAME = "name";
    private static final String USER_MOBILE = "mobile";
    private static String userId = "";
    public static int hasValidate = 0; //默认未认证，1代表已经认证
    private static String face = "";
    private static String name = "";
    private static String mobile = "";

    public static void setUserId(Context context, String userId) {
        SharedPreferences sp = context.getSharedPreferences(USER_SP_NAME, 0);
        UserUtils.userId = userId;
        sp.edit().putString(USER_ID, userId).commit();
    }

    public static String getUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = HyApplication.getApplication().getSharedPreferences(USER_SP_NAME, 0).getString(USER_ID, "");
        }
        return userId;
    }

    /**
     * 手机号已验证
     * @param context
     */
    public static void hasValidate(Context context){
        SharedPreferences sp = context.getSharedPreferences(USER_SP_NAME, 0);
        hasValidate = 1;
        sp.edit().putInt(USER_VALIDATE, hasValidate).commit();
    }
    /**
     * 手机号是否得到验证
     * @param context
     * @return
     */
    public static boolean isValidate(Context context){
        hasValidate = context.getSharedPreferences(USER_SP_NAME, 0).getInt(USER_VALIDATE, 0);

        if(hasValidate == 1){
            return true;
        }
        return false;
    }

    public static void setFace(Context context, String face) {
        SharedPreferences sp = context.getSharedPreferences(USER_SP_NAME, 0);
        UserUtils.face = face;
        sp.edit().putString(USER_FACE, face).commit();
    }

    public static String getFace() {
        if (TextUtils.isEmpty(face)) {
            face = HyApplication.getApplication().getSharedPreferences(USER_SP_NAME, 0).getString(USER_FACE, "");
        }
        return face;
    }

    public static void setName(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(USER_SP_NAME, 0);
        UserUtils.name = name;
        sp.edit().putString(USER_NAME, name).commit();
    }

    public static String getName() {
        if (TextUtils.isEmpty(name)) {
            name = HyApplication.getApplication().getSharedPreferences(USER_SP_NAME, 0).getString(USER_NAME, "");
        }
        return name;
    }

    public static void setMobile(Context context, String mobile) {
        SharedPreferences sp = context.getSharedPreferences(USER_SP_NAME, 0);
        UserUtils.mobile = mobile;
        sp.edit().putString(USER_MOBILE, mobile).commit();
    }

    public static String getMobile() {
        if (TextUtils.isEmpty(mobile)) {
            mobile = HyApplication.getApplication().getSharedPreferences(USER_SP_NAME, 0).getString(USER_MOBILE, "");
        }
        return mobile;
    }

    public static void clearUserInfo(Context context){
        context.getSharedPreferences(USER_SP_NAME, 0).edit().putString(USER_ID, "").commit();
        context.getSharedPreferences(USER_SP_NAME, 0).edit().putInt(USER_VALIDATE, 0).commit();
        userId = "";
        hasValidate= 0;
    }
}
