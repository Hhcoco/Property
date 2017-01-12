package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/19.
 */
public class MUserInfo implements Parcelable {

    @JSONField(name="updated_at")
    private String updated_at;
    @JSONField(name="username")
    private String username;
    @JSONField(name="id")
    private String id;
    @JSONField(name="is_delete")
    private String is_delete;
    @JSONField(name="depart_code")
    private String depart_code;
    @JSONField(name="parent_id")
    private String parent_id;
    @JSONField(name="created_at")
    private String created_at;
    @JSONField(name="id_card")
    private String id_card;
    @JSONField(name="user_id")
    private String user_id;

    /**/
    @JSONField(name = "head_picture")
    private String head_picture;
    /**/
    @JSONField(name = "sex")
    private String sex;
    /**/
    @JSONField(name = "national")
    private String national;

    public String getHead_picture() {
        return head_picture;
    }

    public void setHead_picture(String head_picture) {
        this.head_picture = head_picture;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getNational() {
        return national;
    }

    public void setNational(String national) {
        this.national = national;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete;
    }

    public String getDepart_code() {
        return depart_code;
    }

    public void setDepart_code(String depart_code) {
        this.depart_code = depart_code;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.updated_at);
        dest.writeString(this.username);
        dest.writeString(this.id);
        dest.writeString(this.is_delete);
        dest.writeString(this.depart_code);
        dest.writeString(this.parent_id);
        dest.writeString(this.created_at);
        dest.writeString(this.id_card);
        dest.writeString(this.user_id);
        dest.writeString(this.head_picture);
        dest.writeString(this.sex);
        dest.writeString(this.national);
    }

    public MUserInfo() {
    }

    protected MUserInfo(Parcel in) {
        this.updated_at = in.readString();
        this.username = in.readString();
        this.id = in.readString();
        this.is_delete = in.readString();
        this.depart_code = in.readString();
        this.parent_id = in.readString();
        this.created_at = in.readString();
        this.id_card = in.readString();
        this.user_id = in.readString();
        this.head_picture = in.readString();
        this.sex = in.readString();
        this.national = in.readString();
    }

    public static final Creator<MUserInfo> CREATOR = new Creator<MUserInfo>() {
        @Override
        public MUserInfo createFromParcel(Parcel source) {
            return new MUserInfo(source);
        }

        @Override
        public MUserInfo[] newArray(int size) {
            return new MUserInfo[size];
        }
    };
}
