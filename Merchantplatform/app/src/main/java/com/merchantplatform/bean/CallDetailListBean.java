package com.merchantplatform.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 58 on 2016/12/17.
 */

public class CallDetailListBean implements Parcelable {

    private String time;
    private int type;
    private long duration;

    public CallDetailListBean() {

    }

    protected CallDetailListBean(Parcel in) {
        time = in.readString();
        type = in.readInt();
        duration = in.readLong();
    }

    public static final Creator<CallDetailListBean> CREATOR = new Creator<CallDetailListBean>() {
        @Override
        public CallDetailListBean createFromParcel(Parcel in) {
            return new CallDetailListBean(in);
        }

        @Override
        public CallDetailListBean[] newArray(int size) {
            return new CallDetailListBean[size];
        }
    };

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeInt(type);
        dest.writeLong(duration);
    }
}