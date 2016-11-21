package com.callback;


import com.okhttputils.callback.AbsCallback;
import com.okhttputils.request.BaseRequest;

/**
 * 描    述：请求头
 */
public abstract class CommonCallback<T> extends AbsCallback<T> {
    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //如果账户已经登录，就添加 token 等等
        request.headers("CallBackHeaderKey1", "CallBackHeaderValue1")//
                .headers("CallBackHeaderKey2", "CallBackHeaderValue2")//
                .params("CallBackParamsKey1", "CallBackParamsValue1")//
                .params("CallBackParamsKey2", "CallBackParamsValue1")//
                .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }
}
