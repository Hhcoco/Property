package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/20.
 */
public class MRecordItem implements Parcelable {

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
    /**/
    @JSONField(name="id_card")
    public String id_card;


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
        dest.writeString(this.id_card);
    }

    public MRecordItem() {
    }

    protected MRecordItem(Parcel in) {
        this.username = in.readString();
        this.id = in.readString();
        this.phone = in.readString();
        this.created_at = in.readString();
        this.state = in.readString();
        this.id_card = in.readString();
    }

    public static final Creator<MRecordItem> CREATOR = new Creator<MRecordItem>() {
        @Override
        public MRecordItem createFromParcel(Parcel source) {
            return new MRecordItem(source);
        }

        @Override
        public MRecordItem[] newArray(int size) {
            return new MRecordItem[size];
        }
    };
}
