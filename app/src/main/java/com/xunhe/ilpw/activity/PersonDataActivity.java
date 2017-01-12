package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.squareup.picasso.Picasso;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityPersonDataBinding;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MUserInfo;
import com.xunhe.ilpw.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PersonDataActivity extends BaseActivity implements View.OnClickListener{

    private ActivityPersonDataBinding activityPersonDataBinding;
    private boolean isReadUserInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // EventBus.getDefault().register(this);
        try{
            activityPersonDataBinding = setView(R.layout.activity_person_data , false);
        }catch (Exception e){}
        if(activityPersonDataBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {
        initClick();
        initData();
    }
    public void initData(){

        String userInfo = SPHelper.getStringData(This() , "user_info");
        if(!"".equalsIgnoreCase(userInfo)){
            MUserInfo mUserInfo = JSONObject.parseObject(userInfo , MUserInfo.class);
            activityPersonDataBinding.persondataName.setText(mUserInfo.getUsername());
            activityPersonDataBinding.persondataCardno.setText(mUserInfo.getId_card());
        }
        String phone = SPHelper.getStringData(This() , "user_phone");
        if(!"".equalsIgnoreCase(phone)){
            activityPersonDataBinding.persondataPhone.setText(phone);
        }

    }
    public void initClick(){
        activityPersonDataBinding.persondataLlLeft.setOnClickListener(this);
        activityPersonDataBinding.persondataRlProfile.setOnClickListener(this);
        /*activityPersonDataBinding.persondataName.setOnClickListener(this);
        activityPersonDataBinding.persondataRlCard.setOnClickListener(this);
        activityPersonDataBinding.persondataRlPhone.setOnClickListener(this);*/
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void update(MEventBusMsg msg){
        if(isReadUserInfo) return;
        if(Config.USER_INFO.equalsIgnoreCase(msg.getMsg())){
            String userInfo = SPHelper.getStringData(This() ,"user_info");
            final MUserInfo mUserInfo = com.alibaba.fastjson.JSONObject.parseObject(userInfo , MUserInfo.class);
            if(mUserInfo!=null){
                isReadUserInfo = true;
                String userPhone = SPHelper.getStringData(This(),"user_phone");
                activityPersonDataBinding.persondataName.setText(userPhone);
                PersonDataActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!mUserInfo.getHead_picture().equalsIgnoreCase(""))
                        Picasso.with(This()).load(mUserInfo.getHead_picture()).error(R.drawable.person).into(activityPersonDataBinding.persondataImgProfile);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.persondata_ll_left:
                finish();
                break;
        }
    }
}
