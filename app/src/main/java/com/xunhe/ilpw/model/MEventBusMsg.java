package com.xunhe.ilpw.model;

/**
 * Created by wangliang on 2016/8/3.
 */
public class MEventBusMsg {
    private String FlagMsg;
    public MEventBusMsg(String s){
        this.FlagMsg = s;
    }
    public String getMsg(){
        return FlagMsg;
    }
}
