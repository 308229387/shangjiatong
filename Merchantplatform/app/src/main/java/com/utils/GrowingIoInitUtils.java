package com.utils;

import com.growingio.android.sdk.collection.Configuration;
import com.growingio.android.sdk.collection.GrowingIO;
import com.merchantplatform.application.HyApplication;

/**
 * Created by 58 on 2016/12/5.
 */

public class GrowingIoInitUtils {

    public GrowingIoInitUtils(HyApplication application) {
        GrowingIO.startWithConfiguration(application, new Configuration()
                .useID()
                .trackAllFragments()
                .setChannel(AppInfoUtils.getChannelId(application)));

    }
}
