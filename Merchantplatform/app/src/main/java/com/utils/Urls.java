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
     * im获取登陆token
     */
    public static final String IM_TOKEN = "https://ppuswapapi.58.com/swap/im";
    /**
     * 400电话请求增量数据接口
     */
    public static final String PHONE_INCREASE_DATA = BASE_URL_APP + "phone/increasedata";
    /**
     * 400电话回拨上传数据接口
     */
    public static final String PHONE_UPLOAD_DATA = BASE_URL_APP + "phone/uploaddata";
}
