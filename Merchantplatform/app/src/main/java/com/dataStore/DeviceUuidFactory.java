package com.dataStore;

/**
 * Created by 58 on 2016/12/27.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;


import com.merchantplatform.application.HyApplication;
import com.utils.StringUtil;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

public class DeviceUuidFactory {
    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";

    protected static UUID uuid;

    private static DeviceUuidFactory defaultInstance = null;

    public static DeviceUuidFactory getInstance() {
        if (defaultInstance == null) {
            synchronized (DeviceUuidFactory.class) {
                if (defaultInstance == null) {
                    defaultInstance = new DeviceUuidFactory(HyApplication.getApplication());
                }
            }
        }
        return defaultInstance;
    }

    private DeviceUuidFactory(Context context) {

        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!StringUtil.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                if (!StringUtil.isEmpty(deviceId)) {
                                    uuid = UUID.nameUUIDFromBytes(deviceId.getBytes("utf8"));
                                } else {
                                    uuid = UUID.randomUUID();
                                }
                            }
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).apply();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }
    }

    public String getDeviceUuidString() {
        if (uuid != null) {
            return uuid.toString();
        }
        return "";
    }
}
