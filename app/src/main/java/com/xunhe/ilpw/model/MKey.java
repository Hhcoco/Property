package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/15.
 */
public class MKey implements Parcelable {

    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="user_id")
    public String user_id;
    /**/
    @JSONField(name="name")
    public String name;
    /*门禁设备号*/
    @JSONField(name="guard_code")
    public String guard_code;
    /*社区编号*/
    @JSONField(name="depart_code")
    public String depart_code;
    /*钥匙*/
    @JSONField(name="key_str")
    public String key_str;
    /*有效时间*/
    @JSONField(name="expire_time")
    public String expire_time;
    /*类型*/
    @JSONField(name="type")
    public String type;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_id);
        dest.writeString(this.name);
        dest.writeString(this.guard_code);
        dest.writeString(this.depart_code);
        dest.writeString(this.key_str);
        dest.writeString(this.expire_time);
        dest.writeString(this.type);
    }

    public MKey() {
    }

    protected MKey(Parcel in) {
        this.id = in.readString();
        this.user_id = in.readString();
        this.name = in.readString();
        this.guard_code = in.readString();
        this.depart_code = in.readString();
        this.key_str = in.readString();
        this.expire_time = in.readString();
        this.type = in.readString();
    }

    public static final Creator<MKey> CREATOR = new Creator<MKey>() {
        @Override
        public MKey createFromParcel(Parcel source) {
            return new MKey(source);
        }

        @Override
        public MKey[] newArray(int size) {
            return new MKey[size];
        }
    };
}
