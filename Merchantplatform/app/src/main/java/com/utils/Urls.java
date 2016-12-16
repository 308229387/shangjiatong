package com.utils;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：请求Url汇总类
 */

public class Urls {
    /**
     * 基础Url
     */
    public static final String BASE_URL = "http://hyapp.58.com/";
    /**
     * 基础Url_APP
     */
    public static final String BASE_URL_APP = BASE_URL + "app/";
    /**
     * test
     */
    public static final String URL_METHOD = BASE_URL + "test";
    /**
     * 基础Url_APP_MYCENTER
     */
    public static final String BASE_URL_MYCENTER =BASE_URL_APP + " mycenter/";
    /**
     * im获取登陆token
     */
    public static final String IM_TOKEN = "http://10.9.192.190:9875/swap/im";
    /**
     * 400电话请求增量数据接口
     */
    public static final String PHONE_INCREASE_DATA = BASE_URL_APP + "phone/increasedata";
    /**
     * 400电话回拨上传数据接口
     */
    public static final String PHONE_UPLOAD_DATA = BASE_URL_APP + "phone/uploaddata";
    /**
     * 充值页面接口
     */
    public static final String RECHARGE = "http://paycenter.58.com/wapaccount?payfrom=22&platfrom=app";
    /**
     * 充值页面要拦截的URL
     */
    public static final String RECHARGE_INTERCEPT_URL = "http://paycenter.capital/?jump=true&payfrom=22&transType=";
    /**
     * 充值SDK回调接口
     */
    public static final String RECHARGE_NOTIFY_URL = "http://paycenter.58.com";

    public static final String PERSONAL_FEEDBACK = "";

    public static final String GET_VALIDE_CODE = "";

    public static final String VALIDATE = "";
    /**
     * 个人中心获取用户基本信息
     */
    public static final String PERSONAL_CENTER = BASE_URL_MYCENTER + "userbasicinfo";
}
