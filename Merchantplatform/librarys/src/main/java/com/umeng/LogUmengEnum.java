package com.umeng;

/**
 * Created by linyueyang on 16/12/17.
 * <p/>
 * 友盟测试点枚举,所有自定义埋点信息都在这里定义
 * 所有的点都要在友盟系统里面定义好
 */
public enum LogUmengEnum {

    LOG_TEST_ENUM("1", "测试埋点");//例子

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
