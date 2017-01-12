package com.xunhe.ilpw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MKey;
import com.xunhe.ilpw.model.MKeyList;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;


public class GetKeyService extends Service {

    private WlCallback callback;
    private Context context;

    public GetKeyService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
    }
    @Override
    public int onStartCommand(Intent intent,int flag,int id){
        super.onStartCommand(intent,flag,id);

        /*获取钥匙列表*/
        String token = SPHelper.getStringData(this,"user_token");

        callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.v("out","获取钥匙列表失败");
                stopSelf();
            }

            @Override
            public void onResponse(String response, int id) {
                if(response!=null){
                    try{

                        List<MKey> keyList = com.alibaba.fastjson.JSONObject.parseArray(response,MKey.class);
                        Log.v("out","钥匙列表为："+response);
                        SPHelper.setStringData(context,"keylist",response);
                        /*通知MainActivity钥匙数据已经刷新，无需更新*/
                        ((BaseApplication) getApplication()).keyIsRefresh = true;
                        EventBus.getDefault().post(new MEventBusMsg(Config.KEY_CHANGED));
                        stopSelf();

                    }catch (Exception e){}
                }

            }
        };

        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("page", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(context);
            if(isConnected) {
                HttpHelper.request("api/key", prm.toString(), callback);
            }else Toast.makeText(context,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }


        return START_STICKY;
    }

    /*获取钥匙列表回调*/

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            OkHttpUtils.getInstance().cancelTag("apikey");
        }catch (Exception e){}
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
