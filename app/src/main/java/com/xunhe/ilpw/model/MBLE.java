package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangliang on 2016/7/20.
 */
public class MBLE implements Parcelable {

    public String bleName;
    public String bleAdress;
    public boolean isConnect;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.bleName);
        dest.writeString(this.bleAdress);
        dest.writeByte(this.isConnect ? (byte) 1 : (byte) 0);
    }

    public MBLE() {
    }

    public MBLE(String name,String adress,boolean isConnect) {
        this.bleName = name;
        this.bleAdress = adress;
        this.isConnect = isConnect;
    }

    protected MBLE(Parcel in) {
        this.bleName = in.readString();
        this.bleAdress = in.readString();
        this.isConnect = in.readByte() != 0;
    }

    public static final Creator<MBLE> CREATOR = new Creator<MBLE>() {
        @Override
        public MBLE createFromParcel(Parcel source) {
            return new MBLE(source);
        }

        @Override
        public MBLE[] newArray(int size) {
            return new MBLE[size];
        }
    };
}
