package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/8/10.
 */
public class MVersion implements Parcelable {

    /**/
    @JSONField(name="version_name")
    public String version_name;
    /**/
    @JSONField(name="version_no")
    public String version_no;
    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="version_info")
    public String version_info;
    /**/
    @JSONField(name="created_at")
    public String created_at;
    /**/
    @JSONField(name="version_url")
    public String version_url;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version_name);
        dest.writeString(this.version_no);
        dest.writeString(this.id);
        dest.writeString(this.version_info);
        dest.writeString(this.created_at);
        dest.writeString(this.version_url);
    }

    public MVersion() {
    }

    protected MVersion(Parcel in) {
        this.version_name = in.readString();
        this.version_no = in.readString();
        this.id = in.readString();
        this.version_info = in.readString();
        this.created_at = in.readString();
        this.version_url = in.readString();
    }

    public static final Creator<MVersion> CREATOR = new Creator<MVersion>() {
        @Override
        public MVersion createFromParcel(Parcel source) {
            return new MVersion(source);
        }

        @Override
        public MVersion[] newArray(int size) {
            return new MVersion[size];
        }
    };
}
