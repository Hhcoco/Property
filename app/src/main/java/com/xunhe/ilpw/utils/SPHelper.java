package com.xunhe.ilpw.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.BoolRes;
import android.support.v4.content.ContextCompat;

import com.xunhe.ilpw.model.MKey;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by wangliang on 2016/7/13.
 */
public class SPHelper {

    public static void setStringData(Context context, String key, String value){
        SharedPreferences  sp = context.getSharedPreferences("cdxunhe",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static String getStringData(Context context,String key){
        SharedPreferences  sp = context.getSharedPreferences("cdxunhe",Activity.MODE_PRIVATE);
        return sp.getString(key,"");
    }
    public static void setBooleanData(Context context,String key,boolean value){
        SharedPreferences  sp = context.getSharedPreferences("cdxunhe",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static Boolean getBooleanData(Context context, String key){
        SharedPreferences  sp = context.getSharedPreferences("cdxunhe",Activity.MODE_PRIVATE);
        return sp.getBoolean(key,false);
    }
    public static void clearData(Context context){
        SharedPreferences  sp = context.getSharedPreferences("cdxunhe",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }
}
