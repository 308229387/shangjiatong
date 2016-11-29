package com.android.gmacs.album;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by YanQi on 2016/10/10.
 */

public class ImageUrlArrayListWrapper implements Parcelable {

    public ArrayList<String> mList = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mList);
    }

    public static final Creator CREATOR = new Creator<ImageUrlArrayListWrapper>() {
        @Override
        public ImageUrlArrayListWrapper createFromParcel(Parcel source) {
            return new ImageUrlArrayListWrapper(source);
        }

        @Override
        public ImageUrlArrayListWrapper[] newArray(int size) {
            return new ImageUrlArrayListWrapper[size];
        }
    };

    public ImageUrlArrayListWrapper(ArrayList<String> arrayList) {
        mList.addAll(arrayList);
    }

    private ImageUrlArrayListWrapper(Parcel source) {
        source.readStringList(mList);
    }

}
