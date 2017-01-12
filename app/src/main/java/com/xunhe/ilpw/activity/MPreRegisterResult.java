package com.xunhe.ilpw.activity;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;
import com.xunhe.ilpw.model.MRoot;

import java.util.ArrayList;

/**
 * Created by wangliang on 2016/8/5.
 */
public class MPreRegisterResult implements Parcelable {

    /**/
    @JSONField(name="depart_code")
    public String depart_code;
    /**/
    @JSONField(name="depart_name")
    public String depart_name;
    /**/
    @JSONField(name="lists")
    public ArrayList<MRoot> lists;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.depart_code);
        dest.writeTypedList(this.lists);
    }

    public MPreRegisterResult() {
    }

    protected MPreRegisterResult(Parcel in) {
        this.depart_code = in.readString();
        this.lists = in.createTypedArrayList(MRoot.CREATOR);
    }

    public static final Creator<MPreRegisterResult> CREATOR = new Creator<MPreRegisterResult>() {
        @Override
        public MPreRegisterResult createFromParcel(Parcel source) {
            return new MPreRegisterResult(source);
        }

        @Override
        public MPreRegisterResult[] newArray(int size) {
            return new MPreRegisterResult[size];
        }
    };
}
