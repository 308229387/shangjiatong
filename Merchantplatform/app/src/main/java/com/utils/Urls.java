package com.utils;

/**
 * Created by SongYongmeng on 2016/11/21.
 * 描    述：请求Url汇总类
 */

public class Urls {
    /**
     * 基础Url
     */
    public static final String BASE_URL = "https://hyapp.58.com/";
    /**
     * 外网测试域名
     */
//    public static final String BASE_URL = "https://hyapp.58v5.cn/";

    /**
     * 基础Url_APP
     */
    public static final String BASE_URL_APP = BASE_URL + "app/";
    /**
     * test
     */
    public static final String URL_METHOD = BASE_URL + "test";
    /**
     * 基础Url_APP_mycenter
     */
    public static final String BASE_URL_MYCENTER = BASE_URL_APP + "mycenter/";
    /**
     * 基础url_app_global
     */
    public static final String BASE_URL_GLOBAL = BASE_URL_APP + "global/";
    /**
     * 基础帖子相关url_info
     */
    public static final String BASE_URL_INFO = BASE_URL_APP + "info/";
    /**
     * im获取登陆token
     */
    public static final String IM_TOKEN = "https://ppuswapapi.58.com/swap/im";
//    public static final String IM_TOKEN = "http://10.9.192.190:9875/swap/im";
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
    /**
     * 个人中心获取用户基本信息
     */
    public static final String PERSONAL_CENTER = BASE_URL_MYCENTER + "userbasicinfo";
    /**
     * 个人中心提交意见反馈
     */
    public static final String PERSONAL_FEEDBACK = BASE_URL_MYCENTER + "suggest";
    /**
     * 获取短信验证码
     */
    public static final String GET_VALIDATE_CODE = BASE_URL_GLOBAL + "sendcode";
    /**
     * 首次绑定手机号
     */
    public static final String VALIDATE = BASE_URL_MYCENTER + "bindphone";
    /**
     * 退出登录
     */
    public static final String LOGOUT = BASE_URL_GLOBAL + "logout";
    /**
     * 登录
     */
    public static final String LOGIN = BASE_URL_GLOBAL + "login";
    /**
     * 获取app全局参数
     */
    public static final String GLOBAL_PARAMS = BASE_URL_GLOBAL + "params";
    /**
     * 拉取系统通知
     */
    public static final String SYSTEM_NOTIFICATION = BASE_URL_APP + "message/push/system";
    /**
     * 推广助手页
     */
    public static final String PROMOTE_MESSAGE = "http://m.cube.58.com/cube/m/manage/initMessage?sourceId=205000";
    public static final String PROMOTE_OTHER_MESSAGE = "http://mcube.58.com/cube/m/manage/initMessage?sourceId=205000";
    /**
     * 置顶推广
     */
    public static final String UP_PROMOTE = "http://mcube.58.com/cube/infotop/loadpage/17131403/";
    /**
     * 精准推广
     */
    public static final String PRECISION_PROMOTE = "http://mcube.58.com/cube/m/pm/1/205000/";
    /**
     * 帖子列表
     */
    public static final String POST_LIST = BASE_URL_INFO + "list";
    /**
     * 帖子详情
     */
    public static final String POST_DETAIL = BASE_URL_INFO + "detail";
    /**
     * 获取服务器时间
     */
    public static final String GET_SERVICE_TIME = BASE_URL_GLOBAL + "currentTime";
    /**
     * 获取服务器时间
     */
    public static final String GET_TASK = BASE_URL_APP + "task/info";
    /**
     * 获取服务器时间
     */
    public static final String GET_WELFARE = BASE_URL_APP + "task/welfarehome";

    /**
     * 抽奖详情页
     */
    public static final String DAILY_LOTTERY = BASE_URL_APP + "prize/prizedraw/detail";
    /**
     * 获取奖品接口
     */
    public static final String GET_LOTTERY = BASE_URL_APP + "prize/lucky";
    /**
     * 中奖记录列表
     */
    public static final String LOTTERY_HISTORY = BASE_URL_APP + "prizerecord/list";
    /**
     * 我的奖品列表
     */
    public static final String MY_AWARD_LIST = BASE_URL_APP + "prizerecord/mylist";
    /**
     * 我的任务列表
     */
    public static final String INTEGRAL_LIST = BASE_URL_APP + "score/list";
    /**
     * 任务成功接口
     */
    public static final String TASK_SUCCESS = BASE_URL_APP + "task/donetask";

}
