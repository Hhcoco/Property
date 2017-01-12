package com.xunhe.ilpw.fragments;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.hzblzx.miaodou.sdk.core.model.MDVirtualKey;
import com.squareup.picasso.Picasso;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.KeyDetailActivity;
import com.xunhe.ilpw.activity.MainActivity;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MAd;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MKey;
import com.xunhe.ilpw.utils.ADInfo;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.ImageCycleView;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.SureCancelDialog;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;


/**
 * Created by wangliang on 2016/7/15.
 */
public class Main_home extends Fragment implements SwipeRefreshLayout.OnRefreshListener , OnLoadMoreListener{

    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLayoutManager;
    private LoadingDialog loadingDialog;
    private boolean isNeedRefresh = false , isOnlyOne = true;
    private List<MDVirtualKey> mdVirtualKeys;
    private BaseApplication application;
    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    swipeRefreshLayout.setRefreshing(false);
                    break;

            }
        };
    };
    private ImageCycleView mImgCycleView;
    private ArrayList<ADInfo> infos = new ArrayList<ADInfo>();
    private String[] imageUrls = {R.drawable.error+""};
    private MainHomeAdapter adapter;
    private String token;
    private WlCallback keyCallback;
    private int page = 1;
    private ArrayList<MKey> keys;
    private LinearLayout mLinearLayoutNoMessage;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstanc){

        loadingDialog = new LoadingDialog();
        EventBus.getDefault().register(this);
        application = (BaseApplication) getActivity().getApplication();
        /*默认错误图片*/
        infos.add(new ADInfo("",R.drawable.error+"","",""));

        view = inflater.inflate(R.layout.fragment_main_home,null);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_main_srl);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_main_recycleview);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        swipeRefreshLayout.setOnRefreshListener(this);

        mLinearLayoutNoMessage = (LinearLayout) view.findViewById(R.id.fragment_main_home_nomessage);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.addItemDecoration(new SpaceItemDecoration(2));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /*钥匙列表*/
        final ArrayList<MKey> keys = ((MainActivity)getActivity()).keys;
        if(keys!=null&&keys.size()>0){
            adapter = new MainHomeAdapter(keys,getActivity());
            adapter.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)v.getTag();
                    String keyId = adapter.getData().get(position).id;
                    String keyName = adapter.getData().get(position).name;
                    String depart_code = adapter.getData().get(position).depart_code;
                    String guard_code = adapter.getData().get(position).guard_code;
                    String time = adapter.getData().get(position).expire_time;
                    Intent intent = new Intent(getActivity() , KeyDetailActivity.class);
                    intent.putExtra("keyId" , keyId);
                    intent.putExtra("keyName" , keyName);
                    intent.putExtra("depart_code" , depart_code);
                    intent.putExtra("guard_code" , guard_code);
                    intent.putExtra("time" , time);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);
        }else {

            /*从网络获取*/
            getKeyData();
            if(adapter == null) adapter = new MainHomeAdapter(new ArrayList<MKey>(),getActivity());
            adapter.setOnItemClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int)v.getTag();
                    String keyId = adapter.getData().get(position).id;
                    String keyName = adapter.getData().get(position).name;
                    String depart_code = adapter.getData().get(position).depart_code;
                    String guard_code = adapter.getData().get(position).guard_code;
                    String time = adapter.getData().get(position).expire_time;
                    Intent intent = new Intent(getActivity() , KeyDetailActivity.class);
                    intent.putExtra("keyId" , keyId);
                    intent.putExtra("keyName" , keyName);
                    intent.putExtra("depart_code" , depart_code);
                    intent.putExtra("guard_code" , guard_code);
                    intent.putExtra("time" , time);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(adapter);

        }
        getAdPicture();

        return  view;

    }

    /*开启蓝牙*/
    public void openBlueTooth(){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isSupportBT = bluetoothAdapter == null?false:true;
        if(isSupportBT) {
            openMiaodou();
            if (!SPHelper.getBooleanData(getActivity(), "isCanOpenBlueTooth")) {
            }else {
                if (!bluetoothAdapter.isEnabled()) {
                    final SureCancelDialog dialog = new SureCancelDialog();
                    dialog.setTitle("开启蓝牙");
                    dialog.setBody("　　应用需要开启蓝牙，是否开启?");
                    dialog.setListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BluetoothAdapter.getDefaultAdapter().enable();
                            SPHelper.setBooleanData(getActivity() , "isCanOpenBlueTooth",true);
                            dialog.dismiss();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dialog!=null){
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show(getActivity().getSupportFragmentManager(), "openBtDialog");
                }
            }
        }

    }
    /*打开妙兜*/
    public void openMiaodou() {

        String keysJson = SPHelper.getStringData(getActivity() , "keylist");
        try {
            if(keys == null)
                keys = new ArrayList<MKey>();
            keys = (ArrayList<MKey>) com.alibaba.fastjson.JSONObject.parseArray(keysJson, MKey.class);
            mHandler.sendEmptyMessage(1);
            if(keys.size()==0)
                return;
        }catch (Exception e){
            mHandler.sendEmptyMessage(1);
            Log.v("out","钥匙数据转换失败！");
        }

        if(isOnlyOne) {
            /*只能注册一次，如果开始从本地获取那么已经注册过一次了，这里刷新就不再注册*/
            if(!application.isRegister) {
                MiaodouKeyAgent.registerBluetooth(getActivity());
                application.isRegister = true;
            }

            boolean isOpen = SPHelper.getBooleanData(getActivity(), "user_isOpen");
            if (!isOpen) {
                MiaodouKeyAgent.setNeedSensor(false);
            } else {
                MiaodouKeyAgent.setNeedSensor(true);
            }
            if(keys == null)
                keys = new ArrayList<MKey>();

            /*上面的操作只能执行一次*/
            isOnlyOne = false;
        }



        if(keys!=null&&keys.size()>0){
            if(mdVirtualKeys == null)
                mdVirtualKeys = new ArrayList<MDVirtualKey>();
            else mdVirtualKeys.clear();
            for(MKey key : keys){
                MDVirtualKey mdVirtualKey = MiaodouKeyAgent.makeVirtualKey(getActivity() , key.user_id,key.guard_code,key.depart_code,key.key_str);
                mdVirtualKeys.add(mdVirtualKey);
            }

            MiaodouKeyAgent.keyList = mdVirtualKeys;

        }
    }

    /*get key list*/
    public void getKeyData(){

        if(token == null)
            token = SPHelper.getStringData(getActivity() , "user_token");
        if(keyCallback == null){
        keyCallback = new WlCallback() {
            @Override
            public void onBefore(Request request , int id){
                if(loadingDialog == null)
                    loadingDialog = new LoadingDialog();
                try{
                    loadingDialog.show(getActivity().getSupportFragmentManager() , "");
                }catch (Exception e){}

            }
            @Override
            public void onError(Call call, Exception e, int id) {
                try {
                    loadingDialog.dismiss();
                }catch (Exception e1){}
                swipeRefreshLayout.setRefreshing(false);
                OperateHelper.Log("二次获取钥匙列表失败");
                mLinearLayoutNoMessage.setVisibility(View.VISIBLE);

            }

            @Override
            public void onResponse(String response, int id) {
                swipeRefreshLayout.setRefreshing(false);
                try {
                    loadingDialog.dismiss();
                }catch (Exception e){}
                if(response!=null){
                    try{
                        ArrayList<MKey> keyList = (ArrayList<MKey>) com.alibaba.fastjson.JSONObject.parseArray(response,MKey.class);

                        if(keyList != null && keyList.size() > 0){
                            mLinearLayoutNoMessage.setVisibility(View.INVISIBLE);
                        }else mLinearLayoutNoMessage.setVisibility(View.VISIBLE);

                        if(page == 1)
                            adapter.updateKey(keyList);
                        else adapter.addData(keyList);
                        adapter.notifyDataSetChanged();
                        Log.v("out","钥匙列表为："+response);
                        if(1 == page){
                            SPHelper.setStringData(getActivity() , "keylist",response);
                            openMiaodou();
                        }
                        page++;

                    }catch (Exception e){}
                }else mLinearLayoutNoMessage.setVisibility(View.VISIBLE);

            }
        };}

        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("page", page+"");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(getActivity());
            if(isConnected) {
                HttpHelper.request("api/key", prm.toString(), keyCallback);
            }else Toast.makeText(getActivity(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }

    }


    /*获取广告图片地址*/
    public void getAdPicture(){
        if(adapter == null){
            adapter = new MainHomeAdapter(new ArrayList<MKey>() , getActivity());
        }
        WlCallback callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String response, int id) {
                swipeRefreshLayout.setRefreshing(false);
                OperateHelper.Log("广告"+response);
                List<MAd> ads = com.alibaba.fastjson.JSONObject.parseArray(response , MAd.class);
                if(ads != null && ads.size()>0) {
                    infos.clear();
                    for (MAd mAd : ads) {
                        infos.add(new ADInfo("" , mAd.pic ,mAd.target , ""));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        if(token == null)
        token = SPHelper.getStringData(getActivity() , "user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(getActivity());
            if(isConnected) {
                String ss = prm.toString();
                HttpHelper.request("api/picture", prm.toString(), callback);
            }else Toast.makeText(getActivity(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*广告轮播*/
    public void imgRecycle(RecyclerView.ViewHolder holder){
        final ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(ADInfo info, int position, View imageView) {
                //Toast.makeText(getActivity(), "content->" + info.getContent(), Toast.LENGTH_SHORT).show();
                Intent i = null;
                String url = infos.get(position).getContent();
            }
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                //ImageLoader.getInstance().displayImage(imageURL, imageView);// 使用ImageLoader对图片进行加装！
                Picasso.with(getContext()).load(imageURL).error(R.drawable.error).into(imageView);
            }
        };

        if(infos.size()>0)
        ((MainHomeAdapter.HeaderViewHolder)holder).mImgCycleView.setImageResources(infos,mAdCycleViewListener);
    }

    @Override
    public void onLoadMore() {

    }

    /*create adapter*/
    class MainHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private ArrayList<MKey> keys;
        private Context context;
        private View.OnClickListener listener;
        private List<MDVirtualKey> mdVirtualKeys = new ArrayList<MDVirtualKey>();

        public MainHomeAdapter(ArrayList<MKey> keys , Context context){
            this.keys = keys;
            this.context = context;
        }
        public void updateKey(ArrayList<MKey> keys){
            this.keys = keys;
        }
        public ArrayList<MKey> getData(){
            return this.keys;
        }
        public void addData(ArrayList<MKey> keys){
            this.keys.addAll(keys);
            mdVirtualKeys.clear();
            for(MKey key : this.keys){
                MDVirtualKey mdVirtualKey = MiaodouKeyAgent.makeVirtualKey(getActivity() , key.user_id,key.guard_code,key.depart_code,key.key_str);
                mdVirtualKeys.add(mdVirtualKey);
            }
            MiaodouKeyAgent.keyList = mdVirtualKeys;
        }
        public void setOnItemClickListener(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 1) {
                View itemView = LayoutInflater.from(context).inflate(R.layout.item_key_list, parent, false);
                MainHomeViewHolder VH = new MainHomeViewHolder(itemView);
                if(listener !=null)
                itemView.setOnClickListener(listener);
                return VH;
            }else{
            /*广告轮播*/
                View headerView = LayoutInflater.from(context).inflate(R.layout.header_ad, parent,false);
                HeaderViewHolder VH = new HeaderViewHolder(headerView);
                if(listener !=null)
                    headerView.setOnClickListener(listener);
                return VH;
            }
        }
        @Override
        public int getItemViewType(int position){
            if(position == 0)
            return 0;
            else return 1;
        }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            /*点击事件回调,因为有HeaderView，所以减1*/
            holder.itemView.setTag(position - 1);

            if(position == 0){
                imgRecycle(holder);
            }else {
                ((MainHomeViewHolder)holder).mTvDoorName.setText(keys.get(position-1).name);
                String time;
                if(System.currentTimeMillis() > 1000*Long.parseLong(keys.get(position-1).expire_time)){
                    time = "钥匙已过期";
                    ((MainHomeViewHolder)holder).mTvDoorTime.setTextColor(getResources().getColor(R.color.google_red));
                }else {
                    time = "有效期" + OperateHelper.formatData("yy年MM月dd日", keys.get(position - 1).expire_time);
                    ((MainHomeViewHolder)holder).mTvDoorTime.setTextColor(getResources().getColor(R.color.color_666666));
                }
                ((MainHomeViewHolder)holder).mTvDoorTime.setText(time);
                ((MainHomeViewHolder)holder).mTvDoorType.setText(keys.get(position-1).type.equalsIgnoreCase("1")?"普通钥匙":"复制钥匙");
                ((MainHomeViewHolder)holder).mImgOpenDoor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /*点击具体钥匙开门*/
                        Log.v("out","点击："+keys.get(position-1).id);
                        MKey key = keys.get(position-1);
                        MDVirtualKey mdVirtualKey = MiaodouKeyAgent.makeVirtualKey(context,key.user_id,key.guard_code,key.depart_code,key.key_str);
                        loadingDialog.show(getActivity().getSupportFragmentManager() , "opendoor_loading_dialog");
                        loadingDialog.setShowsDialog(true);
                        ((BaseApplication)getActivity().getApplication()).dialogFragments.add(loadingDialog);
                        MiaodouKeyAgent.openDoor(mdVirtualKey,true);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            /*因为有一个头布局，所以+1*/
            return keys.size()+1;
        }

        class MainHomeViewHolder extends RecyclerView.ViewHolder{
            TextView mTvDoorName , mTvDoorTime , mTvDoorType ;
            ImageView mImgOpenDoor;
            public MainHomeViewHolder(View itemView) {
                super(itemView);
                mTvDoorName = (TextView) itemView.findViewById(R.id.item_key_list_tv_name);
                mTvDoorTime = (TextView) itemView.findViewById(R.id.item_key_list_tv_time);
                mTvDoorType = (TextView) itemView.findViewById(R.id.item_key_list_tv_type);
                mImgOpenDoor = (ImageView) itemView.findViewById(R.id.item_key_list_img_opendoor);
            }

        }

        class HeaderViewHolder extends RecyclerView.ViewHolder{
            ImageCycleView mImgCycleView;
            public HeaderViewHolder(View itemView) {
                super(itemView);
                mImgCycleView = (ImageCycleView) itemView.findViewById(R.id.recycleimageview);
            }
        }

    }


    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void test(MEventBusMsg event) {
        if(event.getMsg().equalsIgnoreCase(Config.KEY_CHANGED)) {
            page = 1;
            getKeyData();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        getKeyData();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try{
            MiaodouKeyAgent.unregisterMiaodouAgent();
        }catch (Exception e){}
    }

}
