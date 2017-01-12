package com.xunhe.ilpw.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityApplykeyBinding;
import com.xunhe.ilpw.model.MDevice;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MHouse;
import com.xunhe.ilpw.model.MHouseInfo;
import com.xunhe.ilpw.model.MKeyInfo;
import com.xunhe.ilpw.model.MUserInfo;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class ApplykeyActivity extends BaseActivity implements View.OnClickListener{

    private ActivityApplykeyBinding activityApplykeyBinding;
    private WlCallback  applykeycallback , getHouseCallback , getDeviceCallback , callback;
    private ArrayList<String>  checkedDevice = new ArrayList<String>();;
    private String depart_code , token;
    private LoadingDialog loadingDialog;
    private DeviceAdapter rvadapter;
    private Context context;
    private List<MDevice> mDeviceData = new ArrayList<MDevice>();
    private LinkedList<String> mItems = new LinkedList<String>() , mHouseIds = new LinkedList<String>() , mHouseCode = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        try {
            activityApplykeyBinding = setView(R.layout.activity_applykey,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(activityApplykeyBinding!=null)
            toDo();
    }

    @Override
    protected void toDo() {

        loadingDialog = new LoadingDialog();
        rvadapter = new DeviceAdapter(mDeviceData);
        activityApplykeyBinding.applykeyRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        activityApplykeyBinding.applykeyRecyclerview.setAdapter(rvadapter);

        try {
            initData();
            initClick();
            getData();
        }catch (Exception e){

        }

    }

    /*载入数据*/
    public void initData() throws Exception{

        String userInfo = SPHelper.getStringData(This(),"user_info");
        MUserInfo mUserInfo = JSONObject.parseObject(userInfo , MUserInfo.class);
        if(mUserInfo!=null){
            activityApplykeyBinding.applykeyTvName.setText(mUserInfo.getUsername());
            String userPhone = SPHelper.getStringData(This(),"user_phone");
            activityApplykeyBinding.applykeyTvPhone.setText(userPhone);
        }else getUserInfo();

    }

    /*获取用户信息*/
    public void getUserInfo(){
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.v("out", "获取用户信息失败!");
                }

                @Override
                public void onResponse(String response, int id) {

                    if (response != null) {
                        SPHelper.setStringData(context, "user_info", response);
                        MUserInfo mUserInfo = JSONObject.parseObject(response , MUserInfo.class);
                        activityApplykeyBinding.applykeyTvName.setText(mUserInfo.getUsername());
                        String userPhone = SPHelper.getStringData(This(),"user_phone");
                        activityApplykeyBinding.applykeyTvPhone.setText(userPhone);
                    } else OperateHelper.Log("获取用户信息失败null");

                }
            };
        }
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(context);
            if(isConnected) {
                HttpHelper.request("api/user", prm.toString(), callback);
            }else Toast.makeText(context,"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }


    }

    /*更新view*/
    public void updateView(final MHouseInfo mHouseInfo ){

        mItems.clear();
        for(MHouse houses:mHouseInfo.houses){
            mItems.add(houses.unit+"单元"+houses.building+"栋"+houses.house);
            /*记录位置*/
            mHouseIds.add(houses.id);
            mHouseCode.add(houses.depart_code);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityApplykeyBinding.applykeySpinner.setAdapter(adapter);
        activityApplykeyBinding.applykeySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mHouseCode.size()>0){
                    if(activityApplykeyBinding.applykeyRlDescri.getVisibility() == View.VISIBLE)
                        activityApplykeyBinding.applykeyRlDescri.setVisibility(View.GONE);
                    depart_code = mHouseCode.get(position);
                    getAllDevice(depart_code);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mDeviceData.clear();
                rvadapter.notifyDataSetChanged();
                if(activityApplykeyBinding.applykeyRlDescri.getVisibility() == View.VISIBLE)
                    activityApplykeyBinding.applykeyRlDescri.setVisibility(View.GONE);
            }
        });
    }

    /*获取对应小区的所有设备*/
    public void getAllDevice(String departCode){
        if(getDeviceCallback == null){
            getDeviceCallback = new WlCallback() {

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
                        OperateHelper.Log("获取的设备为："+response);
                        if(activityApplykeyBinding.applykeyRlDescri.getVisibility() == View.GONE)
                            activityApplykeyBinding.applykeyRlDescri.setVisibility(View.VISIBLE);
                        rvadapter.setData(JSONObject.parseArray(response , MDevice.class));
                        rvadapter.notifyDataSetChanged();
                    }
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
                prm.put("depart_code", departCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/community/guard", prm.toString(), getDeviceCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*获取网络数据*/
    public void getData(){
        if(getHouseCallback == null){
            getHouseCallback = new WlCallback() {
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
                    OperateHelper.Log("用户详细信息："+response);
                    if(response != null){
                        if(response != null) {
                            MHouseInfo mHouseInfo = com.alibaba.fastjson.JSONObject.parseObject(response, MHouseInfo.class);
                            updateView(mHouseInfo);
                        }
                    }
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/user/info", prm.toString(), getHouseCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*设置点击事件*/
    public void initClick(){
        activityApplykeyBinding.applykeyLlLeft.setOnClickListener(this);
        activityApplykeyBinding.applykeyTvSubmit.setOnClickListener(this);
        activityApplykeyBinding.applykeyTvRecord.setOnClickListener(this);
    }


    /*create adapter*/
    class DeviceAdapter extends RecyclerView.Adapter{

        private List<MDevice> data;

        public DeviceAdapter(List<MDevice> data){
            this.data = data;
        }
        public void setData(List<MDevice> data){
            this.data = data;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(This()).inflate(R.layout.item_device,parent,false);
            DeviceViewHolder VH = new DeviceViewHolder(itemView);
            return VH;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(data!=null&&data.size()>0){

                    ((DeviceViewHolder)holder).mTvDeviceName.setText(data.get(position).name);

                    ((DeviceViewHolder)holder).mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                if(!checkedDevice.contains(data.get(position).code)) {
                                    checkedDevice.add(data.get(position).code);
                                }
                            }else{
                                if(checkedDevice.contains(data.get(position).code)) {
                                    checkedDevice.remove(data.get(position).code);
                                }
                            }
                        }
                    });

            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        /**/
        class DeviceViewHolder extends RecyclerView.ViewHolder{

            TextView mTvDeviceName;
            CheckBox mCb;

            public DeviceViewHolder(View itemView) {
                super(itemView);
                mTvDeviceName = (TextView) itemView.findViewById(R.id.item_device_tv);
                mCb = (CheckBox) itemView.findViewById(R.id.item_device_checkbox);
            }
        }

    }

    /*申请钥匙*/
    public void applyKey(){

        if(applykeycallback == null) {
            applykeycallback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {

                }

                @Override
                public void onResponse(String response, int id) {
                    OperateHelper.Log("申请钥匙结果"+response);
                    if(response != null)
                        Toast.makeText(This(), "已申请，等待审核!", Toast.LENGTH_SHORT).show();
                }
            };
        }

        String guard_code = checkedDevice.toString();
        guard_code = guard_code.replace("[" , "");
        guard_code = guard_code.replace("]" , "");
        String descri = activityApplykeyBinding.applykeyEtDescri.getText().toString();
        if(!"".equalsIgnoreCase(guard_code)) {
            String token = SPHelper.getStringData(this, "user_token");
            if (!"".equalsIgnoreCase(token)) {
                org.json.JSONObject prm = new org.json.JSONObject();
                try {
                    prm.put("token", token);
                    prm.put("guard_code", guard_code);
                    prm.put("depart_code", depart_code);
                    prm.put("type", "1");
                    prm.put("description", descri);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean isConnected = OperateHelper.isNetworkConnected(This());
                if (isConnected) {
                    HttpHelper.request("api/application/add", prm.toString(), applykeycallback);
                } else Toast.makeText(This(), "网络已断开，请检查网络连接!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(This(), "至少选择一个设备!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.applykey_ll_left:
                finish();
                break;
            case R.id.applykey_tv_submit:
                applyKey();
                break;
            case R.id.applykey_tv_record:
                startActivity(new Intent(ApplykeyActivity.this , ApplyRecordActivity.class));
                break;
        }
    }
}
