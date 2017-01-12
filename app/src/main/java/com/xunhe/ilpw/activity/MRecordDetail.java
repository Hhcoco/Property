package com.xunhe.ilpw.activity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.xunhe.ilpw.model.MDevice;

import java.util.List;

/**
 * Created by wangliang on 2016/8/5.
 */
public class MRecordDetail implements Parcelable {

    /**/
    @JSONField(name = "operator_headpic")
    public String operator_headpic;
    /**/
    @JSONField(name = "description")
    public String description;
    /**/
    @JSONField(name = "user_headpic")
    public String user_headpic;
    /**/
    @JSONField(name = "username")
    public String username;
    /**/
    @JSONField(name = "property_user_id")
    public String property_user_id;
    /**/
    @JSONField(name = "phone")
    public String phone;
    /**/
    @JSONField(name = "state")
    public String state;
    /**/
    @JSONField(name = "operator_description")
    public String operator_description;
    /**/
    @JSONField(name = "id_card")
    public String id_card;
    /**/
    @JSONField(name = "operator_username")
    public String operator_username;
    /**/
    @JSONField(name = "guard")
    public List<MDevice> guard ;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.operator_headpic);
        dest.writeString(this.description);
        dest.writeString(this.user_headpic);
        dest.writeString(this.username);
        dest.writeString(this.property_user_id);
        dest.writeString(this.phone);
        dest.writeString(this.state);
        dest.writeString(this.operator_description);
        dest.writeString(this.id_card);
        dest.writeString(this.operator_username);
        dest.writeTypedList(this.guard);
    }

    public MRecordDetail() {
    }

    protected MRecordDetail(Parcel in) {
        this.operator_headpic = in.readString();
        this.description = in.readString();
        this.user_headpic = in.readString();
        this.username = in.readString();
        this.property_user_id = in.readString();
        this.phone = in.readString();
        this.state = in.readString();
        this.operator_description = in.readString();
        this.id_card = in.readString();
        this.operator_username = in.readString();
        this.guard = in.createTypedArrayList(MDevice.CREATOR);
    }

    public static final Creator<MRecordDetail> CREATOR = new Creator<MRecordDetail>() {
        @Override
        public MRecordDetail createFromParcel(Parcel source) {
            return new MRecordDetail(source);
        }

        @Override
        public MRecordDetail[] newArray(int size) {
            return new MRecordDetail[size];
        }
    };
}
