package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/28.
 */
public class MHouse implements Parcelable {

    /**/
    @JSONField(name="house_id")
    public String house_id;
    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="building")
    public String building;
    /**/
    @JSONField(name="depart_code")
    public String depart_code;
    /**/
    @JSONField(name="house")
    public String house;
    /**/
    @JSONField(name="unit")
    public String unit;
    /**/
    @JSONField(name="type")
    public String type;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.house_id);
        dest.writeString(this.id);
        dest.writeString(this.building);
        dest.writeString(this.depart_code);
        dest.writeString(this.house);
        dest.writeString(this.unit);
        dest.writeString(this.type);
    }

    public MHouse() {
    }

    protected MHouse(Parcel in) {
        this.house_id = in.readString();
        this.id = in.readString();
        this.building = in.readString();
        this.depart_code = in.readString();
        this.house = in.readString();
        this.unit = in.readString();
        this.type = in.readString();
    }

    public static final Creator<MHouse> CREATOR = new Creator<MHouse>() {
        @Override
        public MHouse createFromParcel(Parcel source) {
            return new MHouse(source);
        }

        @Override
        public MHouse[] newArray(int size) {
            return new MHouse[size];
        }
    };
}
