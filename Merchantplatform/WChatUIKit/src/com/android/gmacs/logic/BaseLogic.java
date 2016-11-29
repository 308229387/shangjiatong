package com.android.gmacs.logic;

/**
 * 逻辑处理基础类
 */
public abstract class BaseLogic {

    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 子类实现（可选）
     */
    public void init() {
        destroy();
    }

    /**
     * 子类实现（可选）
     */
    public void destroy() {
    }

}
