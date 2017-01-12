package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/19.
 */
public class MDevice implements Parcelable {

    @JSONField(name="isChecked")
    public int isChecked;
    @JSONField(name="name")
    public String name;
    @JSONField(name="type")
    public String type;
    @JSONField(name="code")
    public String code;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.isChecked);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeString(this.code);
    }

    public MDevice() {
    }

    protected MDevice(Parcel in) {
        this.isChecked = in.readInt();
        this.name = in.readString();
        this.type = in.readString();
        this.code = in.readString();
    }

    public static final Creator<MDevice> CREATOR = new Creator<MDevice>() {
        @Override
        public MDevice createFromParcel(Parcel source) {
            return new MDevice(source);
        }

        @Override
        public MDevice[] newArray(int size) {
            return new MDevice[size];
        }
    };
}
