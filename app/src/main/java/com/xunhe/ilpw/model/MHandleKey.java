package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/7/21.
 */
public class MHandleKey implements Parcelable {

    /**/
    @JSONField(name = "items")
    public List<MHandleKeyItem> items ;
    /**/
    @JSONField(name = "total_pages")
    public int total_pages;
    /**/
    @JSONField(name = "total_items")
    public int total_items;
    /**/
    @JSONField(name = "last")
    public int last;
    /**/
    @JSONField(name = "current")
    public int current;
    /**/
    @JSONField(name = "before")
    public int before;
    /**/
    @JSONField(name = "next")
    public int next;
    /**/
    @JSONField(name = "first")
    public int first;
    /**/
    @JSONField(name = "limit")
    public int limit;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.items);
        dest.writeInt(this.total_pages);
        dest.writeInt(this.total_items);
        dest.writeInt(this.last);
        dest.writeInt(this.current);
        dest.writeInt(this.before);
        dest.writeInt(this.next);
        dest.writeInt(this.first);
        dest.writeInt(this.limit);
    }

    public MHandleKey() {
    }

    protected MHandleKey(Parcel in) {
        this.items = in.createTypedArrayList(MHandleKeyItem.CREATOR);
        this.total_pages = in.readInt();
        this.total_items = in.readInt();
        this.last = in.readInt();
        this.current = in.readInt();
        this.before = in.readInt();
        this.next = in.readInt();
        this.first = in.readInt();
        this.limit = in.readInt();
    }

    public static final Creator<MHandleKey> CREATOR = new Creator<MHandleKey>() {
        @Override
        public MHandleKey createFromParcel(Parcel source) {
            return new MHandleKey(source);
        }

        @Override
        public MHandleKey[] newArray(int size) {
            return new MHandleKey[size];
        }
    };
}
