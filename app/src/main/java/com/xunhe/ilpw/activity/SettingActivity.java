package com.xunhe.ilpw.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Looper;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.databinding.ActivitySettingBinding;
import com.xunhe.ilpw.service.UpdateService;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.SureCancelDialog;

public class SettingActivity extends BaseActivity implements View.OnClickListener{

    private ActivitySettingBinding activitySettingBinding;
    private Boolean isOpen;
    private SureCancelDialog sureCancelDialog , updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activitySettingBinding = setView(R.layout.activity_setting , false);
        }catch (Exception e){}
        if(activitySettingBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activitySettingBinding.settingRlExit.setOnClickListener(this);
        activitySettingBinding.settingRlAbout.setOnClickListener(this);
        activitySettingBinding.settingRlUpdate.setOnClickListener(this);
        activitySettingBinding.settingImgLeft.setOnClickListener(this);
        activitySettingBinding.settingRlChangepwd.setOnClickListener(this);

        isOpen = SPHelper.getBooleanData(This() , "user_isOpen");
        if(!isOpen){
            activitySettingBinding.settingSwitch.setChecked(false);
            MiaodouKeyAgent.setNeedSensor(false);
        }else {
            activitySettingBinding.settingSwitch.setChecked(true);
            MiaodouKeyAgent.setNeedSensor(true);
        }
        activitySettingBinding.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SPHelper.setBooleanData(This() , "user_isOpen" , true);
                    MiaodouKeyAgent.setNeedSensor(true);
                }else {
                    SPHelper.setBooleanData(This() , "user_isOpen" , false);
                    MiaodouKeyAgent.setNeedSensor(false);
                }
            }
        });

    }

    /*检查更新*/
    public void checkUpdate(){
        ((BaseApplication)getApplication()).cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                /*从SP中读取服务器版本号，与本地版本号对比*/
                String serviceVersion = SPHelper.getStringData(This() , "user_serviceversion");
                if(!"".equalsIgnoreCase(serviceVersion)){
                    int versionCode = Integer.parseInt(serviceVersion);
                    int curVersion = OperateHelper.getVersionCode(This());
                    String tip = SPHelper.getStringData(This() , "user_updatetip");
                    if(versionCode > curVersion){
                        updateDialog = new SureCancelDialog();
                        updateDialog.setTitle("发现新版本,更新后体验更优");
                        updateDialog.setBody("　　"+tip);
                        updateDialog.setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                /*开启下载服务*/
                                updateDialog.dismiss();
                                Intent intent = new Intent(SettingActivity.this , UpdateService.class);
                                intent.putExtra("flag" , 0x002);
                                startService(intent);
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateDialog.dismiss();
                            }
                        });
                        updateDialog.show(getSupportFragmentManager() , "updatedialog");
                    }else {
                        Looper.prepare();
                        Toast.makeText(This() , "当前已是最新版本，无需更新！",Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }else {
                    Looper.prepare();
                    Toast.makeText(This() , "当前已是最新版本，无需更新！",Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }
        });
    }

    /*退出登录*/
    public void exit(){
        sureCancelDialog = new SureCancelDialog();
        sureCancelDialog.setTitle("是否要退出登录？");
        sureCancelDialog.setBody("  　　退出登录后用户数据将被清除，真的要退出吗？");
        sureCancelDialog.show(getSupportFragmentManager() , "");
        sureCancelDialog.setListener(this,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_rl_exit:
                exit();
                break;
            case R.id.setting_rl_about:
                startActivity(new Intent(SettingActivity.this , AboutActivity.class));
                break;
            case R.id.setting_rl_update:
                checkUpdate();
                break;
            case R.id.surecanceldialog_tv_ok:
                sureCancelDialog.dismiss();
                ((BaseApplication)getApplication()).cacheThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        SPHelper.clearData(This());
                        SPHelper.setBooleanData(This(),"isFirstOpen",true);
                    }
                });

                /*if(!((BaseApplication)getApplication()).isPreOpen) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    boolean isSupportBT = bluetoothAdapter == null?false:true;
                    if(isSupportBT){
                        bluetoothAdapter.disable();
                    }
                }*/
                //((BaseApplication)getApplication()).isRegister = false;
                //((BaseApplication)getApplication()).exit();  这一步操作放到login里面的返回键中。
                Intent intent = new Intent(SettingActivity.this , LoginActivity.class);
                intent.setType("");
                startActivity(intent);
                finish();
                break;
            case R.id.surecanceldialog_tv_cancel:
                sureCancelDialog.dismiss();
                break;
            case R.id.setting_img_left:
                finish();
                break;
            case R.id.setting_rl_changepwd:
                startActivity(new Intent(SettingActivity.this , ChangePwdActivity.class));
                break;
        }
    }
}
