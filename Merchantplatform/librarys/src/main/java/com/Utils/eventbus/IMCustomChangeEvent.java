package com.Utils.eventbus;

/**
 * Created by linyueyang on 17/2/23.
 */

public class IMCustomChangeEvent extends BaseActionEvent {
    public IMCustomChangeEvent(Object data) {
        this.data = data;
    }
}
