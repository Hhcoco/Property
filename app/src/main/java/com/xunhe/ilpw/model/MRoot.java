package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/8/3.
 */
public class MRoot implements Parcelable {

    /**/
    @JSONField(name = "building")
    public String building;
    /**/
    @JSONField(name = "units")
    public List<MUnits> units;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.building);
        dest.writeTypedList(this.units);
    }

    public MRoot() {
    }

    protected MRoot(Parcel in) {
        this.building = in.readString();
        this.units = in.createTypedArrayList(MUnits.CREATOR);
    }

    public static final Creator<MRoot> CREATOR = new Creator<MRoot>() {
        @Override
        public MRoot createFromParcel(Parcel source) {
            return new MRoot(source);
        }

        @Override
        public MRoot[] newArray(int size) {
            return new MRoot[size];
        }
    };
}
