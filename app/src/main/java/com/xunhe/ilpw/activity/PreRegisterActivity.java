package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.invs.InvsIdCard;
import com.invs.invswlt;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.databinding.ActivityPreRegisterBinding;
import com.xunhe.ilpw.model.MHouses;
import com.xunhe.ilpw.model.MRoot;
import com.xunhe.ilpw.model.MUnits;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.SureCancelDialog;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.WheelView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Request;

public class PreRegisterActivity extends BaseActivity implements View.OnClickListener{

    private ActivityPreRegisterBinding activityPreRegisterBinding;
    private BaseApplication application ;
    private LoadingDialog loadingDialog;
    private SureCancelDialog sureCancelDialog;
    private static final String TEMP_IMG_NAME = "xunhe_temp_head.jpg";
    private Bitmap bitmap , bmp;
    private WlCallback houseCallback , uploadPicCallback , uploadDataCallback;
    private String token , userCardNo , userName, sex , national , userType = "1", houseId , tel;
    private boolean isNeedReTry = false , isPicOk = false;
    private InvsIdCard invsIdCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityPreRegisterBinding = setView(R.layout.activity_pre_register , false);
        }catch (Exception e){}
        if(activityPreRegisterBinding != null) toDo();
    }

    @Override
    protected void toDo() {
        application = (BaseApplication) getApplication();
        loadingDialog = new LoadingDialog();

        initClick();
        connectDevice();
    }

    /*init click*/
    public void initClick(){
        activityPreRegisterBinding.preregisterLlLeft.setOnClickListener(this);
        activityPreRegisterBinding.preregisterTvRight.setOnClickListener(this);
        activityPreRegisterBinding.preregisterImgNow.setOnClickListener(this);
        activityPreRegisterBinding.preregisterLlLoading.setOnClickListener(this);
        activityPreRegisterBinding.preregisterTvOk.setOnClickListener(this);

        activityPreRegisterBinding.preregisterRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.preregister_rb_living){
                    userType = "1";
                }else if(checkedId == R.id.preregister_rb_tenant) userType = "2";
            }
        });

        activityPreRegisterBinding.preregisterWheelviewRoom.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                houseId = item.split("/")[1];
            }
        });

        /*解决楼栋选择器与ScrollView滑动冲突问题*/
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    activityPreRegisterBinding.scrollView.requestDisallowInterceptTouchEvent(false);
                }else{
                    activityPreRegisterBinding.scrollView.requestDisallowInterceptTouchEvent(true);
                }
                return false;
            }
        };
        activityPreRegisterBinding.preregisterWheelviewBuilding.setOnTouchListener(listener);
        activityPreRegisterBinding.preregisterWheelviewUnit.setOnTouchListener(listener);
        activityPreRegisterBinding.preregisterWheelviewRoom.setOnTouchListener(listener);
    }

    /*先判断连接，如果未连接弹出框提示去连接设备*/
    public void connectDevice(){
        if(!application.isConnect){
            /*先自动连接*/
            final String adress = SPHelper.getStringData(This() , "BLEAdress");
            loadingDialog.show(getSupportFragmentManager() , "");
            if(!"".equalsIgnoreCase(adress)){
                application.cacheThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(application.mClient.connectBt(adress)){
                            loadingDialog.dismiss();
                            getData();
                        }else {
                            loadingDialog.dismiss();
                            sureCancelDialog = new SureCancelDialog();
                            sureCancelDialog.setTitle("提示:");
                            sureCancelDialog.setBody("　　自动连接设备失败，去手动连接?");
                            sureCancelDialog.setListener(PreRegisterActivity.this ,PreRegisterActivity.this);
                            sureCancelDialog.show(getSupportFragmentManager() , "");
                        }
                    }
                });
            }else {
                sureCancelDialog = new SureCancelDialog();
                sureCancelDialog.setTitle("提示:");
                sureCancelDialog.setBody("　　当前未连接设备，是否去连接?");
                sureCancelDialog.setListener(this ,this);
                sureCancelDialog.show(getSupportFragmentManager() , "");
            }
        }else {
            getData();
        }
    }

    /*获取小区信息*/
    public void getData(){
        if(houseCallback == null){
            houseCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    try {
                        loadingDialog.show(getSupportFragmentManager(), "");
                    }catch (Exception e){}
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        MPreRegisterResult mPreRegisterResult = com.alibaba.fastjson.JSONObject.parseObject(response , MPreRegisterResult.class);
                        activityPreRegisterBinding.preregisterTvBuilding.setText(mPreRegisterResult.depart_name);
                        ArrayList<MRoot> data = mPreRegisterResult.lists;
                        HashMap<String , HashMap<String , ArrayList<MHouses>>> hashMap = new HashMap<String , HashMap<String , ArrayList<MHouses>>>();
                        if(data != null && data.size() > 0 ){
                            for(MRoot mRoot :data){
                                HashMap<String, ArrayList<MHouses>> tempHaspMap = new HashMap<String, ArrayList<MHouses>>();
                                for(MUnits mUnits:mRoot.units) {
                                    tempHaspMap.put(mUnits.unit , (ArrayList<MHouses>) mUnits.houses);
                                }
                                hashMap.put(mRoot.building ,tempHaspMap );
                            }
                        }

                        ArrayList<String> buildings = new ArrayList<String>();
                        ArrayList<String> units = new ArrayList<String>();
                        ArrayList<String> rooms = new ArrayList<String>();
                        if(data != null)
                        for(String item :hashMap.keySet()){
                            buildings.add(item);
                        }
                        for(String unit:hashMap.get(buildings.get(0)).keySet()){
                            units.add(unit);
                        }
                        for(MHouses room:hashMap.get(buildings.get(0)).get(units.get(0))){
                            rooms.add(room.house+"/"+room.id);
                        }
                        activityPreRegisterBinding.preregisterWheelviewBuilding.setItems(buildings);
                        activityPreRegisterBinding.preregisterWheelviewUnit.setItems(units);
                        activityPreRegisterBinding.preregisterWheelviewRoom.setItems(rooms);
                        /*因为选择器必须要点击一下才会触发监听事件给houseId赋值，但是很多人只有1栋房屋，所以不会去点击，因此这里
                        * 将第一个赋值给houseId*/
                        try{
                            houseId = rooms.get(0).split("/")[1];
                        }catch (Exception e){}

                    }
                }
            };
        }
        if(token == null){
            token = SPHelper.getStringData(this,"user_token");
        }
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/service/house", prm.toString(), houseCallback);
            }else {
                Looper.prepare();
                Toast.makeText(This() , "网络已断开，请检查网络连接!" , Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    /*绑定身份证信息*/
    public void displayView(InvsIdCard invsIdCard){

        userCardNo = invsIdCard.idNo;
        userName = invsIdCard.name;
        sex = invsIdCard.sex;
        national = invsIdCard.nation;

        activityPreRegisterBinding.preregisterTvName.setText(invsIdCard.name);
        activityPreRegisterBinding.preregisterTvIdcard.setText(invsIdCard.idNo.replaceAll("(\\d{4})\\d{10}(\\w{4})","$1*****$2"));

        byte[] szBmp = invswlt.Wlt2Bmp(invsIdCard.wlt);
        if ((szBmp != null) && (szBmp.length == 38862)){
            bmp = BitmapFactory.decodeByteArray(szBmp, 0, szBmp.length);
            activityPreRegisterBinding.preregisterImgCard.setVisibility(View.VISIBLE);
            activityPreRegisterBinding.preregisterImgCard.setImageBitmap(bmp);
        }else{
            Toast.makeText(This() , "读取图片失败!" , Toast.LENGTH_LONG).show();
        }
        if(bmp != null && bitmap == null){
            Toast.makeText(This() , "请拍照!" , Toast.LENGTH_LONG).show();
        }else if(bmp !=null && bitmap != null){
            uploadPic();
        }

    }

    /*开始上传图片*/
    public void uploadPic(){
        if(uploadPicCallback == null){
            uploadPicCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    activityPreRegisterBinding.preregisterImgFail.setVisibility(View.INVISIBLE);
                    activityPreRegisterBinding.preregisterProgressbar.setVisibility(View.VISIBLE);
                    activityPreRegisterBinding.preregisterTvState.setVisibility(View.VISIBLE);
                    activityPreRegisterBinding.preregisterTvState.setText("上传中...");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    isNeedReTry = true;
                    isPicOk = false;
                    activityPreRegisterBinding.preregisterTvState.setText("上传失败,点击重试!");
                    activityPreRegisterBinding.preregisterImgFail.setBackgroundResource(R.drawable.fail);
                    activityPreRegisterBinding.preregisterImgFail.setVisibility(View.VISIBLE);
                    activityPreRegisterBinding.preregisterProgressbar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onResponse(String response, int id) {
                    if(response != null){
                        isPicOk = true;
                        activityPreRegisterBinding.preregisterTvState.setText("上传成功!");
                        activityPreRegisterBinding.preregisterTvOk.setBackgroundColor(getResources().getColor(R.color.color_009FF8));
                        activityPreRegisterBinding.preregisterImgFail.setBackgroundResource(R.drawable.success);
                        activityPreRegisterBinding.preregisterImgFail.setVisibility(View.VISIBLE);
                        activityPreRegisterBinding.preregisterProgressbar.setVisibility(View.INVISIBLE);
                    } else {
                        isNeedReTry = true;
                        isPicOk = false;
                        activityPreRegisterBinding.preregisterTvState.setText("认证失败!");
                        activityPreRegisterBinding.preregisterImgFail.setBackgroundResource(R.drawable.fail);
                        activityPreRegisterBinding.preregisterImgFail.setVisibility(View.VISIBLE);
                        activityPreRegisterBinding.preregisterProgressbar.setVisibility(View.INVISIBLE);
                    }
                }
            };
        }
        if(token == null){
            token = SPHelper.getStringData(this,"user_token");
        }
        if(!"".equalsIgnoreCase(token)) {
            String imgCard = OperateHelper.bitmpaToString(bmp);
            String imgNow = OperateHelper.bitmpaToString(bitmap);
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("id_card", userCardNo);
                prm.put("idcard_face", imgCard);
                prm.put("user_face", imgNow);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/service/validateFace", prm.toString(), uploadPicCallback);
            }else Toast.makeText(This() , "网络已断开，请检查网络连接!" , Toast.LENGTH_SHORT).show();
        }
    }

    /*上传身份信息*/
    public void uploadUserData(){
        if(!isPicOk){
            Toast.makeText(This() , "需先完成身份认证" , Toast.LENGTH_LONG).show();
            return;
        }
        tel = activityPreRegisterBinding.preregisterEtPhone.getText().toString();
        if(0x000 != OperateHelper.checkRule(This() , tel)){
            return;
        }
        if(uploadDataCallback == null){
            uploadDataCallback = new WlCallback() {
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
                        Toast.makeText(This() , "资料上传成功!" , Toast.LENGTH_SHORT).show();
                    }
                }
            };
        }
        if(token == null){
            token = SPHelper.getStringData(this,"user_token");
        }
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("id_card", userCardNo);
                prm.put("username", userName);
                prm.put("sex", sex);
                prm.put("national", national);
                prm.put("user_type", userType);
                prm.put("house_id", houseId);
                prm.put("phone", tel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/service/bindHouse", prm.toString(), uploadDataCallback);
            }else Toast.makeText(This() , "网络已断开，请检查网络连接!" , Toast.LENGTH_SHORT).show();
        }

    }

    /*从相机选取*/
    public void chooseFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(OperateHelper.isHaveSDCard()){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() , TEMP_IMG_NAME)));
            startActivityForResult(intent , 202);
        }else Toast.makeText(This() , "当前没有存储设备可用" , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode , int responseCode , Intent intent) {
        /*RESULT_CANCEL，因为在Fragment中，所以直接使用0*/
        if (responseCode == 0) {
            Toast.makeText(This(), "已取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode) {
            case 202:
                ((BaseApplication)getApplication()).cacheThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        File cropImg = new File(Environment.getExternalStorageDirectory(), TEMP_IMG_NAME);
                        if (cropImg != null && cropImg.length() > 0) {
                            FileInputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream(cropImg);
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                /*base64转码*/
                                byte[] bytes = new byte[(int) cropImg.length()];
                                inputStream.read(bytes);
                                PreRegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        activityPreRegisterBinding.preregisterImgNow.setImageBitmap(bitmap);
                                    }
                                });
                                inputStream.close();
                                String bitmapString = Base64.encodeToString(bytes, Base64.DEFAULT);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (bmp == null && bitmap != null) {
                                Looper.prepare();
                                Toast.makeText(This(), "请读卡!", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            } else if (bmp != null && bitmap != null) {
                                PreRegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        uploadPic();
                                    }
                                });
                            }
                        }
                    }
                });
                    break;

        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try {
            if(bmp != null) bmp.recycle();
            if(bitmap != null) bitmap.recycle();
        }catch (Exception e){}
        File cropImg = new File(Environment.getExternalStorageDirectory() , TEMP_IMG_NAME);
        if(cropImg.exists() &&cropImg.isFile()){
            try {
                cropImg.delete();
            }catch (Exception e){}
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.preregister_ll_left:
                try {
                    if(bmp != null) bmp.recycle();
                    if(bitmap != null) bitmap.recycle();
                }catch (Exception e){}
                File cropImg = new File(Environment.getExternalStorageDirectory() , TEMP_IMG_NAME);
                if(cropImg.exists() &&cropImg.isFile()){
                    try {
                        cropImg.delete();
                    }catch (Exception e){}
                }
                finish();
                break;
            case R.id.surecanceldialog_tv_ok:
                startActivity(new Intent(PreRegisterActivity.this , CardReadDeviceActivity.class));
                finish();
                break;
            case R.id.surecanceldialog_tv_cancel:
                finish();
                break;
            case R.id.preregister_tv_right:
                if(application.mClient != null){
                    Toast.makeText(This() , "正在读卡..." , Toast.LENGTH_LONG).show();
                    ((BaseApplication)getApplication()).cacheThreadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            invsIdCard = application.mClient.readCard();
                            if(invsIdCard != null){
                                PreRegisterActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(invsIdCard != null)
                                        displayView(invsIdCard);
                                    }
                                });

                            }else {
                                Looper.prepare();
                                Toast.makeText(This() , "读卡失败，请重试" , Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }
                        }
                    });

                }
                break;
            case R.id.preregister_img_now:
                chooseFromCamera();
                break;
            case R.id.preregister_ll_loading:
                if(isNeedReTry){
                    uploadPic();
                }
                break;
            case R.id.preregister_tv_ok:
                uploadUserData();
                break;
        }
    }
}
