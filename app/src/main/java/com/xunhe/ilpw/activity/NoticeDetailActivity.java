package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityNoticeDetailBinding;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;

import okhttp3.Call;

public class NoticeDetailActivity extends BaseActivity {

    private ActivityNoticeDetailBinding activityNoticeDetailBinding;
    private WlCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityNoticeDetailBinding = setView(R.layout.activity_notice_detail , false);
        }catch (Exception e){}
        if(activityNoticeDetailBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {
        getNoticeDetailData();
    }

    /*获取网络数据*/
    public void getNoticeDetailData(){

        callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                Log.v("out" , "公告详情结果:"+response);
            }
        };
        if(OperateHelper.isNetworkConnected(This())){
            String token = SPHelper.getStringData(This(),"user_token");
            if(!"".equalsIgnoreCase(token)) {
                org.json.JSONObject prm = new org.json.JSONObject();
                try {
                    prm.put("msgId" , "1");
                    prm.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpHelper.request("api/msg", prm.toString(), callback);
            }
        }else { Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();}
    }
}
