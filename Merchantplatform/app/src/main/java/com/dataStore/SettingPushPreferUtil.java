package com.dataStore;

import android.content.Context;

import com.utils.UserUtils;

/**
 * Created by 58 on 2016/12/9.
 */

public class SettingPushPreferUtil extends BaseSharedPrefersStore{

    private static final String PREFER_FILE_NAME = "SettingPushPreferUtil";

    private static final String PUSH_SOUND_ALERT ="PushSoundAlert";

    private static final String PUSH_VIBRATE_ALERT = "PushVibrateAlert";

    private static SettingPushPreferUtil mInstance;

    public SettingPushPreferUtil(Context context) {
        super(context, PREFER_FILE_NAME);
    }

    public static SettingPushPreferUtil getInstance(Context context){
        if(mInstance == null){
            synchronized (SettingPushPreferUtil.class){
                if(mInstance == null){
                    mInstance = new SettingPushPreferUtil(context);
                }
            }
        }
        return mInstance;
    }

    public boolean isPushSoundAlertOpened(){
        return getBoolean(PUSH_SOUND_ALERT ,true);
    }

    public void savePushSoundAlertState(boolean state) {
        saveBoolean(PUSH_SOUND_ALERT, state);
    }

    public boolean isPushVibrateAlertOpened() {
        return getBoolean(PUSH_VIBRATE_ALERT, true);
    }

    public void savePushVibrateAlertState(boolean state) {
        saveBoolean(PUSH_VIBRATE_ALERT, state);
    }
}
