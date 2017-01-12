package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/28.
 */
public class MKeyInfo implements Parcelable {

    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="depart_code")
    public String depart_code;
    /**/
    @JSONField(name="name")
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.depart_code);
        dest.writeString(this.name);
    }

    public MKeyInfo() {
    }

    protected MKeyInfo(Parcel in) {
        this.id = in.readString();
        this.depart_code = in.readString();
        this.name = in.readString();
    }

    public static final Creator<MKeyInfo> CREATOR = new Creator<MKeyInfo>() {
        @Override
        public MKeyInfo createFromParcel(Parcel source) {
            return new MKeyInfo(source);
        }

        @Override
        public MKeyInfo[] newArray(int size) {
            return new MKeyInfo[size];
        }
    };
}
