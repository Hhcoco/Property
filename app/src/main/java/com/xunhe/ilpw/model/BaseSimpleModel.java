package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by wangliang on 2016/7/15.
 */
public class BaseSimpleModel implements Parcelable {

    /*错误代码*/
    @JSONField(name="errorNo")
    public int errorNo;
    /*返回的数据*/
    @JSONField(name="data")
    public String data;
    /*错误信息*/
    @JSONField(name="errorMsg")
    public String errorMsg;
    /*耗时*/
    @JSONField(name="responseTime")
    public String responseTime;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errorNo);
        dest.writeString(this.data);
        dest.writeString(this.errorMsg);
        dest.writeString(this.responseTime);
    }

    public BaseSimpleModel() {
    }

    protected BaseSimpleModel(Parcel in) {
        this.errorNo = in.readInt();
        this.data = in.readString();
        this.errorMsg = in.readString();
        this.responseTime = in.readString();
    }

    public static final Creator<BaseSimpleModel> CREATOR = new Creator<BaseSimpleModel>() {
        @Override
        public BaseSimpleModel createFromParcel(Parcel source) {
            return new BaseSimpleModel(source);
        }

        @Override
        public BaseSimpleModel[] newArray(int size) {
            return new BaseSimpleModel[size];
        }
    };
}
