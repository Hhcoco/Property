package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/8/3.
 */
public class MHouses implements Parcelable {

    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="house")
    public String house;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.house);
    }

    public MHouses() {
    }

    protected MHouses(Parcel in) {
        this.id = in.readString();
        this.house = in.readString();
    }

    public static final Creator<MHouses> CREATOR = new Creator<MHouses>() {
        @Override
        public MHouses createFromParcel(Parcel source) {
            return new MHouses(source);
        }

        @Override
        public MHouses[] newArray(int size) {
            return new MHouses[size];
        }
    };
}
