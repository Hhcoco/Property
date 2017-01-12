package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;

/**
 * Created by wangliang on 2016/7/15.
 */
public class MKeyList implements Parcelable {

    /*钥匙列表*/
    @JSONField(name = "items")
    public ArrayList<MKey> items;
    /*钥匙列表*/
    @JSONField(name = "first")
    public int first;
    /*钥匙列表*/
    @JSONField(name = "before")
    public int before;
    /*钥匙列表*/
    @JSONField(name = "current")
    public int current;
    /*钥匙列表*/
    @JSONField(name = "last")
    public int last;
    /*钥匙列表*/
    @JSONField(name = "next")
    public int next;
    /*钥匙列表*/
    @JSONField(name = "total_pages")
    public int total_pages;
    /*钥匙列表*/
    @JSONField(name = "total_items")
    public int total_items;
    /*钥匙列表*/
    @JSONField(name = "limit")
    public int limit;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.items);
        dest.writeInt(this.first);
        dest.writeInt(this.before);
        dest.writeInt(this.current);
        dest.writeInt(this.last);
        dest.writeInt(this.next);
        dest.writeInt(this.total_pages);
        dest.writeInt(this.total_items);
        dest.writeInt(this.limit);
    }

    public MKeyList() {
    }

    protected MKeyList(Parcel in) {
        this.items = in.createTypedArrayList(MKey.CREATOR);
        this.first = in.readInt();
        this.before = in.readInt();
        this.current = in.readInt();
        this.last = in.readInt();
        this.next = in.readInt();
        this.total_pages = in.readInt();
        this.total_items = in.readInt();
        this.limit = in.readInt();
    }

    public static final Creator<MKeyList> CREATOR = new Creator<MKeyList>() {
        @Override
        public MKeyList createFromParcel(Parcel source) {
            return new MKeyList(source);
        }

        @Override
        public MKeyList[] newArray(int size) {
            return new MKeyList[size];
        }
    };
}
