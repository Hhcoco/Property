package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
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

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityAddPersonBinding;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MHouse;
import com.xunhe.ilpw.model.MHouseInfo;
import com.xunhe.ilpw.model.MKeyInfo;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

public class AddPersonActivity extends BaseActivity implements View.OnClickListener{

    private ActivityAddPersonBinding activityAddPersonBinding;
    private int type = 0x000;
    private LoadingDialog loadingDialog;
    private WlCallback callback , addPersonCallback;
    private LinkedList<String> mItems = new LinkedList<String>() , mHouseIds = new LinkedList<String>() , mHouseCode = new LinkedList<String>();
    private LinkedList<MKeyInfo> mData = new LinkedList<MKeyInfo>();
    private DeviceAdapter rvadapter;
    private ArrayList<String> checkedKey = new ArrayList<String>();
    private String selectedHouseId;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityAddPersonBinding = setView(R.layout.activity_add_person , false);
        }catch (Exception e){}
        if(activityAddPersonBinding != null) toDo();
    }

    @Override
    protected void toDo() {

        activityAddPersonBinding.addpersonLlLeft.setOnClickListener(this);
        activityAddPersonBinding.addpersonTvOk.setOnClickListener(this);
        loadingDialog = new LoadingDialog();
        type = getIntent().getIntExtra("persontype" , 0x000);
        if(0x001 == type){
            activityAddPersonBinding.addpersonTvTitle.setText("添加家人");
            uri ="api/relation/addFamily";
        }else if(0x002 == type){
            activityAddPersonBinding.addpersonTvTitle.setText("添加租户");
            uri = "api/relation/addTenant";
        }
        rvadapter = new DeviceAdapter(mData);
        activityAddPersonBinding.addpersonRecyclerview.setLayoutManager(new LinearLayoutManager(This()));
        activityAddPersonBinding.addpersonRecyclerview.setAdapter(rvadapter);

        getHouseInfo();
    }

    /*create Adapter*/
    class DeviceAdapter extends RecyclerView.Adapter{

        LinkedList<MKeyInfo> mData;
        public DeviceAdapter(LinkedList<MKeyInfo> data){
            this.mData = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(This()).inflate(R.layout.item_device,parent,false);
            DeviceVH VH = new DeviceVH(itemView);
            return VH;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(mData.size()>0){
                ((DeviceVH)holder).mTvDeviceName.setText(mData.get(position).name);
                ((DeviceVH)holder).mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            if(!checkedKey.contains(mData.get(position).id)){
                                checkedKey.add(mData.get(position).id);
                            }
                        }else {
                            if(checkedKey.contains(mData.get(position).id)){
                                checkedKey.remove(mData.get(position).id);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
        class DeviceVH extends RecyclerView.ViewHolder{

            TextView mTvDeviceName;
            CheckBox mCb;

            public DeviceVH(View itemView) {
                super(itemView);
                mTvDeviceName = (TextView) itemView.findViewById(R.id.item_device_tv);
                mCb = (CheckBox) itemView.findViewById(R.id.item_device_checkbox);
            }
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
        /*默认显示第一栋房子的钥匙信息*/
        if(mHouseCode.size()>0){
            String code = mHouseCode.get(0);
            mData.clear();
            for(MKeyInfo mKeyInfo:mHouseInfo.keys){
                if(mKeyInfo.depart_code.equalsIgnoreCase(code)){
                    mData.add(mKeyInfo);
                }
            }
            rvadapter.notifyDataSetChanged();
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityAddPersonBinding.addpersonSpinner.setAdapter(adapter);
        activityAddPersonBinding.addpersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHouseId = mHouseIds.get(position);
                if(mHouseCode.size()>0){
                    String code = mHouseCode.get(position);
                    mData.clear();
                    for(MKeyInfo mKeyInfo:mHouseInfo.keys){
                        if(mKeyInfo.depart_code.equalsIgnoreCase(code)){
                            mData.add(mKeyInfo);
                        }
                    }
                    rvadapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedHouseId = "";
                mData.clear();
                rvadapter.notifyDataSetChanged();
            }
        });
    }

    /*获取用户房屋钥匙信息*/
    public void getHouseInfo(){
        if(callback == null){
            callback = new WlCallback() {
                @Override
                public void onBefore(Request request ,int id){
                    loadingDialog.show(getSupportFragmentManager() , "loadingdialog");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    OperateHelper.Log("房屋钥匙信息："+response);
                    if(response != null) {
                        MHouseInfo mHouseInfo = com.alibaba.fastjson.JSONObject.parseObject(response, MHouseInfo.class);
                        updateView(mHouseInfo);
                    }
                }
            };
            String token = SPHelper.getStringData(this,"user_token");
            if(!"".equalsIgnoreCase(token)) {
                JSONObject prm = new JSONObject();
                try {
                    prm.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean isConnected = OperateHelper.isNetworkConnected(This());
                if(isConnected) {
                    HttpHelper.request("api/user/info", prm.toString(), callback);
                }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*添加用户*/
    public void addPerson(){
        String name = activityAddPersonBinding.addpersonEtName.getText().toString();
        String phone = activityAddPersonBinding.addpersonEtPhone.getText().toString();
        String idcard = activityAddPersonBinding.addpersonEtPwd.getText().toString();
        String national = activityAddPersonBinding.addpersonEtNational.getText().toString();
        /*去掉首尾的[]符号*/
        String key = checkedKey.toString().replace("[","");
        key = key.replace("]" , "");
        if("".equalsIgnoreCase(name)){
            Toast.makeText(This() , "名字不能为空!"  , Toast.LENGTH_SHORT).show();
            return;
        }else if("".equalsIgnoreCase(phone)){
            Toast.makeText(This() , "电话不能为空!"  , Toast.LENGTH_SHORT).show();
            return;
        }else if("".equalsIgnoreCase(idcard)){
            Toast.makeText(This() , "身份证不能为空!"  , Toast.LENGTH_SHORT).show();
            return;
        }else if("".equalsIgnoreCase(national)){
            Toast.makeText(This() , "民族不能为空!"  , Toast.LENGTH_SHORT).show();
            return;
        }else if("".equalsIgnoreCase(key)){
            Toast.makeText(This() , "至少选择一把钥匙!"  , Toast.LENGTH_SHORT).show();
            return;
        }
        if(addPersonCallback == null){
            addPersonCallback = new WlCallback() {
                @Override
                public void onBefore(Request request ,int id){
                    loadingDialog.show(getSupportFragmentManager() , "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                    Toast.makeText(This() , "网络已断开!"  , Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        EventBus.getDefault().post(new MEventBusMsg(Config.PERSON_CHANGED));
                        Toast.makeText(This() , "添加成功!"  , Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        String token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("phone", phone);
                prm.put("username", name);
                prm.put("id_card", idcard);
                prm.put("national", national);
                prm.put("user_house_id", selectedHouseId);
                prm.put("key_id", key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request(uri, prm.toString(), addPersonCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addperson_ll_left:
                finish();
                break;
            case R.id.addperson_tv_ok:
                addPerson();
                break;
        }
    }
}
