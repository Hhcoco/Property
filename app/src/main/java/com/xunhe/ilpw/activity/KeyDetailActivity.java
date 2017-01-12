package com.xunhe.ilpw.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityKeyDetailBinding;
import com.xunhe.ilpw.fragments.Main_home;
import com.xunhe.ilpw.model.MEventBusMsg;
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

public class KeyDetailActivity extends BaseActivity implements View.OnClickListener{

    private ActivityKeyDetailBinding activityKeyDetailBinding;
    private String keyId , keyName ,guard_code , depart_code , time;
    private LoadingDialog dialog;
    /*钥匙原始名称,点击保存时如果发现修改后的与原始的一致就不调用网络了*/
    private String originalKeyName;
    private WlCallback deleteCallback;
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityKeyDetailBinding = setView(R.layout.activity_key_detail , false);
        }catch (Exception e){}
        if(activityKeyDetailBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activityKeyDetailBinding.keydetailTvShare.setOnClickListener(this);
        activityKeyDetailBinding.keydetailTvApplydelay.setOnClickListener(this);
        activityKeyDetailBinding.keydetailTvLeft.setOnClickListener(this);
        activityKeyDetailBinding.keydetailTopRight.setOnClickListener(this);
        activityKeyDetailBinding.keydetailTvDelete.setOnClickListener(this);
        originalKeyName = activityKeyDetailBinding.keydetailEt.getText().toString();
        activityKeyDetailBinding.keydetailEt.setEnabled(false);

        dialog = new LoadingDialog();

        try {
            keyId = getIntent().getStringExtra("keyId");
            keyName = getIntent().getStringExtra("keyName");
            depart_code = getIntent().getStringExtra("depart_code");
            guard_code = getIntent().getStringExtra("guard_code");
            time = getIntent().getStringExtra("time");
            activityKeyDetailBinding.keydetailEt.setText(keyName);
        }catch (Exception e){}

    }

    /*钥匙延期*/
    public void startDelay(){

        WlCallback callback = new WlCallback() {
            @Override
            public void onBefore(Request request, int id) {
                dialog.show(getSupportFragmentManager() ,"applykeydelay");
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                dialog.dismiss();
                Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                dialog.dismiss();
                if(response != null){
                    Toast.makeText(This() , "延期申请已提交，待审核!" , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(This() , "提交申请失败，请稍后重试!" , Toast.LENGTH_SHORT).show();
                }
            }
        };
        String token = SPHelper.getStringData(this, "user_token");
        if (!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("depart_code", depart_code);
                prm.put("guard_code", guard_code);
                prm.put("type", "2");
                prm.put("description", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if (isConnected) {
                HttpHelper.request("api/application/add", prm.toString(), callback);
            } else Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
        }
    }

    /*请求修改钥匙名称*/
    public void startUpdatekeyName(String newName){
        WlCallback callback = new WlCallback() {
            @Override
            public void onBefore(Request request, int id) {
                dialog.show(getSupportFragmentManager() ,"applykeydelay");
            }
            @Override
            public void onError(Call call, Exception e, int id) {
                dialog.dismiss();
                Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                dialog.dismiss();
                if(response != null){
                    EventBus.getDefault().post(new MEventBusMsg(Config.KEY_CHANGED));
                    Toast.makeText(This() , "修改成功!" , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(This() , "修改失败，请稍后重试!" , Toast.LENGTH_SHORT).show();
                }
            }
        };
        String token = SPHelper.getStringData(this, "user_token");
        if (!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("keyId", keyId);
                prm.put("name", newName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if (isConnected) {
                HttpHelper.request("api/key/update", prm.toString(), callback);
            } else Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
        }
    }
    /*删除钥匙*/
    public void deleteKey(){
        if(deleteCallback == null){
            deleteCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    dialog.show(getSupportFragmentManager() , "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    dialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    dialog.dismiss();
                    if(response != null){
                        EventBus.getDefault().post(new MEventBusMsg(Config.KEY_CHANGED));
                        Toast.makeText(This(), "钥匙删除成功!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            };
        }
        String token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("keyId", keyId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/key/del", prm.toString(), deleteCallback);
            }else Toast.makeText(This() ,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.keydetail_tv_share:
                if(time != null)
                if(System.currentTimeMillis() > Long.parseLong(time)*1000){
                    Toast.makeText(This() , "钥匙已过期,不能分享!" , Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(KeyDetailActivity.this , KeyShareActivity.class);
                if(keyId!=null)
                intent.putExtra("keyId" , keyId);
                startActivity(intent);
                break;
            case R.id.keydetail_tv_applydelay:
                startDelay();
                break;
            case R.id.keydetail_tv_left:
                finish();
                break;
            case R.id.keydetail_top_right:
                if("编 辑".equalsIgnoreCase(activityKeyDetailBinding.keydetailTopRight.getText().toString())){
                    activityKeyDetailBinding.keydetailTopRight.setText("保 存");
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    activityKeyDetailBinding.keydetailEt.setEnabled(true);
                    activityKeyDetailBinding.keydetailEt.setCursorVisible(true);
                }else {
                    String keyName = activityKeyDetailBinding.keydetailEt.getText().toString();
                    if(!originalKeyName.equalsIgnoreCase(keyName)){
                        startUpdatekeyName(keyName);
                    }else {
                        Toast.makeText(This(), "已修改!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.keydetail_tv_delete:
                deleteKey();
                break;
        }
    }
}
