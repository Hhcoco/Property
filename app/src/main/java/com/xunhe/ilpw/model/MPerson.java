package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/8/1.
 */
public class MPerson implements Parcelable {

    /**/
    @JSONField(name = "id")
    public String id;
    /**/
    @JSONField(name = "user_id")
    public String user_id;
    /**/
    @JSONField(name = "target_id")
    public String target_id;
    /**/
    @JSONField(name = "type")
    public String type;
    /**/
    @JSONField(name = "is_delete")
    public String is_delete;
    /**/
    @JSONField(name = "created_at")
    public String created_at;
    /**/
    @JSONField(name = "updated_at")
    public String updated_at;
    /**/
    @JSONField(name = "username")
    public String username;
    /**/
    @JSONField(name = "phone")
    public String phone;
    /**/
    @JSONField(name = "head_picture")
    public String head_picture;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.user_id);
        dest.writeString(this.target_id);
        dest.writeString(this.type);
        dest.writeString(this.is_delete);
        dest.writeString(this.created_at);
        dest.writeString(this.updated_at);
        dest.writeString(this.username);
        dest.writeString(this.phone);
        dest.writeString(this.head_picture);
    }

    public MPerson() {
    }

    protected MPerson(Parcel in) {
        this.id = in.readString();
        this.user_id = in.readString();
        this.target_id = in.readString();
        this.type = in.readString();
        this.is_delete = in.readString();
        this.created_at = in.readString();
        this.updated_at = in.readString();
        this.username = in.readString();
        this.phone = in.readString();
        this.head_picture = in.readString();
    }

    public static final Creator<MPerson> CREATOR = new Creator<MPerson>() {
        @Override
        public MPerson createFromParcel(Parcel source) {
            return new MPerson(source);
        }

        @Override
        public MPerson[] newArray(int size) {
            return new MPerson[size];
        }
    };
}
