package com.utils.eventbus;




/**
 * 第三方解耦事件通讯工具--EventBus的配合组件
 * EventBus开源地址：https://github.com/greenrobot/EventBus.git
 * event 事件，包含类型和数据
 */
public class EventAction {
    public Object type;
    public Object data;

    public EventAction(Object type) {
        this.type = type;
    }

    public EventAction(Object type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public Object getType() {
        return type;
    }
}
