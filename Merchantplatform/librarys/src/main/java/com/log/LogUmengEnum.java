package com.log;

/**
 * Created by linyueyang on 16/12/17.
 * <p/>
 * 友盟测试点枚举,所有自定义埋点信息都在这里定义
 * 所有的点都要在友盟系统里面定义好
 */
public enum LogUmengEnum {

    /**
     * 1.0.0版本埋点
     */
    LOG_DIANHUAXQY_BDDH("dianhuaXQY_BDDH", "电话详情页_点击拨打电话"),
    LOG_DIANHUAXQY_RETURN("dianhuaXQY_return", "电话详情页_点击返回上一级"),
    LOG_DY_DH("DY_DH", "单页-电话"),
    LOG_DY_KFDH("DY_KFDH", "客服电话按钮"),
    LOG_DY_WD("DY_WD", "单页-我的"),
    LOG_DY_XX("DY_XX", "单页_消息"),
    LOG_HY_LOGIN("login", "登录"),
    LOG_LIAOTIANXQY_AZHSHH("liaotianXQY_AZhShH", "聊天详情页-按住说话按钮"),
    LOG_LIAOTIANXQY_BQ("liaotianXQY_BQ", "聊天详情页-表情按钮"),
    LOG_LIAOTIANXQY_JH("liaotianXQY_JH", "聊天详情页-“+”按钮"),
    LOG_LIAOTIANXQY_RETURN("liaotianXQY_return", "聊天详情页_返回按钮"),
    LOG_LIAOTIANXQY_TPCHK("liaotianXQY_TPChK", "聊天详情页-图片查看点击"),
    LOG_LIAOTIANXQY_WZ("liaotianXQY_WZ", "聊天详情页-位置按钮"),
    LOG_LIAOTIANXQY_WZFS("liaotianXQY_WZFS", "聊天详情页_文字发送按钮"),
    LOG_LIAOTIANXQY_XC("liaotianXQY_XC", "聊天详情页-相册按钮"),
    LOG_LIAOTIANXQY_XCFS("liaotianXQY_XCFS", "聊天详情页-相册-发送按钮"),
    LOG_LIAOTIANXQY_XCQX("liaotianXQY_XCQX", "聊天详情页-相册-取消按钮"),
    LOG_LIAOTIANXQY_XCYT("liaotianXQY_XCYT", "聊天详情页-相册原图按钮"),
    LOG_LIAOTIANXQY_XL("liaotianXQY_XL", "聊天详情页-下拉获取历史消息"),
    LOG_LIAOTIANXQY_YYBF("liaotianXQY_YYBF", "聊天详情页-语音播放点击"),
    LOG_LIAOTIANXQY_YYLT("liaotianXQY_YYLT", "聊天详情页-语音聊天点击"),
    LOG_LIAOTIANXQY_YYORJPAN("liaotianXQY_YYorJPAN", "聊天详情页_语音_键盘按钮"),
    LOG_LIAOTIANXQY_ZX("liaotianXQY_ZX", "聊天详情页-照相按钮"),
    LOG_LIAOTIANXQY_ZXQX("liaotianXQY_ZXQX", "聊天详情页-照相取消按钮"),
    LOG_LIAOTIANXQY_ZXSYXP("liaotianXQY_ZXSYXP", "聊天详情页-照相-使用相片按钮"),
    LOG_SHEZHIXQY_GGBDSJ("shezhiXQY_GGBDSJ", "设置详情页-更改绑定手机按钮"),
    LOG_SHEZHIXQY_GYWM("shezhiXQY_GYWM", "设置详情页-关于我们按钮"),
    LOG_SHEZHIXQY_HQYZHM("shezhiXQY_HQYZhM", "设置详情页-获取验证码按钮"),
    LOG_SHEZHIXQY_QRTCHDL("shezhiXQY_QRTChDL", "设置详情页_确认退出登录"),
    LOG_SHEZHIXQY_SJHMGGTJ("shezhiXQY_SJHMGGTJ", "设置详情页-提交更改手机号码按钮"),
    LOG_SHEZHIXQY_SYTX("shezhiXQY_SYTX", "设置详情页-声音提醒按钮"),
    LOG_SHEZHIXQY_TCZH("shezhiXQY_TCZH", "设置详情页_退出账号按钮"),
    LOG_SHEZHIXQY_XXTS("shezhiXQY_XXTS", "设置详情页-消息推送按钮"),
    LOG_SHEZHIXQY_YHXY("shezhiXQY_YHXY", "设置详情页-用户协议"),
    LOG_SHEZHIXQY_YJFK("shezhiXQY_YJFK", "设置详情页-意见反馈按钮"),
    LOG_SHEZHIXQY_YJFKTJ("shezhiXQY_YJFKTJ", "设置详情页-意见反馈提交按钮"),
    LOG_SHEZHIXQY_ZDTX("shezhiXQY_ZDTX", "设置详情页-震动提醒按钮"),
    LOG_SHEZHIXQY_ZHBD("shezhiXQY_ZHBD", "设置详情页-账号绑定按钮"),
    LOG_TONGHUALBY_DJBDDH("tonghuaLBY_DJBDDH", "通话列表页-点击拨打电话"),
    LOG_TONGHUALBY_QRSCH("tonghuaLBY_QRSCh", "通话列表页-左滑-确认删除"),
    LOG_TONGHUALBY_THJL("tonghuaLBY_THJL", "通话列表页-通话记录"),
    LOG_TONGHUALBY_WJLD("tonghuaLBY_WJLD", "通话列表页-未接来电"),
    LOG_TONGHUALBY_CHASHCH("tonghuaLBY_ChAShCh", "通话列表页-左滑删除"),
    LOG_TONGHULBY_DJJRXQY("tonghuLBY_DJJRXQY", "通话列表页-点击进入详情页"),
    LOG_WODELBY_SZ("wodeLBY_SZ", "我的列表页-设置"),
    LOG_WODELBY_WDZJ("wodeLBY_WDZJ", "我的列表页_我的资金"),
    LOG_XIAOXILBY_DELETE("xiaoxiLBY_delete", "消息列表页-删除对话（左滑）"),
    LOG_XIAOXILBY_HHDJ("xiaoxiLBY_HHDJ", "消息列表页-会话点击"),

