package com.okhttputils.callback;

import okhttp3.Response;

/**
 * 描    述：返回字符串类型的数据
 */
public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String parseNetworkResponse(Response response) throws Exception {
        return response.body().string();
    }
}
