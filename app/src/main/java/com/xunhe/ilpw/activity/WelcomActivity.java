package com.xunhe.ilpw.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.model.MUploadLog;
import com.xunhe.ilpw.service.GetKeyService;
import com.xunhe.ilpw.service.GetUserInfoService;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class WelcomActivity extends BaseActivity {

    private Handler mHandler;
    private String token;
    private WlCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        /**/
        toDo();
    }

    /*开始上传开门记录*/
    public void uploadRecord(final String data){
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    if(response != null) {
                        /*清除记录*/
                        SPHelper.setStringData(This() , "recordhistory" , "");
                    }
                }
            };
        }
        if(token == null || token.equalsIgnoreCase(""))
            token = SPHelper.getStringData(getApplicationContext() , "user_token");
        if(!"".equalsIgnoreCase(token)) {

            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("type", "2");
                prm.put("data", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(getApplicationContext());
            if(isConnected) {
                HttpHelper.request("api/access", prm.toString(), callback);
            }
        }

    }

    @Override
    protected void toDo() {

        final Intent intent = new Intent();
        Boolean isLogin = SPHelper.getBooleanData(this,"isLogin");
        if(isLogin){
            String userType = SPHelper.getStringData(This() , "user_type");
            intent.putExtra("user_type" , userType);
            intent.setClass(WelcomActivity.this,MainActivity.class);
        }else intent.setClass(WelcomActivity.this, LoginActivity.class);

        /*上传上传未成功上传的记录*/
        try{
            String recordHistory = SPHelper.getStringData(This() , "recordhistory");
            if(!recordHistory.equalsIgnoreCase("")){
                uploadRecord(recordHistory);
            }
        }catch (Exception e){}

        /*开启Service从服务器获取钥匙，并与本地比较，如果不一致则更新钥匙*/
        startService(new Intent(WelcomActivity.this, GetKeyService.class));
        /*获取用户信息*/
        startService(new Intent(WelcomActivity.this , GetUserInfoService.class));

        initSet();

        /*根据用户配置决定是否默认打开蓝牙*/
        openBlueTooth();

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==1){
                    startActivity(intent);

                    finish();
                }
            }
        };

        mHandler.sendEmptyMessageDelayed(1,2000);

    }

    /*根据配置做一些基本操作*/
    public void initSet(){
        //MiaodouKeyAgent.setNeedSensor(true);
    }

    /*开启蓝牙*/
    public void openBlueTooth(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isSupportBT = bluetoothAdapter == null?false:true;
        if(isSupportBT){
            if(!bluetoothAdapter.isEnabled()){
                ((BaseApplication)getApplication()).isPreOpen = false;
                bluetoothAdapter.enable();
            }else ((BaseApplication)getApplication()).isPreOpen = true;
        }else {
            Log.v("out","不支持蓝牙！");
        }
        /*if(SPHelper.getBooleanData(This(),"isCanOpenBlueTooth")){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean isSupportBT = bluetoothAdapter == null?false:true;
            if(isSupportBT){
                if(!bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.enable();
                }
            }else {
                Log.v("out","不支持蓝牙！");
            }
        }*/
    }
}
