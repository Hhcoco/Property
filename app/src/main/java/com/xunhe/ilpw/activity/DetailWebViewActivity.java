package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityDetailWebViewBinding;
import com.xunhe.ilpw.model.MDetailTip;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MTipListItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;

public class DetailWebViewActivity extends BaseActivity implements View.OnClickListener{

    private ActivityDetailWebViewBinding activityDetailWebViewBinding;
    private LoadingDialog loadingDialog;
    private WlCallback updateStateCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityDetailWebViewBinding = setView(R.layout.activity_detail_web_view , false);
        }catch (Exception e){}
        if(activityDetailWebViewBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activityDetailWebViewBinding.detailwebviewLlLeft.setOnClickListener(this);
        loadingDialog = new LoadingDialog();
        getDetailData();
        flag();
    }

    /*标记已读*/
    public void flag(){
        if(updateStateCallback == null){
            updateStateCallback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                }

                @Override
                public void onResponse(String response, int id) {
                    if(response != null){
                        EventBus.getDefault().post(new MEventBusMsg(Config.NOTICE_CHANGE));
                    }
                }
            };
        }
        String token = SPHelper.getStringData(this,"user_token");
        String id = getIntent().getStringExtra("id");
        if(!"".equalsIgnoreCase(token) && id != null) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("id", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/bulletin/read", prm.toString(), updateStateCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*获取公告详情*/
    public void getDetailData(){
        WlCallback callback = new WlCallback() {
            @Override
            public void onBefore(Request request, int id) {
                loadingDialog.show(getSupportFragmentManager() , "detailtip");
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                loadingDialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                if(response != null) {
                    MDetailTip mDetailTip = com.alibaba.fastjson.JSONObject.parseObject(response, MDetailTip.class);
                    updateView(mDetailTip);
                    loadingDialog.dismiss();
                }
            }
        };
        String token = SPHelper.getStringData(this,"user_token");
        String id = getIntent().getStringExtra("id");
        if(!"".equalsIgnoreCase(token) && id != null) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("id", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/bulletin/info", prm.toString(), callback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*更新view*/
    public void updateView(MDetailTip mTipListItem){
        activityDetailWebViewBinding.detailwebviewTvTitle.setText(mTipListItem.title);
        StringBuilder stringBuilder =  new StringBuilder();
        stringBuilder.append("时间 ");
        stringBuilder.append(OperateHelper.formatData("yy-MM-dd",mTipListItem.created_at));
        stringBuilder.append("   来源 ");
        stringBuilder.append(mTipListItem.author_name);
        activityDetailWebViewBinding.detailwebviewTvIntroduction.setText(stringBuilder);
        activityDetailWebViewBinding.detailwebviewTvBody.setText(Html.fromHtml(mTipListItem.content));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.detailwebview_ll_left:
                finish();
                break;
        }
    }
}
