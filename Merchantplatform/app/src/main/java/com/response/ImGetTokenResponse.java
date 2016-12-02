package com.response;

import java.io.Serializable;

/**
 * Created by SongYongmeng on 2016/11/30.
 */

public class ImGetTokenResponse implements Serializable {
    private String token;
    private String ttl;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }
}
