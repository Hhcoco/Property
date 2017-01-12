package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/26.
 */
public class MTipListItem implements Parcelable {

    /**/
    @JSONField(name="digest")
    public String digest;
    /**/
    @JSONField(name="title")
    public String title;
    /**/
    @JSONField(name="id")
    public String id;
    /**/
    @JSONField(name="created_at")
    public String created_at;
    /**/
    @JSONField(name="author_name")
    public String author_name;
    /**/
    @JSONField(name="state")
    public String state;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.digest);
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeString(this.created_at);
        dest.writeString(this.author_name);
    }

    public MTipListItem() {
    }

    protected MTipListItem(Parcel in) {
        this.digest = in.readString();
        this.title = in.readString();
        this.id = in.readString();
        this.created_at = in.readString();
        this.author_name = in.readString();
    }

    public static final Creator<MTipListItem> CREATOR = new Creator<MTipListItem>() {
        @Override
        public MTipListItem createFromParcel(Parcel source) {
            return new MTipListItem(source);
        }

        @Override
        public MTipListItem[] newArray(int size) {
            return new MTipListItem[size];
        }
    };
}
