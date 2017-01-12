package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/28.
 */
public class MApplyRecordItem implements Parcelable{

    /**/
    @JSONField(name="username")
    public String username;
    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="phone")
    public String phone;
    /**/
    @JSONField(name="created_at")
    public String created_at;
    /**/
    @JSONField(name="state")
    public String state;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.username);
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.created_at);
        dest.writeString(this.state);
    }

    public MApplyRecordItem() {
    }

    protected MApplyRecordItem(Parcel in) {
        this.username = in.readString();
        this.id = in.readString();
        this.phone = in.readString();
        this.created_at = in.readString();
        this.state = in.readString();
    }

    public static final Creator<MApplyRecordItem> CREATOR = new Creator<MApplyRecordItem>() {
        @Override
        public MApplyRecordItem createFromParcel(Parcel source) {
            return new MApplyRecordItem(source);
        }

        @Override
        public MApplyRecordItem[] newArray(int size) {
            return new MApplyRecordItem[size];
        }
    };
}
