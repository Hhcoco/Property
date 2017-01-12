package com.xunhe.ilpw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MUserInfo;
import com.xunhe.ilpw.model.MVersion;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class GetUserInfoService extends Service {

    private WlCallback callback , getVersionCallback;
    private Context context;
    private String token;

    public GetUserInfoService() {
    }

    @Override
    public void onCreate(){
        context = this;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent , int flag , int id){
        super.onStartCommand(intent , flag , id);

        getUserInfo();
        getVersionInfo();

        return START_STICKY;
    }

    /*获取版本信息*/
    public void getVersionInfo(){
        /*获取钥匙列表*/
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(getVersionCallback == null) {
            getVersionCallback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.v("out", "获取版本信息失败!");
                }

                @Override
                public void onResponse(String response, int id) {

                    if (response != null) {
                        MVersion version = com.alibaba.fastjson.JSONObject.parseObject(response , MVersion.class);
                        SPHelper.setStringData(context , "user_serviceversion"  , version.version_no);
                        SPHelper.setStringData(context , "apkurl" , version.version_url);
                        SPHelper.setStringData(context , "user_updatetip" , version.version_info);
                    } else OperateHelper.Log("获取版本信息失败null");

                }
            };
        }
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            Boolean isConnected = OperateHelper.isNetworkConnected(context);
            if(isConnected) {
                HttpHelper.request("api/app", prm.toString(), getVersionCallback);
            }else Toast.makeText(context,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }


    }

    /*获取用户信息*/
    public void getUserInfo(){
        if(token == null)
        token = SPHelper.getStringData(this,"user_token");
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.v("out", "获取用户信息失败!");
                }

                @Override
                public void onResponse(String response, int id) {

                    if (response != null) {
                        SPHelper.setStringData(context, "user_info", response);
                        OperateHelper.Log("获取用户信息成功" + response);
                        EventBus.getDefault().post(new MEventBusMsg(Config.USER_INFO));
                    } else OperateHelper.Log("获取用户信息失败null");

                }
            };
        }
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(context);
            if(isConnected) {
                HttpHelper.request("api/user", prm.toString(), callback);
            }else Toast.makeText(context,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            stopSelf();
        }catch (Exception e){
            Log.v("out","服务已经关闭");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
