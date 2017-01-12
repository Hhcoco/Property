package com.xunhe.ilpw.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.databinding.ActivityCardReadDeviceBinding;
import com.xunhe.ilpw.model.MBLE;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.SPHelper;

import java.util.ArrayList;

public class CardReadDeviceActivity extends BaseActivity implements View.OnClickListener{

    private ActivityCardReadDeviceBinding activityCardReadDeviceBinding;
    private LoadingDialog loadingDialog;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;
    private DeviceAdapter adapter;
    private ArrayList<MBLE> mData = new ArrayList<MBLE>();
    /*连接读卡器回调*/
    private BaseApplication application;
    /*当前点击的位置，给回调接口使用*/
    private int curPositon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityCardReadDeviceBinding = setView(R.layout.activity_card_read_device,false);
        }catch (Exception e){}
        if(activityCardReadDeviceBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activityCardReadDeviceBinding.cardreadLlLeft.setOnClickListener(this);
        application = (BaseApplication) getApplication();


        adapter = new DeviceAdapter(mData);
        activityCardReadDeviceBinding.cardreadRecyclerview.setLayoutManager(new LinearLayoutManager(This()));
        activityCardReadDeviceBinding.cardreadRecyclerview.setAdapter(adapter);
        loadingDialog = new LoadingDialog();


        if(application.isConnect){
            /*是从连接设备界面连接的*/
            if(application.mClient!=null&&application.mble!=null) {
                mData.add(application.mble);
                adapter.notifyDataSetChanged();
            }else {
                /*是自动连接的*/
                String adress = SPHelper.getStringData(This() , "BLEAdress");
                String name = SPHelper.getStringData(This() , "BLEName");
                MBLE temp = new MBLE(name , adress ,true);
                mData.add(temp);
                adapter.notifyDataSetChanged();
            }

        }else {
            searchBLE();
        }

    }

    /*搜索蓝牙*/
    public void searchBLE(){

        loadingDialog.show(getSupportFragmentManager() , "searchble");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter!=null){
            if(!bluetoothAdapter.isEnabled()){
                        /*open bluetooth*/
                bluetoothAdapter.enable();
            }
        }else {
            Toast.makeText(This(),"手机无蓝牙!",Toast.LENGTH_SHORT).show();
            return;
        }


        /*广播接收设备信息*/
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()){
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        //
                        loadingDialog.dismiss();
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                        String deviceName = device.getName();
                        /*有的蓝牙设备没有名字*/
                        if(deviceName!=null&&!"".equalsIgnoreCase(deviceName)) {
                            if (deviceName.contains("INVS")) {
                                loadingDialog.dismiss();
                                MBLE mble = new MBLE(deviceName, device.getAddress(), false);
                                //mData.add(mble);
                                adapter.addData(mble);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;

                }
            }
        };

        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播接收器，接收并处理搜索结果
        This().registerReceiver(receiver, intentFilter);
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        bluetoothAdapter.startDiscovery();

    }

    /*create adapter*/
    class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<MBLE> data;

        public DeviceAdapter(ArrayList<MBLE> data){
            this.data = data;
        }

        public void addData(MBLE ble){
            data.add(ble);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(This()).inflate(R.layout.item_ble ,parent , false);
            DeviceViewHolder VH = new DeviceViewHolder(itemView);
            return VH;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if(data!=null&&data.size()>0){
                ((DeviceViewHolder)holder).mTvName.setText(data.get(position).bleName);
                ((DeviceViewHolder)holder).mTvAdress.setText(data.get(position).bleAdress);
                if(data.get(position).isConnect) ((DeviceViewHolder)holder).mTvConnect.setText("断开连接");
                else
                    ((DeviceViewHolder)holder).mTvConnect.setText("连接设备");

                ((DeviceViewHolder)holder).mTvConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((DeviceViewHolder)holder).mTvConnect.getText().toString().equalsIgnoreCase("连接设备")) {
                            /*开始连接设备*/
                            curPositon = position;
                            loadingDialog.show(getSupportFragmentManager() , "");
                            Toast.makeText(This() , "连接中..." , Toast.LENGTH_LONG).show();
                            Log.v("out","地址为："+data.get(position).bleAdress);
                            application.cacheThreadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if(application.mClient.connectBt(data.get(position).bleAdress)) {
                                            loadingDialog.dismiss();
                                            application.mble = data.get(position);
                                            application.mble.isConnect = true;
                                            CardReadDeviceActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((DeviceViewHolder)holder).mTvConnect.setText("断开连接");
                                                }
                                            });
                                            /*如果连接成功,将地址保存本地，下次直接连接,只保存最近已经成功连接的地址，防止自动连接时等待时间过长*/
                                            SPHelper.setStringData(This() , "BLEAdress" , data.get(position).bleAdress);
                                            SPHelper.setStringData(This() , "BLEName" , data.get(position).bleName);
                                        }
                                        else {
                                            loadingDialog.dismiss();
                                            Looper.prepare();
                                            Toast.makeText(This() , "连接失败，请重试!",Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    }catch (Exception e){
                                        Looper.prepare();
                                        Toast.makeText(This() , "连接失败，请重试!",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                        loadingDialog.dismiss();
                                    }
                                }
                            });


                        }else if(((DeviceViewHolder)holder).mTvConnect.getText().toString().equalsIgnoreCase("断开连接")){
                            /*断开设备*/
                            curPositon = position;

                                    try {
                                        if(application.mClient != null) {
                                            if(application.mClient.disconnectBt()) {
                                                Log.v("out","断开成功");
                                                ((DeviceViewHolder)holder).mTvConnect.setText("连接设备");
                                            }
                                            else Log.v("out","断开失败");
                                        }
                                    }catch (Exception e){}


                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class DeviceViewHolder extends RecyclerView.ViewHolder{

            private TextView mTvName,mTvAdress,mTvConnect;

            public DeviceViewHolder(View itemView) {
                super(itemView);
                mTvName = (TextView) itemView.findViewById(R.id.item_ble_tv_name);
                mTvAdress = (TextView) itemView.findViewById(R.id.item_ble_tv_adress);
                mTvConnect = (TextView) itemView.findViewById(R.id.item_ble_tv_connect);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardread_ll_left:
                if(receiver!=null)
                unregisterReceiver(receiver);
                if(bluetoothAdapter!=null)
                bluetoothAdapter.cancelDiscovery();
                finish();
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            if(receiver!=null)
            unregisterReceiver(receiver);
            if(bluetoothAdapter!=null)
            bluetoothAdapter.cancelDiscovery();
        }catch (Exception e){}
    }
}
