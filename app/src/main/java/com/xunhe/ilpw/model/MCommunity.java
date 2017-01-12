package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/19.
 */
public class MCommunity implements Parcelable {

    @JSONField(name="building")
    public String building;
    @JSONField(name="depart_code")
    public String depart_code;
    @JSONField(name="unit")
    public String unit;
    @JSONField(name="depart_name")
    public String depart_name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.building);
        dest.writeString(this.depart_code);
        dest.writeString(this.unit);
        dest.writeString(this.depart_name);
    }

    public MCommunity() {
    }

    protected MCommunity(Parcel in) {
        this.building = in.readString();
        this.depart_code = in.readString();
        this.unit = in.readString();
        this.depart_name = in.readString();
    }

    public static final Creator<MCommunity> CREATOR = new Creator<MCommunity>() {
        @Override
        public MCommunity createFromParcel(Parcel source) {
            return new MCommunity(source);
        }

        @Override
        public MCommunity[] newArray(int size) {
            return new MCommunity[size];
        }
    };
}