    /**
     * 1.0.1版本埋点
     */
    LOG_DH_TZ("DH_TZ", "底部导航_帖子"),
    LOG_XX_TGZS("XX_TGZS", "消息_推广助手"),
    LOG_TZLB_TZQY("TZLB_TZQY", "帖子列表_帖子区域"),
    LOG_TZLB_JZ("TZLB_JZ", "帖子列表_精准"),
    LOG_TZLB_ZD("TZLB_ZD", "帖子列表_置顶"),
    LOG_TZXQ_JZ("TZXQ_JZ", "帖子详情_精准"),
    LOG_CZTSTC_QD("CZTSTC_QD", "充值提示弹窗_确定按钮"),
    LOG_TZ_JZSDK_FH("TZ_JZSDK_FH", "帖子_精准SDK_返回"),
    LOG_TZ_ZDSDK_FH("TZ_ZDSDK_FH", "帖子_置顶SDK_返回"),
    LOG_TGSDK_FH("TGSDK_FH", "推广SDK_返回"),
    LOG_TZXQ_FH("TZXQ_FH", "帖子详情页_返回"),
    LOG_TZXQ_ZD("TZXQ_ZD", "帖子详情页_置顶"),

    /**
     * 1.0.2版本埋点
     */
    LOG_DH_FL("DH_FL", "底部导航_福利"),
    LOG_TZXQ_FX("TZXQ_FX", "帖子详情页_分享"),
    LOG_FL_JIFEN("FL_jifen", "福利_积分总和"),
    LOG_FL_CHOUJIANG("FL_choujiang", "刮奖按钮"),
    LOG_FL_QUWANCHENG("FL_quwancheng", "福利_完成任务按钮"),
    LOG_GJ_JILU("GJ_jilu", "刮奖_中奖记录"),
    LOG_GJ_GUAJIANG("GJ_guajiang", "刮奖_点我刮奖"),
    LOG_GJ_QURENWU("GJ_qurenwu", "刮奖_去做任务"),
    LOG_GJ_JIXU("GJ_jixu", "刮奖_再抽一次"),
    LOG_GJ_QUCHAKAN("GJ_quchakan", "刮奖_去查看中奖记录"),

    /**
     * 1.2.1埋点
     */
    LOG_ZSKF("ZSKF", "专属客服");

    private String eventid;
    private String eventDescription;

    LogUmengEnum(String eventid, String eventDescription) {
        this.eventid = eventid;
        this.eventDescription = eventDescription;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

}
