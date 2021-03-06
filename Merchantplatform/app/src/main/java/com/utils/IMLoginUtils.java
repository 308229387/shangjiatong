package com.utils;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.Utils.Urls;
import com.Utils.UserUtils;
import com.android.gmacs.core.GmacsManager;
import com.callback.JsonCallback;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.Gmacs;
import com.common.gmacs.parse.message.GmacsUserInfo;
import com.google.gson.Gson;
import com.merchantplatform.application.HyApplication;
import com.okhttputils.OkHttpUtils;
import com.response.ImGetTokenResponse;
import com.wuba.loginsdk.external.LoginClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by SongYongmeng on 2016/11/28.
 * 描    述：获取IMtoken
 */

public class IMLoginUtils {

    private Activity context;

    String userId = LoginClient.getUserID(HyApplication.getApplication());
    String keyValue = "wb@D11ncE2Ym4xOJnSWknzi";
    String appId = "100217-wb@a2hvgoAwgHY";
    String clientType = "app";
    String source = "2";
    String key;
    String imToken;

    String tempUserId;
    ImGetTokenResponse tokenResponse;

    public IMLoginUtils(Activity context) {
        this.context = context;
        calculationKey();
        getToken();
    }

    public void calculationKey() {
        String temp = "/swap/im" + "?" + "appId=" + appId + "&clientType=" + clientType + "&source=" + source + "&userId=" + userId;
        StringBuffer temp1 = new StringBuffer();
        temp1.append(keyValue);
        temp1.append(temp);
        String temp2 = encryptToSHA(temp1.toString());
        StringBuffer temp3 = new StringBuffer();
        temp3.append(temp2);
        temp3.append(keyValue);
        key = encryptToSHA(temp3.toString());
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
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    private void getToken() {
        OkHttpUtils.get(Urls.IM_TOKEN + "?" + "appId=" + appId + "&clientType=" + clientType + "&source=" + source + "&userId=" + userId + "&key=" + key)
                .execute(new GetTokenResult());
    }

    private class GetTokenResult extends JsonCallback<String> {

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
            login(s);
        }
    }


    public void login(String s) {
        Gson temp = new Gson();
        tokenResponse = temp.fromJson(s, ImGetTokenResponse.class);


        GmacsUserInfo gmacsUserInfo = new GmacsUserInfo();
        gmacsUserInfo.avatar = UserUtils.getFace(context);
        gmacsUserInfo.userId = UserUtils.getUserId(context);
        gmacsUserInfo.userName = UserUtils.getName(context);
        gmacsUserInfo.userSource = 2;

        tempUserId = UserUtils.getUserId(context);
        imToken = tokenResponse.getToken();

        Gmacs.getInstance().setGmacsUserInfo(gmacsUserInfo);
        if (imToken != null)
            Gmacs.getInstance().loginAsync(tempUserId, "", 2, "", imToken, 0, new ClientManager.LoginCb() {

                @Override
                public void done(int i, String s) {
                    GmacsManager.getInstance().startGmacs(new MessageNotifyHelper());
                }
            });
    }
}
