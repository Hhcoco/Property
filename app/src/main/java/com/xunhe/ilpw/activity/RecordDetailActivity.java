package com.xunhe.ilpw.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityRecordDetailBinding;
import com.xunhe.ilpw.model.MDevice;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;

import okhttp3.Call;
import okhttp3.Request;

public class RecordDetailActivity extends BaseActivity {

    private ActivityRecordDetailBinding activityRecordDetailBinding;
    private LoadingDialog loadingDialog;
    private WlCallback callback;
    private String token , applicationId , time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityRecordDetailBinding = setView(R.layout.activity_record_detail , false);
            applicationId = getIntent().getStringExtra("applicationId");
            time =  getIntent().getStringExtra("time");
        }catch (Exception e){}
        if(activityRecordDetailBinding != null) toDo();

    }

    @Override
    protected void toDo() {
        loadingDialog = new LoadingDialog();
        activityRecordDetailBinding.recorddetailLlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    /*get detail record data*/
    public void getData(){
        if(callback == null){
            callback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    loadingDialog.show(getSupportFragmentManager() , "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        OperateHelper.Log("申请详情结果："+response);
                        MRecordDetail mRecordDetail = JSONObject.parseObject(response , MRecordDetail.class);
                        updateView(mRecordDetail);
                    }
                }
            };
        }
        if(token == null){
            token = SPHelper.getStringData(This(),"user_token");
        }
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("applicationId" , applicationId);
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpHelper.request("api/application/info", prm.toString(), callback);
        } else { Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();}
    }

    /*update view*/
    public void updateView(MRecordDetail mRecordDetail){
        if(mRecordDetail == null) return;
        activityRecordDetailBinding.recorddetailTvName.setText(mRecordDetail.username);
        activityRecordDetailBinding.recorddetailTvPhone.setText(mRecordDetail.phone);
        activityRecordDetailBinding.recorddetailTvCardno.setText(mRecordDetail.id_card);
        activityRecordDetailBinding.recorddetailTvTime.setText(time);
        StringBuilder stringBuilder = new StringBuilder();
        if(mRecordDetail.guard.size()>0){
            for(MDevice mDevice : mRecordDetail.guard){
                stringBuilder.append(mDevice.name);
                if(mDevice == mRecordDetail.guard.get(mRecordDetail.guard.size()-1))
                stringBuilder.append("\n");
            }
        }
        activityRecordDetailBinding.recorddetailDevice.setText(stringBuilder);
        activityRecordDetailBinding.recorddetailTvDescri.setText(mRecordDetail.description);
        activityRecordDetailBinding.recorddetailTvFedback.setText(mRecordDetail.operator_description);
    }

}
