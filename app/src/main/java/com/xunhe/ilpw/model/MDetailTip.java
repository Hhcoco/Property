package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/27.
 */
public class MDetailTip implements Parcelable {

    /**/
    @JSONField(name = "digest")
    public String digest;
    /**/
    @JSONField(name = "title")
    public String title;
    /**/
    @JSONField(name = "created_at")
    public String created_at;
    /**/
    @JSONField(name = "content")
    public String content;
    /**/
    @JSONField(name = "author_name")
    public String author_name;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.digest);
        dest.writeString(this.title);
        dest.writeString(this.created_at);
        dest.writeString(this.content);
        dest.writeString(this.author_name);
    }

    public MDetailTip() {
    }

    protected MDetailTip(Parcel in) {
        this.digest = in.readString();
        this.title = in.readString();
        this.created_at = in.readString();
        this.content = in.readString();
        this.author_name = in.readString();
    }

    public static final Creator<MDetailTip> CREATOR = new Creator<MDetailTip>() {
        @Override
        public MDetailTip createFromParcel(Parcel source) {
            return new MDetailTip(source);
        }

        @Override
        public MDetailTip[] newArray(int size) {
            return new MDetailTip[size];
        }
    };
}
