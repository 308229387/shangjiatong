package com.callback;


import android.content.pm.PackageManager;

import com.dataStore.DeviceUuidFactory;
import com.merchantplatform.application.HyApplication;
import com.okhttputils.callback.AbsCallback;
import com.okhttputils.request.BaseRequest;
import com.utils.AppInfoUtils;
import com.wuba.loginsdk.external.LoginClient;

/**
 * 描    述：请求头
 */
public abstract class CommonCallback<T> extends AbsCallback<T> {
    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        //如果账户已经登录，就添加 token 等等
        try {
            request.headers("ppu", LoginClient.doGetPPUOperate(HyApplication.getApplication()))
                    .headers("version", AppInfoUtils.getVersionCode(HyApplication.getApplication()))
                    .headers("imei", DeviceUuidFactory.getInstance().getDeviceUuidString())
                    .headers("platform", "1");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
