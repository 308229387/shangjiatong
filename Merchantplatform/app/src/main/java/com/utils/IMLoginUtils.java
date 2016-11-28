package com.utils;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import com.callback.JsonCallback;
import com.merchantplatform.application.HyApplication;
import com.okhttputils.OkHttpUtils;
import com.okhttputils.callback.AbsCallback;
import com.wuba.loginsdk.external.LoginClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/28.
 */

public class IMLoginUtils {
    String keyValue = "wb@D11ncE2Ym4xOJnSWknzi";
    String key;
    String appId = "100217-wb@a2hvgoAwgHY";
    String userId = LoginClient.getUserID(HyApplication.getApplication());
    String clientType = "app";
    String source = "2";

    public IMLoginUtils() {
        calculationKey();
        OkHttpUtils.get(Urls.IM_TOKEN+"?" + "appId=" + appId +"&clientType=" + clientType +"&source=" + source +"&userId=" + userId +"&key="+key)
                .execute(new IMLoginResult());
    }

    public void calculationKey() {
        String temp = "/swap/im" + "?" + "appId=" + appId +"&clientType=" + clientType +"&source=" + source +"&userId=" + userId ;
        StringBuffer temp1 = new StringBuffer();
        temp1.append(keyValue);
        temp1.append(temp);
        String temp2 = encryptToSHA(temp1.toString());
        StringBuffer temp3 = new StringBuffer();
        temp3.append(temp2);
        temp3.append(keyValue);
        key = encryptToSHA(temp3.toString());


//        String temp = "/common/push/bind_entity?source=2&debug=qa";
//        StringBuffer temp1 = new StringBuffer();
//        temp1.append(keyValue);
//        temp1.append(temp);
//        String temp2 = encryptToSHA(temp1.toString());
//        StringBuffer temp3 = new StringBuffer();
//        temp3.append(temp2);
//        temp3.append(keyValue);
//        key = encryptToSHA(temp3.toString());
    }

    public static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            alga.update(info.getBytes());
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String rs = byte2hex(digesta);
        return rs;
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    private class IMLoginResult extends JsonCallback<String> {

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
            ToastUtils.showToast(s);
        }
    }
}
