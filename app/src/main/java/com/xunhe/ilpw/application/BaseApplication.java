package com.xunhe.ilpw.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.hzblzx.miaodou.sdk.core.bluetooth.MDActionListener;
import com.hzblzx.miaodou.sdk.core.model.BigSurprise;
import com.hzblzx.miaodou.sdk.core.model.MDVirtualKey;
import com.invs.BtReaderClient;
import com.invs.IClientCallBack;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.CardReadDeviceActivity;
import com.xunhe.ilpw.activity.SettingActivity;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MBLE;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MUploadLog;
import com.xunhe.ilpw.service.GetUserInfoService;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;


/**
 * Created by wangliang on 2016/7/13.
 */
public class BaseApplication extends Application implements MDActionListener{

    /*记录所有已经打开的Activity*/
    public ArrayList<Activity> Activitys;
    /*钥匙数据是否已经刷新*/
    public boolean keyIsRefresh = false;
    private static Context context;
    /*记录弹出的dialog，有点回调是在Applica里面设置的，所以需要在这里面来dismiss()*/
    public List<DialogFragment> dialogFragments = new ArrayList<DialogFragment>();
    /*定义一个全局变量，读卡使用*/
    public BtReaderClient mClient;
    /*isPreOpen:记录在打开app之前是否开启了蓝牙*/
    public boolean isConnect  = false , isRegister = false , isPreOpen;
    public MBLE mble = null;
    /*因为多个界面需要保存设备，将回调设置在application中*/

    public ExecutorService cacheThreadPool = Executors.newCachedThreadPool();

    /*上传开门记录*/
    public WlCallback callback;
    public MUploadLog log = new MUploadLog();

    public String token;

    public LoadingDialog loadingDialog;

    private AlertDialog dialog;

    private String phoneData = "";

    /*记录未上传成功的开门记录，下次上传*/
    public ArrayList<MUploadLog> mUploadLogHistoryArrayList = new ArrayList<MUploadLog>();


    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
        Activitys = new ArrayList<Activity>();
        cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                token = SPHelper.getStringData(getApplicationContext() , "user_token");
                phoneData = OperateHelper.getVersionCode(getApplicationContext())+OperateHelper.getPhoneData(getApplicationContext());
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setView(R.layout.loadingdialog);
        builder.setTitle("你好");
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//指定会全局,可以在后台弹出
        dialog.setCanceledOnTouchOutside(false);

        /*OkHttpUtils初始化*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        /*MiaodouSDK 初始化*/
        MiaodouKeyAgent.init(this);
        MiaodouKeyAgent.setMDActionListener(this);

        /*身份证阅读器*/
        mClient = new BtReaderClient(this);
        mClient.setCallBack(new IClientCallBack() {
            @Override
            public void onBtState(boolean b) {
                if(b){
                    isConnect = true;
                }else {
                    isConnect = false;
                }
            }
        });

        stopService(new Intent(getApplicationContext() , GetUserInfoService.class));

        listenBrocast();
    }

    /*添加广播，监听熄屏*/
    public void listenBrocast(){
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case Intent.ACTION_SCREEN_OFF:
                        MiaodouKeyAgent.setNeedSensor(false);
                        break;
                }
            }
        }, filter);
    }

    public static Context getContext(){
        if(context != null) return context;
        else {
            context = getContext();
            return context;
        }
    }

    public void exit() {
        /*退出所有Activity*/
        for(Activity activity :Activitys){
            if(!activity.isFinishing()){
                if(activity instanceof SettingActivity) continue;
                activity.finish();
            }
        }
        /*退出所有Service*/
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfoList = activityManager.getRunningServices(10);
        if(runningServiceInfoList !=null && runningServiceInfoList.size()>0){
            /*循环比对，退出Service*/
            for(ActivityManager.RunningServiceInfo runningServiceInfo :runningServiceInfoList) {
                String packageName = runningServiceInfo.service.getPackageName();
                if("com.xunhe.ilpw".equalsIgnoreCase(packageName)) {
                    String className = runningServiceInfo.service.getClassName();
                    stopService(new Intent(this, GetUserInfoService.class));
                }
            }
        }else return;
    }

    /*开始上传开门记录*/
    public void uploadRecord(final MUploadLog mUploadLog){
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    OperateHelper.Log("上传日志失败");
                    if(mUploadLogHistoryArrayList == null)
                        mUploadLogHistoryArrayList = new ArrayList<MUploadLog>();
                    mUploadLogHistoryArrayList.add(mUploadLog);
                }

                @Override
                public void onResponse(String response, int id) {
                    if(response != null) {
                        OperateHelper.Log("上传日志成功");
                    }
                }
            };
        }
        if(token == null || token.equalsIgnoreCase(""))
        token = SPHelper.getStringData(getApplicationContext() , "user_token");
        if(!"".equalsIgnoreCase(token)) {

            JSONObject prm = new JSONObject();
            try {
                ArrayList<MUploadLog> mUploadLogArrayList = new ArrayList<MUploadLog>();
                mUploadLogArrayList.add(mUploadLog);
                String data = com.alibaba.fastjson.JSONObject.toJSONString(mUploadLogArrayList);
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

    /*MiaodouSDK接口回调方法*/

    @Override
    public void scaningDevices() {
        EventBus.getDefault().post(new MEventBusMsg(Config.SCAN_DEVICE));
        Toast.makeText(context,"设备扫描中...",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void findAvaliableKey(MDVirtualKey mdVirtualKey) {
    }

    @Override
    public void onComplete(int i, MDVirtualKey mdVirtualKey) {
        EventBus.getDefault().post(new MEventBusMsg(Config.END_SCAN));
        Toast.makeText(context,"开门成功",Toast.LENGTH_SHORT).show();
        /*播放开门成功的MP3*/
        OperateHelper.playMp3(context);
        /*移除开门时的提示dialog*/
        if(dialogFragments!=null&&dialogFragments.size()>0){
            for(DialogFragment fragment:dialogFragments){
                if("opendoor_loading_dialog".equalsIgnoreCase(fragment.getTag())){
                    fragment.dismiss();
                    dialogFragments.remove(fragment);
                }
            }
        }
        Log.v("out","开门成功");
        long time = System.currentTimeMillis()/1000;
        log.setCreated_at(time+"");
        log.setDescription("");
        log.setGuard_code(mdVirtualKey.name);
        log.setWay("1");
        log.setState("1");
        uploadRecord(log);
    }

    @Override
    public void onError(int i, int i1) {
        EventBus.getDefault().post(new MEventBusMsg(Config.END_SCAN));
        Toast.makeText(context,"开门失败",Toast.LENGTH_SHORT).show();
        //OperateHelper.playMp3(context);
        for(DialogFragment fragment:dialogFragments){
            if("opendoor_loading_dialog".equalsIgnoreCase(fragment.getTag())){
                fragment.dismiss();
                dialogFragments.remove(fragment);
            }
        }
        Log.v("out","开门失败1:"+i+"/"+i1);
        log.setCreated_at(System.currentTimeMillis()/1000+"");
        log.setDescription(i1+"/"+phoneData);
        log.setGuard_code("");
        log.setWay("1");
        log.setState("2");
        uploadRecord(log);
    }

    @Override
    public void onError(int i, int i1, MDVirtualKey mdVirtualKey) {
    }

    @Override
    public void onOpendoorGetSurpirsed(List<BigSurprise> list) {

    }

    @Override
    public void onDisconnect() {

    }
    /*MiaodouSDK接口回调方法 End*/
}
