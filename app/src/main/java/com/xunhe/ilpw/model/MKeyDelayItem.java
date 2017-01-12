package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/21.
 */
public class MKeyDelayItem implements Parcelable {

    /**/
    @JSONField(name="updated_at")
    public String updated_at;
    /**/
    @JSONField(name="renewl_month")
    public String renewl_month;
    /**/
    @JSONField(name="key_name")
    public String key_name;
    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="is_delete")
    public String is_delete;
    /**/
    @JSONField(name="depart_code")
    public String depart_code;
    /**/
    @JSONField(name="property_user_id")
    public String property_user_id;
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
    @JSONField(name="key_id")
    public String key_id;
    /**/
    @JSONField(name="user_name")
    public String user_name;

    /**/
    @JSONField(name="user_id")
    public String user_id;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.updated_at);
        dest.writeString(this.renewl_month);
        dest.writeString(this.key_name);
        dest.writeString(this.id);
        dest.writeString(this.is_delete);
        dest.writeString(this.depart_code);
        dest.writeString(this.property_user_id);
        dest.writeString(this.phone);
        dest.writeString(this.created_at);
        dest.writeString(this.state);
        dest.writeString(this.key_id);
        dest.writeString(this.user_name);
        dest.writeString(this.user_id);
    }

    public MKeyDelayItem() {
    }

    protected MKeyDelayItem(Parcel in) {
        this.updated_at = in.readString();
        this.renewl_month = in.readString();
        this.key_name = in.readString();
        this.id = in.readString();
        this.is_delete = in.readString();
        this.depart_code = in.readString();
        this.property_user_id = in.readString();
        this.phone = in.readString();
        this.created_at = in.readString();
        this.state = in.readString();
        this.key_id = in.readString();
        this.user_name = in.readString();
        this.user_id = in.readString();
    }

    public static final Creator<MKeyDelayItem> CREATOR = new Creator<MKeyDelayItem>() {
        @Override
        public MKeyDelayItem createFromParcel(Parcel source) {
            return new MKeyDelayItem(source);
        }

        @Override
        public MKeyDelayItem[] newArray(int size) {
            return new MKeyDelayItem[size];
        }
    };
}
