package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/26.
 */
public class MAd implements Parcelable {

    /**/
    @JSONField(name="title")
    public String title;
    /**/
    @JSONField(name="pic")
    public String pic;
    /**/
    @JSONField(name="target")
    public String target;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.pic);
        dest.writeString(this.target);
    }

    public MAd() {
    }

    protected MAd(Parcel in) {
        this.title = in.readString();
        this.pic = in.readString();
        this.target = in.readString();
    }

    public static final Creator<MAd> CREATOR = new Creator<MAd>() {
        @Override
        public MAd createFromParcel(Parcel source) {
            return new MAd(source);
        }

        @Override
        public MAd[] newArray(int size) {
            return new MAd[size];
        }
    };
}
