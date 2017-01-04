package com.db.dao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by SongYongmeng on 2016/12/21.
 */
@Entity(generateConstructors = false)
public class SystemNotificationDetial {
    @Id
    public Long id;
    public String userId;
    public String content;
    public int contentType;
    public String describe;
    public Long sortId;
    public String title;
    public int type;
    public int isReaded;
    public SystemNotificationDetial() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getContentType() {
        return this.contentType;
    }
    public void setContentType(int contentType) {
        this.contentType = contentType;
    }
    public String getDescribe() {
        return this.describe;
    }
    public void setDescribe(String describe) {
        this.describe = describe;
    }
    public Long getSortId() {
        return this.sortId;
    }
    public void setSortId(Long sortId) {
        this.sortId = sortId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public int getIsReaded() {
        return this.isReaded;
    }
    public void setIsReaded(int isReaded) {
        this.isReaded = isReaded;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

}
