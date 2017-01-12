package com.xunhe.ilpw.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.xunhe.ilpw.Interface.PostTips;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.model.MKey;
import com.xunhe.ilpw.model.MKeyList;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


public class CheckNewApplyService extends Service {

    private WlCallback callback , newTipCallback;
    private Context context;
    private JSONObject prm;
    private CheckNewApplyBinder checkNewApplyBinder;
    private PostTips postTips;

    public CheckNewApplyService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
        checkNewApplyBinder = new CheckNewApplyBinder();
        getData();
    }

    public void setPostTips(PostTips postTips){
        this.postTips = postTips;
    }

    public void getData(){
        String token = SPHelper.getStringData(this,"user_token");

        /*获取是否有新申请*/
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    stopSelf();
                }

                @Override
                public void onResponse(String response, int id) {
                    if (response != null) {
                        OperateHelper.Log("当前有新更新");
                        postTips.isHaveNewApply(true);
                    }

                }
            };
        }

        if(!"".equalsIgnoreCase(token)) {
            prm = new JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Boolean isConnected = OperateHelper.isNetworkConnected(context);
                    if(isConnected) {
                        HttpHelper.request("api/apply/new", prm.toString(), callback);
                    }else Toast.makeText(context,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
                }
            } , 0 , 60*1000);

        }

        /*获取是否有新公告*/
        if(newTipCallback == null){
            newTipCallback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    stopSelf();
                }

                @Override
                public void onResponse(String response, int id) {
                    if (response != null) {
                        OperateHelper.Log("当前有新公告");
                        if(response != null) {
                            int num = Integer.parseInt(response.split(":")[1]);
                            postTips.isHaveNewTips(num);
                        }
                    }

                }
            };
        }

    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            OkHttpUtils.getInstance().cancelTag("apiapplynew");
        }catch (Exception e){}
        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return checkNewApplyBinder;
    }

    public class CheckNewApplyBinder extends Binder {
        public CheckNewApplyService getService(){
            return CheckNewApplyService.this;
        }
    }

}
