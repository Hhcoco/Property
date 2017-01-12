package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/15.
 */
public class MLoginResult implements Parcelable {

    /**/
    @JSONField(name="userId")
    public String userId;
    /**/
    @JSONField(name="token")
    public String token;
    /**/
    @JSONField(name="phone")
    public String phone;
    /**/
    @JSONField(name="type")
    public String type;
    /**/
    @JSONField(name="head_picture")
    public String head_picture;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.token);
        dest.writeString(this.phone);
        dest.writeString(this.type);
        dest.writeString(this.head_picture);
    }

    public MLoginResult() {
    }

    protected MLoginResult(Parcel in) {
        this.userId = in.readString();
        this.token = in.readString();
        this.phone = in.readString();
        this.type = in.readString();
        this.head_picture = in.readString();
    }

    public static final Creator<MLoginResult> CREATOR = new Creator<MLoginResult>() {
        @Override
        public MLoginResult createFromParcel(Parcel source) {
            return new MLoginResult(source);
        }

        @Override
        public MLoginResult[] newArray(int size) {
            return new MLoginResult[size];
        }
    };
}
