package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/8/3.
 */
public class MUnits implements Parcelable {

    /**/
    @JSONField(name="unit")
    public String unit;
    /**/
    @JSONField(name="houses")
    public List<MHouses> houses;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.unit);
        dest.writeTypedList(this.houses);
    }

    public MUnits() {
    }

    protected MUnits(Parcel in) {
        this.unit = in.readString();
        this.houses = in.createTypedArrayList(MHouses.CREATOR);
    }

    public static final Creator<MUnits> CREATOR = new Creator<MUnits>() {
        @Override
        public MUnits createFromParcel(Parcel source) {
            return new MUnits(source);
        }

        @Override
        public MUnits[] newArray(int size) {
            return new MUnits[size];
        }
    };
}
