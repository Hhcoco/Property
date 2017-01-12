package com.xunhe.ilpw.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.utils.SPHelper;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.https.HttpsUtils;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;

public class UpdateService extends Service {

    private NotificationManager notificationManager;
    private Notification notification;
    private final int NOTIFY_ID = 0x000 , FLAG_INSTALL = 0x001 ,FLAG_DOWNLOAD = 0x002;
    private float preProgress = 0;
    private RemoteViews remoteViews;
    private Context context;

    public UpdateService() {
    }

    @Override
    public void onCreate(){
        context = this;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent , int flag , int startId){
        /*开始下载*/
        if(FLAG_INSTALL == intent.getIntExtra("flag",0x400))
            install();
        else if(FLAG_DOWNLOAD == intent.getIntExtra("flag",0x002))
        {
            String url = SPHelper.getStringData(context , "apkurl" );
            downloadTask(url);
        }

        return flag;
    }

    public void downloadTask(String url){
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/xunhe");
        if(!file.exists())
            file.mkdirs();
            try {
                if(!file.createNewFile()){
                    OkHttpUtils
                            .get()
                            .url(url)
                            .build()
                            .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath()+"/xunhe/" , "xunhe.apk") {

                                @Override
                                public void onBefore(Request request, int id) {
                                    if(notificationManager == null)
                                        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(UpdateService.this , 0 ,new Intent(UpdateService.this , Context.class), 0);
                                    notification = new Notification(android.R.drawable.stat_sys_download, "正在下新版摇摇开门更新包", System.currentTimeMillis());
                                    remoteViews = new RemoteViews(getPackageName() , R.layout.item_push_view);
                                    remoteViews.setTextViewText(R.id.notificationTitle, "正在下载新版摇摇开门");
                                    remoteViews.setTextViewText(R.id.notificationPercent, "0%");
                                    remoteViews.setProgressBar(R.id.notificationProgress, 100, 0, false);
                                    notification.contentView = remoteViews;
                                    notification.contentIntent = pendingIntent;
                                    notificationManager.notify( NOTIFY_ID, notification);
                                }
                                @Override
                                public void inProgress(float progress, long total , int id)
                                {
                                    if(progress-preProgress>=0.01){
                                        remoteViews.setProgressBar(R.id.notificationProgress, 100, (int) (100 * progress), false);
                                        remoteViews.setTextViewText(R.id.notificationPercent, (int) (100 * progress) + "%");
                                        notificationManager.notify(NOTIFY_ID, notification);
                                        preProgress = progress;
                                    }

                                }
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                    stopSelf();
                                }

                                @Override
                                public void onResponse(File response, int id) {
                                    remoteViews.setTextViewText(R.id.notificationTitle, "下载完成，点击开始安装");
                                    remoteViews.setProgressBar(R.id.notificationProgress, 100, 100, false);
                                    remoteViews.setTextViewText(R.id.notificationPercent, 100 + "%");
                                    Intent installIntent = new Intent(UpdateService.this, UpdateService.class);
                                    installIntent.putExtra("flag" , FLAG_INSTALL);
                                    PendingIntent pt = PendingIntent.getService(UpdateService.this, 0, installIntent, 0);
                                    notification.contentIntent = pt;
                                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                                    notificationManager.notify(NOTIFY_ID, notification);
                                    /*开始安装*/
                                    install();
                                }
                            });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    /*安装*/
    public void install(){
        File apkFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/xunhe/" , "xunhe.apk");
        if(apkFile.exists()&&apkFile.isFile()){
            long size = apkFile.length();
            Log.v("out","文件大小为："+size+"KB");
        }
        if(apkFile != null && apkFile.length() > 0) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            UpdateService.this.startActivity(intent);
            stopSelf();
        }else
            stopSelf();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopSelf();
    }

}
