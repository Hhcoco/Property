package com.xunhe.ilpw.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Created by wangliang on 2016/7/26.
 */
public class MTip implements Parcelable {

    /**/
    @JSONField(name="items")
    public List<MTipItem> items ;
    /**/
    @JSONField(name="total_pages")
    public int total_pages;
    /**/
    @JSONField(name="total_items")
    public int total_items;
    /**/
    @JSONField(name="last")
    public int last;
    /**/
    @JSONField(name="current")
    public int current;
    /**/
    @JSONField(name="before")
    public int before;
    /**/
    @JSONField(name="next")
    public int next;
    /**/
    @JSONField(name="first")
    public int first;
    /**/
    @JSONField(name="limit")
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

    public MTip() {
    }

    protected MTip(Parcel in) {
        this.items = in.createTypedArrayList(MTipItem.CREATOR);
        this.total_pages = in.readInt();
        this.total_items = in.readInt();
        this.last = in.readInt();
        this.current = in.readInt();
        this.before = in.readInt();
        this.next = in.readInt();
        this.first = in.readInt();
        this.limit = in.readInt();
    }

    public static final Creator<MTip> CREATOR = new Creator<MTip>() {
        @Override
        public MTip createFromParcel(Parcel source) {
            return new MTip(source);
        }

        @Override
        public MTip[] newArray(int size) {
            return new MTip[size];
        }
    };
}
