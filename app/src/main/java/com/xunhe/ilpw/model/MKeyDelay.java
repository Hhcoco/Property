package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/7/21.
 */
public class MKeyDelay implements Parcelable {

    /**/
    @JSONField(name = "items")
    public List<MKeyDelayItem> items ;
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

    public MKeyDelay() {
    }

    protected MKeyDelay(Parcel in) {
        this.items = in.createTypedArrayList(MKeyDelayItem.CREATOR);
        this.total_pages = in.readInt();
        this.total_items = in.readInt();
        this.last = in.readInt();
        this.current = in.readInt();
        this.before = in.readInt();
        this.next = in.readInt();
        this.first = in.readInt();
        this.limit = in.readInt();
    }

    public static final Creator<MKeyDelay> CREATOR = new Creator<MKeyDelay>() {
        @Override
        public MKeyDelay createFromParcel(Parcel source) {
            return new MKeyDelay(source);
        }

        @Override
        public MKeyDelay[] newArray(int size) {
            return new MKeyDelay[size];
        }
    };
}
