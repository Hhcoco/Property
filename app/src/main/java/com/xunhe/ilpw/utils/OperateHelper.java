package com.xunhe.ilpw.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.xunhe.ilpw.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by wangliang on 2016/7/13.
 */
public class OperateHelper {

    /*播放本地MP3*/
    public static void playMp3(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context , R.raw.doorbell);
        if(mediaPlayer.isPlaying()){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

    }

    /*Bitmap转string*/
    public static String bitmpaToString(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG , options , outputStream);
        /*大于120k继续压缩*/
        while ((outputStream.toByteArray().length/1024)>200){
            options -= 5;
            outputStream.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG , options , outputStream);
        }
        if(outputStream != null && outputStream.size()>0){
            try {
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = outputStream.toByteArray();
            String bitmapString = Base64.encodeToString(bytes, Base64.DEFAULT);
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmapString;
        }
        return null;
    }

    /*检测是否有SDCard*/
    public static boolean isHaveSDCard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED))
            return  true;
        return false;
    }

    /*获取当前版本号*/
    public static int getVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName , 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /*获取手机系统等信息*/
    public static String getPhoneData(Context context){
        String data = Build.MODEL+"/"+Build.VERSION.SDK;
        return null;
    }

    /*检查手机号规则*/
    public static int checkRule(Context context , String phone ){

        String REGEX_MOBILE = "^1[3|4|5|7|8][0-9]\\d{8}$";
        Pattern pattern = Pattern.compile(REGEX_MOBILE);

        if (TextUtils.isEmpty(phone)) {//手机号码为空
            Toast.makeText(context , "手机号码不能为空!", Toast.LENGTH_SHORT).show();
            return 0x001;
        } else if (!pattern.matcher(phone).find()) {//手机号码无效
            Toast.makeText(context , "手机号码无效!", Toast.LENGTH_SHORT).show();
            return 0x002;
        }

        return 0x000;
    }

    /*输出日志，正式版发布后修改为false不输出日志*/
    public static void Log(String log){
        String tag  = "out";
        boolean isLog = false;
        if(isLog) {
            Log.v(tag, log);
        }
    }

    /*获取网络状态，第一次进入时使用，以后网络状态变化就通过Receiver来接收了*/
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isConnectedOrConnecting();
        } else return false;
    }

    /*时间戳转日期*/
    public static String formatData(String dataFormat, String time) {
        try {
            long timeStamp = Long.parseLong(time);
            if (timeStamp == 0) {
                return "";
            }
            timeStamp = timeStamp * 1000;
            String result = "";
            SimpleDateFormat format = new SimpleDateFormat(dataFormat);
            result = format.format(new Date(timeStamp));
            return result;
        } catch (Exception e) {
        }
        return "";
    }

}
