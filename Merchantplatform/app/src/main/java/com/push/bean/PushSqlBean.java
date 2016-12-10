package com.push.bean;

/**
 * Created by linyueyang on 15/9/30.
 */
public class PushSqlBean extends PushBean {
    private int id; // 存入本地数据库时需要
    private String userId;
    protected String title; // 不同于外面的title和message, 这个是消息列表显示的title
    protected String message; // 消息列表显示的message
    protected String detail;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
