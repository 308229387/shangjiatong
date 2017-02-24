package com.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by SongYongmeng on 2016/11/28.
 */

public class Constant {
    /**
     * buglyId
     */
    public static final String BUGLY_APP_ID = "900060310";
    /**
     * WPush的APP_ID
     */
    public static final String WPUSH_APP_ID = "1007";
    /**
     * WPush的APP_KEY
     */
    public static final String WPUSH_APP_KEY = "b04RT5u0dyWXjewN";


    public static final String MOBILE = "mobile";


    /**
     * 程序目录定义
     */
    public static class Directorys {
        /**
         * SD卡根目录
         */
        public static String SDCARD = Environment.getExternalStorageDirectory().toString();
        /**
         * 程序根目录
         */
        public static final String ROOT = SDCARD + File.separator + "MerchantPlatform" + File.separator;
        /**
         * 临时位置
         */
        public static final String TEMP = ROOT + "temp" + File.separator;
        /**
         * 缓存位置
         */
        public static final String CACHE = ROOT + "cache" + File.separator;
        /**
         * 下载
         */
        public static final String DOWNLOAD = ROOT + "download" + File.separator;

        /**
         * SD卡上图片保存的目录
         */
        public static final String IMAGEDIR = SDCARD + "image" + File.separator;
    }


    public static final String INFOID = "infoId";

    public static final String SORTID = "sortId";



}
