package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/21.
 */
public class MHistroyRecordItem implements Parcelable {

    /**/
    @JSONField(name = "updated_at")
    public String updated_at;
    /**/
    @JSONField(name = "way")
    public String way;
    /**/
    @JSONField(name = "username")
    public String username;
    /**/
    @JSONField(name = "id")
    public String id;
    /**/
    @JSONField(name = "is_delete")
    public String is_delete;
    /**/
    @JSONField(name = "phone")
    public String phone;
    /**/
    @JSONField(name = "created_at")
    public String created_at;
    /**/
    @JSONField(name = "guard_code")
    public String guard_code;

    /**/
    @JSONField(name = "type")
    public String type;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.updated_at);
        dest.writeString(this.way);
        dest.writeString(this.username);
        dest.writeString(this.id);
        dest.writeString(this.is_delete);
        dest.writeString(this.phone);
        dest.writeString(this.created_at);
        dest.writeString(this.guard_code);
        dest.writeString(this.type);
    }

    public MHistroyRecordItem() {
    }

    protected MHistroyRecordItem(Parcel in) {
        this.updated_at = in.readString();
        this.way = in.readString();
        this.username = in.readString();
        this.id = in.readString();
        this.is_delete = in.readString();
        this.phone = in.readString();
        this.created_at = in.readString();
        this.guard_code = in.readString();
        this.type = in.readString();
    }

    public static final Creator<MHistroyRecordItem> CREATOR = new Creator<MHistroyRecordItem>() {
        @Override
        public MHistroyRecordItem createFromParcel(Parcel source) {
            return new MHistroyRecordItem(source);
        }

        @Override
        public MHistroyRecordItem[] newArray(int size) {
            return new MHistroyRecordItem[size];
        }
    };
}
