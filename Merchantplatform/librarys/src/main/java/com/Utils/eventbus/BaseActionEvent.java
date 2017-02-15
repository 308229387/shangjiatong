package com.Utils.eventbus;


/**
 * Created by linyueyang on 17/2/14.
 * <p>
 * EventBus事件类基类
 */

public class BaseActionEvent {

    public BaseActionEvent() {

    }

    public BaseActionEvent(Object data) {
        this.data = data;
    }

    Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
