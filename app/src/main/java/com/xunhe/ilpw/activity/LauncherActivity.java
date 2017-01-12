package com.xunhe.ilpw.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.utils.SPHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        /*HashMap<String , HashMap<String , ArrayList<String>>> data = new HashMap<String , HashMap<String , ArrayList<String>>>();
        ArrayList<String> list1 = new ArrayList<String>();
        list1.add("101");
        list1.add("102");
        list1.add("103");
        ArrayList<String> list2 = new ArrayList<String>();
        list1.add("201");
        list1.add("202");
        list1.add("203");
        HashMap<String , ArrayList<String>> map1 = new HashMap<String, ArrayList<String>>();
        map1.put("1单元",list1);
        map1.put("2单元" , list2);
        data.put("1" , map1);
        String ss = JSONObject.toJSONString(data);
        Log.v("out" , ss);*/

        /*judge start login/guide/main*/
        /**/
        Intent intent = new Intent();
        Boolean isFirstOpen = SPHelper.getBooleanData(this,"isFirstOpen");
        if(!isFirstOpen){
            intent.setClass(LauncherActivity.this,GuideActivity.class);
        }else {
            intent.setClass(LauncherActivity.this, WelcomActivity.class);
        }
        startActivity(intent);
        finish();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
