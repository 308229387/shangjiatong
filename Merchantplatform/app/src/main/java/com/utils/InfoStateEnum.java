package com.utils;

/**
 * Created by linyueyang on 17/1/10.
 */

public enum InfoStateEnum {
    SHOWING(1, "显示中"),
    CHECKING(2, "审核中"),
    REFUSED(3, "审核失败"),
    DELETE(4, "已删除");


    private int id;
    private String content;


    InfoStateEnum(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static InfoStateEnum getEnumById(int id) {
        for (InfoStateEnum infoStateEnum : InfoStateEnum.values()) {
            if (infoStateEnum.getId() == id) {
                return infoStateEnum;
            }
        }
        return null;
    }

}
