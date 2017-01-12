package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/7/28.
 */
public class MHouseInfo implements Parcelable {

    /**/
    @JSONField(name="houses")
    public List<MHouse> houses ;
    /**/
    @JSONField(name="keys")
    public List<MKeyInfo> keys ;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.houses);
        dest.writeTypedList(this.keys);
    }

    public MHouseInfo() {
    }

    protected MHouseInfo(Parcel in) {
        this.houses = in.createTypedArrayList(MHouse.CREATOR);
        this.keys = in.createTypedArrayList(MKeyInfo.CREATOR);
    }

    public static final Creator<MHouseInfo> CREATOR = new Creator<MHouseInfo>() {
        @Override
        public MHouseInfo createFromParcel(Parcel source) {
            return new MHouseInfo(source);
        }

        @Override
        public MHouseInfo[] newArray(int size) {
            return new MHouseInfo[size];
        }
    };
}
