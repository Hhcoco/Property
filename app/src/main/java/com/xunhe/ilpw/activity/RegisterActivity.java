package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityRegisterBinding;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.MD5Util;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Request;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private ActivityRegisterBinding activityRegisterBinding;
    private CountDownTimer countDownTimer;
    private WlCallback callback , registerCallback;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityRegisterBinding = setView(R.layout.activity_register , false);
        }catch (Exception e){}
        if(activityRegisterBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {
        loadingDialog = new LoadingDialog();
        try{
            if(getIntent().getType().equalsIgnoreCase("forget"))
                activityRegisterBinding.registerTvTitle.setText("重置密码");
            activityRegisterBinding.registerTvRegister.setText("确定");
        }catch (Exception e){}
        activityRegisterBinding.registerTvGetcode.setOnClickListener(this);
        activityRegisterBinding.registerTvRegister.setOnClickListener(this);
        activityRegisterBinding.registerLlLeft.setOnClickListener(this);
    }

    /*获取验证码*/
    public void getCode(String phone){
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(This(), "网络故障或服务器异常!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    Toast.makeText(This(), "已发送，请注意查收!", Toast.LENGTH_SHORT).show();
                }
            };
        }
        if(countDownTimer == null){
            countDownTimer = new CountDownTimer(60000 , 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    activityRegisterBinding.registerTvGetcode.setText(millisUntilFinished/1000+"s");
                }

                @Override
                public void onFinish() {
                    activityRegisterBinding.registerTvGetcode.setText("重新获取");
                }
            };
        }

        countDownTimer.start();

        JSONObject prm = new JSONObject();
        String key = MD5Util.MD5(phone);
        try {
            prm.put("type", "1");
            prm.put("target", phone);
            prm.put("key", key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Boolean isConnected = OperateHelper.isNetworkConnected(This());
        if(isConnected) {
            HttpHelper.request("api/validate", prm.toString(), callback);
        }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
    }
    /*忘记密码*/
    public void forget(){

        final String phone  = activityRegisterBinding.registerEtPhone.getText().toString();
        String code = activityRegisterBinding.registerEtCode.getText().toString();
        String pwd = activityRegisterBinding.registerEtPwd.getText().toString();
        String cofirmpwd = activityRegisterBinding.registerEtConfirmpwd.getText().toString();

        if(0x000 != OperateHelper.checkRule(This() , phone))
            return;
        if("".equalsIgnoreCase(code)){
            Toast.makeText(This() , "验证码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equalsIgnoreCase(pwd) || "".equalsIgnoreCase(cofirmpwd)){
            Toast.makeText(This() , "密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if(!pwd.equalsIgnoreCase(cofirmpwd)){
            Toast.makeText(This() , "两次密码不一致！",Toast.LENGTH_LONG).show();
            return;
        }
        if(registerCallback == null){
            registerCallback = new WlCallback() {
                @Override
                public void onBefore(Request request, int id) {
                    loadingDialog.show(getSupportFragmentManager() , "loadingdialog");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This() , "重置密码失败！",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        Toast.makeText(This() , "重置密码成功！",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("phone" , phone);
                        setResult(200 , intent);
                        finish();
                    }
                }
            };
        }

        JSONObject prm = new JSONObject();
        try {
            prm.put("phone", phone);
            prm.put("pass", pwd);
            prm.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Boolean isConnected = OperateHelper.isNetworkConnected(This());
        if(isConnected) {
            HttpHelper.request("/api/forget", prm.toString(), registerCallback);
        }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();

    }
    /*注册*/
    public void register(){

        final String phone  = activityRegisterBinding.registerEtPhone.getText().toString();
        String code = activityRegisterBinding.registerEtCode.getText().toString();
        String pwd = activityRegisterBinding.registerEtPwd.getText().toString();
        String cofirmpwd = activityRegisterBinding.registerEtConfirmpwd.getText().toString();

        if(0x000 != OperateHelper.checkRule(This() , phone))
            return;
        if("".equalsIgnoreCase(code)){
            Toast.makeText(This() , "验证码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if("".equalsIgnoreCase(pwd) || "".equalsIgnoreCase(cofirmpwd)){
            Toast.makeText(This() , "密码不能为空！",Toast.LENGTH_LONG).show();
            return;
        }
        if(!pwd.equalsIgnoreCase(cofirmpwd)){
            Toast.makeText(This() , "两次密码不一致！",Toast.LENGTH_LONG).show();
            return;
        }
        if(registerCallback == null){
            registerCallback = new WlCallback() {
                @Override
                public void onBefore(Request request, int id) {
                    loadingDialog.show(getSupportFragmentManager() , "loadingdialog");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This() , "注册失败！",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        Toast.makeText(This() , "注册成功！",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.putExtra("phone" , phone);
                        setResult(200 , intent);
                        finish();
                    }else Toast.makeText(This() , "手机号已经使用！",Toast.LENGTH_LONG).show();
                }
            };
        }

        JSONObject prm = new JSONObject();
        try {
            prm.put("phone", phone);
            prm.put("password", pwd);
            prm.put("code", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Boolean isConnected = OperateHelper.isNetworkConnected(This());
        if(isConnected) {
            HttpHelper.request("api/register", prm.toString(), registerCallback);
        }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_tv_getcode:
                if("获取验证码".equalsIgnoreCase(activityRegisterBinding.registerTvGetcode.getText().toString())
                        || "重新发送".equalsIgnoreCase(activityRegisterBinding.registerTvGetcode.getText().toString())){
                    String phone = activityRegisterBinding.registerEtPhone.getText().toString();
                    if(0x000 == OperateHelper.checkRule(This() , phone)){
                        getCode(phone);
                    }
                }
                break;
            case R.id.register_ll_left:
                finish();
                break;
            case R.id.register_tv_register:
                if("forget".equalsIgnoreCase(getIntent().getType())){
                    forget();
            }else
                register();
                break;
        }
    }
}
