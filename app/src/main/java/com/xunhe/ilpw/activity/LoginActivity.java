package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.databinding.ActivityLoginBinding;
import com.xunhe.ilpw.model.MLoginResult;
import com.xunhe.ilpw.utils.HttpHelper;

import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Request;


public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private ActivityLoginBinding activityLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityLoginBinding = setView(R.layout.activity_login,false);
        }catch (Exception e){}
        toDo();
    }

    @Override
    protected void toDo() {
        if(activityLoginBinding!=null){
            activityLoginBinding.loginTvLogin.setOnClickListener(this);
            activityLoginBinding.loginTvRegister.setOnClickListener(this);
            activityLoginBinding.loginTvForget.setOnClickListener(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode ,int resultCode ,Intent intent){
        super.onActivityResult(requestCode ,resultCode ,intent);
        if(requestCode==666&&resultCode==200){
            if(intent != null){
                String phone = intent.getStringExtra("phone");
                activityLoginBinding.loginEtPhone.setText(phone);
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_tv_login:
                handleLogin();
                break;
            case R.id.login_tv_register:
                Intent i = new Intent(LoginActivity.this , RegisterActivity.class);
                i.setType("");
                startActivityForResult(i , 666);
                break;
            case R.id.login_tv_forget:
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                intent.setType("forget");
                startActivityForResult(intent , 666);
                break;
        }
    }

    /*handle login*/
    public void handleLogin() {
        String phone = activityLoginBinding.loginEtPhone.getText().toString().trim();
        String password = activityLoginBinding.loginEtPwd.getText().toString();
        int rule = checkRule(phone,password);
        if(0x000==rule){
            JSONObject prm = new JSONObject();
            try {
                prm.put("phone",phone);
                prm.put("password",password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpHelper.request("api/login",prm.toString(),callback);
        }
    }

    WlCallback callback = new WlCallback() {
        @Override
        public void onBefore(Request request , int id){
            activityLoginBinding.loginProgressbar.setVisibility(View.VISIBLE);
        }
        @Override
        public void onError(Call call, Exception e, int id) {
            activityLoginBinding.loginProgressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(This() , "服务异常" , Toast.LENGTH_LONG).show();
        }

        @Override
        public void onResponse(String response, int id)  {
            activityLoginBinding.loginProgressbar.setVisibility(View.INVISIBLE);
            if(response!=null){
                try {

                    MLoginResult mLoginResult = com.alibaba.fastjson.JSONObject.parseObject(response, MLoginResult.class);
                    SPHelper.setStringData(This(),"user_userId",mLoginResult.userId);
                    SPHelper.setStringData(This(),"user_token",mLoginResult.token);
                    SPHelper.setStringData(This(),"user_phone",mLoginResult.phone);
                    SPHelper.setStringData(This(),"user_type",mLoginResult.type);
                    SPHelper.setStringData(This(),"user_head_picture",mLoginResult.head_picture);

                    SPHelper.setBooleanData(This(),"isLogin",true);
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    intent.putExtra("user_type",mLoginResult.type);
                    startActivity(intent);

                    finish();

                }catch (Exception e){}
            }else Toast.makeText(This() , "账号或密码错误!" , Toast.LENGTH_LONG).show();
        }
    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            ((BaseApplication)getApplication()).exit();
            System.exit(0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode , event);
    }

    /*check rule*/
    public int checkRule(String phone , String password){

        String REGEX_MOBILE = "^1[3|4|5|7|8][0-9]\\d{8}$";
        Pattern pattern = Pattern.compile(REGEX_MOBILE);

        if (TextUtils.isEmpty(phone)) {//手机号码为空
            Toast.makeText(This(), "手机号码不能为空!", Toast.LENGTH_SHORT).show();
            return 0x001;
        } else if (!pattern.matcher(phone).find()) {//手机号码无效
            Toast.makeText(This(), "手机号码无效!", Toast.LENGTH_SHORT).show();
            return 0x002;
        } else if (TextUtils.isEmpty(password)) {//密码为空
            Toast.makeText(This(), "密码不能为空！", Toast.LENGTH_SHORT).show();
            return 0x003;
        }

        return 0x000;
    }
}
