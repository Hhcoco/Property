package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/8/1.
 */
public class MUploadLog implements Parcelable {

    /**/
    @JSONField(name = "guard_code")
    public String guard_code;
    /**/
    @JSONField(name = "created_at")
    public String created_at;
    /**/
    @JSONField(name = "way")
    public String way;
    /**/
    @JSONField(name = "state")
    public String state;
    /**/
    @JSONField(name = "description")
    public String description;

    public void setGuard_code(String guard_code) {
        this.guard_code = guard_code;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.guard_code);
        dest.writeString(this.created_at);
        dest.writeString(this.way);
        dest.writeString(this.state);
        dest.writeString(this.description);
    }

    public MUploadLog() {
    }

    protected MUploadLog(Parcel in) {
        this.guard_code = in.readString();
        this.created_at = in.readString();
        this.way = in.readString();
        this.state = in.readString();
        this.description = in.readString();
    }

    public static final Creator<MUploadLog> CREATOR = new Creator<MUploadLog>() {
        @Override
        public MUploadLog createFromParcel(Parcel source) {
            return new MUploadLog(source);
        }

        @Override
        public MUploadLog[] newArray(int size) {
            return new MUploadLog[size];
        }
    };
}
