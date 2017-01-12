package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityKeyShareBinding;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;

public class KeyShareActivity extends BaseActivity implements View.OnClickListener{

    private ActivityKeyShareBinding activityKeyShareBinding;
    /*选择的分享天数*/
    private String time , keyId;
    private WlCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityKeyShareBinding = setView(R.layout.activity_key_share,false);
        }catch (Exception e){}
        if(activityKeyShareBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {
        initData();
    }
    /*载入数据*/
    public void initData(){

        try {
            keyId = getIntent().getStringExtra("keyId");
        }catch (Exception e){}

        activityKeyShareBinding.keyShareTvOk.setOnClickListener(this);
        activityKeyShareBinding.keyshareLlLeft.setOnClickListener(this);
        activityKeyShareBinding.keyShareTvTime.setOnClickListener(this);

        ArrayList<String> mList = new ArrayList<String>();
        for(int i=1;i<4;i++) {
            mList.add(i+"天");
        }
        activityKeyShareBinding.keyShareWheelview.setItems(mList);
        activityKeyShareBinding.keyShareWheelview.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                activityKeyShareBinding.keyShareTvTime.setText(item);
                time = item.substring(0,1);
            }
        });

    }
    /*开启网络请求分享钥匙*/
    public void startShare(){

        callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                if(response != null){
                    Toast.makeText(This() , "分享申请已提交，待审核!" , Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(This() , "提交申请失败，请稍后重试!" , Toast.LENGTH_SHORT).show();
                }
            }
        };

        String phone = activityKeyShareBinding.keyShareEt.getText().toString();
        if(0x000 == OperateHelper.checkRule(This() , phone)) {
            String token = SPHelper.getStringData(this, "user_token");
            if (!"".equalsIgnoreCase(token)) {
                JSONObject prm = new JSONObject();
                try {
                    prm.put("token", token);
                    prm.put("phone", phone);
                    prm.put("key_id", keyId);
                    prm.put("expire_time", time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean isConnected = OperateHelper.isNetworkConnected(This());
                if (isConnected) {
                    HttpHelper.request("api/key/share", prm.toString(), callback);
                } else Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.keyshare_ll_left:
                finish();
                break;
            case R.id.key_share_tv_ok:
                startShare();
                break;
            case R.id.key_share_tv_time:
                if(View.INVISIBLE == activityKeyShareBinding.keyShareWheelview.getVisibility()){
                    activityKeyShareBinding.keyShareWheelview.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
