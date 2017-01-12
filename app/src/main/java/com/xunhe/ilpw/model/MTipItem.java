package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/26.
 */
public class MTipItem implements Parcelable {

    /**/
    @JSONField(name = "updated_at")
    public String updated_at;
    /**/
    @JSONField(name = "title")
    public String title;
    /**/
    @JSONField(name = "id")
    public String id;
    /**/
    @JSONField(name = "is_delete")
    public String is_delete;
    /**/
    @JSONField(name = "msg_id")
    public String msg_id;
    /**/
    @JSONField(name = "accepter")
    public String accepter;
    /**/
    @JSONField(name = "created_at")
    public String created_at;
    /**/
    @JSONField(name = "state")
    public String state;
    /**/
    @JSONField(name = "content")
    public String content;
    /**/
    @JSONField(name = "sender")
    public String sender;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.updated_at);
        dest.writeString(this.title);
        dest.writeString(this.id);
        dest.writeString(this.is_delete);
        dest.writeString(this.msg_id);
        dest.writeString(this.accepter);
        dest.writeString(this.created_at);
        dest.writeString(this.state);
        dest.writeString(this.content);
        dest.writeString(this.sender);
    }

    public MTipItem() {
    }

    protected MTipItem(Parcel in) {
        this.updated_at = in.readString();
        this.title = in.readString();
        this.id = in.readString();
        this.is_delete = in.readString();
        this.msg_id = in.readString();
        this.accepter = in.readString();
        this.created_at = in.readString();
        this.state = in.readString();
        this.content = in.readString();
        this.sender = in.readString();
    }

    public static final Creator<MTipItem> CREATOR = new Creator<MTipItem>() {
        @Override
        public MTipItem createFromParcel(Parcel source) {
            return new MTipItem(source);
        }

        @Override
        public MTipItem[] newArray(int size) {
            return new MTipItem[size];
        }
    };
}
