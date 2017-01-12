package com.xunhe.ilpw.utils;


import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.model.BaseSimpleModel;
import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/14.
 */
public abstract class WlCallback extends Callback<String>
{
    private BaseSimpleModel baseSimpleModel;

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException
    {
        String stringresponse = response.body().string();
        OperateHelper.Log("未转换前:"+stringresponse);
        try {
            baseSimpleModel = JSONObject.parseObject(stringresponse,BaseSimpleModel.class);
            String relust = baseSimpleModel.data;
            int errNo = baseSimpleModel.errorNo;
            Log.v("out","转换后结果："+relust);
            if(0 == errNo)
            return relust;
            else {
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BaseApplication.getContext() , baseSimpleModel.errorMsg , Toast.LENGTH_LONG).show();
                    }
                });
            }
        }catch (Exception e){
            Log.v("out","json转model出错!");
        }

        return null;
    }
}