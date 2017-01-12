package com.xunhe.ilpw.utils;

import com.xunhe.ilpw.config.Config;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

/**
 * Created by wangliang on 2016/7/14.
 */

public  class HttpHelper {

    public static void request(String action, String paramas, Callback callback){
        String tag = action.replace("/","");
        OkHttpUtils
                .postString()
                .tag(tag)
                .url(Config.XH_Base_URL+action)
                .content(paramas)
                .build()
                .execute(callback);
    }
}
