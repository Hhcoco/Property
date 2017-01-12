package com.xunhe.ilpw.activity;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityChangePwdBinding;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;

public class ChangePwdActivity extends BaseActivity implements View.OnClickListener{

    private ActivityChangePwdBinding activityChangePwdBinding;
    private WlCallback getCodeCallback , changePwdCallback;
    private String token;
    private LoadingDialog loadingDialog;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityChangePwdBinding = setView(R.layout.activity_change_pwd , false);
        }catch (Exception e){}
        if(activityChangePwdBinding != null) toDo();
    }

    @Override
    protected void toDo() {
        loadingDialog = new LoadingDialog();
        countDownTimer = new CountDownTimer(60*1000 , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                activityChangePwdBinding.changepwdTvGetcode.setText(millisUntilFinished/1000+"s");
            }

            @Override
            public void onFinish() {
                activityChangePwdBinding.changepwdTvGetcode.setText("重新发送");
            }
        };
        activityChangePwdBinding.changepwdLlLeft.setOnClickListener(this);
        activityChangePwdBinding.changepwdTvGetcode.setOnClickListener(this);
        activityChangePwdBinding.changepwdTvRegister.setOnClickListener(this);
    }

    /*获取验证码*/
    public void getCode(){

        countDownTimer.start();

        if(getCodeCallback == null){
            getCodeCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    loadingDialog.show(getSupportFragmentManager() , "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This(),"出错了!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This(),"已发送，请注意查收!",Toast.LENGTH_SHORT).show();
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("type", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/user/sendsms", prm.toString(), getCodeCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }
    /*修改密码*/
    public void changePwd(){
        String code = activityChangePwdBinding.changepwdEtCode.getText().toString();
        String newpwd = activityChangePwdBinding.changepwdEtPwd.getText().toString();
        if("".equalsIgnoreCase(code)) {
            Toast.makeText(This(),"请填写验证码!",Toast.LENGTH_SHORT).show();
            return;
        }
        if("".equalsIgnoreCase(newpwd)){
            Toast.makeText(This(),"请输入密码!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(changePwdCallback == null) {
            changePwdCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    loadingDialog.show(getSupportFragmentManager() , "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This(),"网络异常!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This(),"已修改!",Toast.LENGTH_SHORT).show();
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("code", code);
                prm.put("pass", newpwd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/user/changepass", prm.toString(), changePwdCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changepwd_tv_getcode:
                if(!activityChangePwdBinding.changepwdTvGetcode.getText().toString().contains("s"))
                getCode();
                break;
            case R.id.changepwd_ll_left:
                finish();
                break;
            case R.id.changepwd_tv_register:
                changePwd();
                break;
        }
    }
}
