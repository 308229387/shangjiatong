package com.android.gmacs.event;

import com.common.gmacs.parse.pubcontact.PublicContactInfo;

import java.util.List;

/**
 * Created by zhaobing on 16/7/26.
 */
public class PublicAccountListEvent {
    List<PublicContactInfo> publicContactInfos;

    public PublicAccountListEvent(List<PublicContactInfo> publicContactInfos) {
        this.publicContactInfos = publicContactInfos;
    }

    public List<PublicContactInfo> getPublicContactInfos() {
        return publicContactInfos;
    }
}
