package com.xunhe.ilpw.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.hzblzx.miaodou.sdk.core.model.MDVirtualKey;
import com.jauker.widget.BadgeView;
import com.xunhe.ilpw.Interface.PostTips;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityMainBinding;
import com.xunhe.ilpw.fragments.Main_home;
import com.xunhe.ilpw.fragments.Main_me;
import com.xunhe.ilpw.fragments.Main_record;
import com.xunhe.ilpw.fragments.Main_service;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MKey;
import com.xunhe.ilpw.service.CheckNewApplyService;
import com.xunhe.ilpw.service.UpdateService;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.SureCancelDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private ActivityMainBinding activityMainBinding;
    private TextView preTv;
    private String userType;
    private SureCancelDialog dialog , updateDialog;
    public  ArrayList<MKey> keys;
    private Handler mHandler;
    private LoadingDialog loadingDialog;
    private BadgeView badgeView ;
    private BaseApplication application;
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            /*通过DataBind绑定了所有视图和事件*/
            activityMainBinding = setView(R.layout.activity_main,false);
        }catch (Exception e){
            Log.v("out",e.toString());
        }

        toDo();

    }

    /*具体业务逻辑处理*/
    public void toDo(){

        application = (BaseApplication) getApplication();

        loadingDialog = new LoadingDialog();
        if(activityMainBinding!=null) {

            mHandler = new Handler(){
                @Override
                public void handleMessage(Message message){
                    /*从本地读取到数据了*/
                    if(message.what==1){
                        openBlueTooth();
                    }else if(message.what==0){     //读取本地钥匙失败
                        /*从网络再次获取数据*/
                    }
                }
            };

            initData();

            initClick();

            updateDialog();

            initFragment();

            addTips();



        }
    }

    /*是否显示消息提醒*/
    public void addTips(){
        if(!("1".equalsIgnoreCase(userType)&&"3".equalsIgnoreCase(userType))){
            badgeView = new BadgeView(this);

            this.getApplicationContext().bindService(new Intent(MainActivity.this, CheckNewApplyService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    if(service != null){
                        ((CheckNewApplyService.CheckNewApplyBinder)service).getService().setPostTips(new PostTips() {
                            @Override
                            public void isHaveNewApply(boolean newApply) {
                                if(newApply){
                                    badgeView.setBadgeCount(1);
                                    badgeView.setTextColor(getResources().getColor(R.color.color_FF0000));
                                    badgeView.setBackgroundColor(getResources().getColor(R.color.color_FF0000));
                                    badgeView.setBadgeGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
                                    badgeView.setBadgeMargin(6,0,0,0);
                                    badgeView.setTargetView(activityMainBinding.mainTvService);
                                }
                            }
                            @Override
                            public void isHaveNewTips(int newTips) {
                                badgeView.setBadgeCount(newTips);
                                badgeView.setTextColor(getResources().getColor(R.color.white));
                                badgeView.setBackgroundColor(getResources().getColor(R.color.color_FF0000));
                                badgeView.setBadgeGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
                                badgeView.setBadgeMargin(6,0,0,0);
                                badgeView.setTargetView(activityMainBinding.mainTvMe);
                            }
                        });
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            } , BIND_AUTO_CREATE);
        }
    }

    /*如果需要更新，弹出提示用户更新*/
    public void updateDialog(){
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
                                Intent intent = new Intent(MainActivity.this , UpdateService.class);
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
                    }
                }
            }
        });
    }

    /*载入数据*/
    public void initData(){

        /*读取数据是耗时操作*/
        new Thread(new Runnable() {
            @Override
            public void run() {
                String keysJson = SPHelper.getStringData(This(),"keylist");
                try {
                    keys = (ArrayList<MKey>) JSONObject.parseArray(keysJson, MKey.class);
                    if(keys != null && keys.size() >0)
                    mHandler.sendEmptyMessage(1);
                }catch (Exception e){
                    mHandler.sendEmptyMessage(0);
                    Log.v("out","钥匙数据转换失败！");
                }
            }
        }).start();

    }

    /*底部tab点击事件*/
    public void initClick(){

        Intent intent = getIntent();
        userType = intent.getStringExtra("user_type");
            /*1和3是普通用户，不显示服务tab*/
        if("1".equalsIgnoreCase(userType)||"3".equalsIgnoreCase(userType)||"".equalsIgnoreCase(userType)){
            activityMainBinding.mainTvService.setVisibility(View.GONE);
        }
        activityMainBinding.mainImgLeft.setOnClickListener(this);
        activityMainBinding.mainTvHome.setSelected(true);
        preTv = activityMainBinding.mainTvHome;
        activityMainBinding.mainTvHome.setOnClickListener(this);
        activityMainBinding.mainTvMe.setOnClickListener(this);
        if(activityMainBinding.mainTvService.getVisibility()==View.VISIBLE) {
            activityMainBinding.mainTvService.setOnClickListener(this);
        }
        activityMainBinding.mainTvRecord.setOnClickListener(this);

    }

    /*加载fragment*/
    public void initFragment(){

        final List<Fragment> fragments = new ArrayList<Fragment>();
        /*添加顺序不能变*/
        fragments.add(new Main_home());
        fragments.add(new Main_record());
        /*1和3是普通用户，不显示服务tab*/
        if(!("1".equalsIgnoreCase(userType)&&"3".equalsIgnoreCase(userType))){
            fragments.add(new Main_service());
        }
        fragments.add(new Main_me());


        /*禁止滑动*/
        activityMainBinding.mainViewpager.setCanScroll(false);
        activityMainBinding.mainViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()){
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        activityMainBinding.mainViewpager.setOffscreenPageLimit(4);
    }

    /*开启蓝牙*/
    public void openBlueTooth(){

            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            boolean isSupportBT = bluetoothAdapter == null?false:true;
            if(isSupportBT) {
                openMiaodou();
                if (!SPHelper.getBooleanData(This(), "isCanOpenBlueTooth")) {

                }else {

                    if (!bluetoothAdapter.isEnabled()) {
                        dialog = new SureCancelDialog();
                        dialog.setTitle("开启蓝牙");
                        dialog.setBody("　　应用需要开启蓝牙，是否开启?");
                        dialog.setListener(this, this);
                        dialog.show(getSupportFragmentManager(), "openBtDialog");
                    }
                }
                }

    }

    /*MiaodouSDK相关*/
    public void openMiaodou() {

        if(!application.isRegister){
            MiaodouKeyAgent.registerBluetooth(this);
            application.isRegister = true;
        }

        boolean isOpen = SPHelper.getBooleanData(This() , "user_isOpen");
        if(!isOpen){
            MiaodouKeyAgent.setNeedSensor(false);
        }else {
            MiaodouKeyAgent.setNeedSensor(true);
        }

        if(keys!=null&&keys.size()>0){

            List<MDVirtualKey> mdVirtualKeys = new ArrayList<MDVirtualKey>();
            for(MKey key : keys){
                MDVirtualKey mdVirtualKey = MiaodouKeyAgent.makeVirtualKey(this,key.user_id,key.guard_code,key.depart_code,key.key_str);
                mdVirtualKeys.add(mdVirtualKey);
            }

            MiaodouKeyAgent.keyList = mdVirtualKeys;

        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if(!((BaseApplication)getApplication()).isPreOpen) {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    boolean isSupportBT = bluetoothAdapter == null?false:true;
                    if(isSupportBT){
                        bluetoothAdapter.disable();
                    }
                }
                /*保存未上传的开门记录*/
                ((BaseApplication)getApplication()).cacheThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        if(((BaseApplication)getApplication()).mUploadLogHistoryArrayList != null && ((BaseApplication)getApplication()).mUploadLogHistoryArrayList.size() > 0) {
                            String data = JSONObject.toJSONString(((BaseApplication) getApplication()).mUploadLogHistoryArrayList);
                            SPHelper.setStringData(This() , "recordhistory" , data);
                        }
                    }
                });
                finish();
                ((BaseApplication)getApplication()).exit();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            MiaodouKeyAgent.unregisterMiaodouAgent();
        }catch (Exception e){}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_tv_home:
                preTv.setSelected(false);
                preTv = activityMainBinding.mainTvHome;
                activityMainBinding.mainTvHome.setSelected(true);
                activityMainBinding.mainViewpager.setCurrentItem(0,false);
                activityMainBinding.mainImgLeft.setVisibility(View.VISIBLE);
                activityMainBinding.mainTopTitle.setText("首页");
                activityMainBinding.mainTopRight.setVisibility(View.INVISIBLE);
                break;
            case R.id.main_tv_service:
                preTv.setSelected(false);
                preTv = activityMainBinding.mainTvService;
                activityMainBinding.mainTvService.setSelected(true);
                if(!("1".equalsIgnoreCase(userType)&&"3".equalsIgnoreCase(userType))){
                    activityMainBinding.mainViewpager.setCurrentItem(2,false);
                    activityMainBinding.mainImgLeft.setVisibility(View.INVISIBLE);
                    activityMainBinding.mainTopRight.setVisibility(View.VISIBLE);
                    activityMainBinding.mainTopRight.setOnClickListener(this);
                    activityMainBinding.mainTopTitle.setText("服务");
                }
                break;
            case R.id.main_tv_me:
                preTv.setSelected(false);
                preTv = activityMainBinding.mainTvMe;
                activityMainBinding.mainTvMe.setSelected(true);
                activityMainBinding.mainImgLeft.setVisibility(View.INVISIBLE);
                activityMainBinding.mainTopTitle.setText("我");
                activityMainBinding.mainTopRight.setVisibility(View.INVISIBLE);
                if(!("1".equalsIgnoreCase(userType)&&"3".equalsIgnoreCase(userType))){
                    activityMainBinding.mainViewpager.setCurrentItem(3,false);
                }else activityMainBinding.mainViewpager.setCurrentItem(2,false);
                break;
            case R.id.main_tv_record:
                preTv.setSelected(false);
                preTv = activityMainBinding.mainTvRecord;
                activityMainBinding.mainTopRight.setVisibility(View.INVISIBLE);
                activityMainBinding.mainTvRecord.setSelected(true);
                activityMainBinding.mainViewpager.setCurrentItem(1,false);
                activityMainBinding.mainImgLeft.setVisibility(View.INVISIBLE);
                activityMainBinding.mainTopTitle.setText("记录");
                break;
            case R.id.surecanceldialog_tv_ok:
                BluetoothAdapter.getDefaultAdapter().enable();
                SPHelper.setBooleanData(This(),"isCanOpenBlueTooth",true);
                dialog.dismiss();
                break;
            case R.id.surecanceldialog_tv_cancel:
                if(dialog!=null){
                    dialog.dismiss();
                }
                break;
            case R.id.main_img_left:
                startActivity(new Intent(MainActivity.this , ApplykeyActivity.class));
                break;
            case R.id.main_top_right:
                startActivity(new Intent(MainActivity.this , CardReadDeviceActivity.class));
                break;
        }
    }

}
