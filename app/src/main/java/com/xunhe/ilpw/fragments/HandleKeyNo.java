package com.xunhe.ilpw.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.HandleKeyApplyActivity;
import com.xunhe.ilpw.activity.HandleKeyApplyDetailActivity;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MHandleKey;
import com.xunhe.ilpw.model.MHandleKeyItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.RecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

public class HandleKeyNo extends Fragment implements View.OnClickListener , SwipeRefreshLayout.OnRefreshListener , OnLoadMoreListener{

    private View view;
    private RecyclerView recyclerView;
    private WlCallback callback;
    private RelativeLayout relativeLayoutLoading;
    private RecyclerView.Adapter adapter;
    private ArrayList<MHandleKeyItem> mData = new ArrayList<MHandleKeyItem>();
    private int page = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeToLoadLayout swipeToLoadLayout;
    private MHandleKey mHandleKey = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        EventBus.getDefault().register(this);
        view = inflater.inflate(R.layout.fragment_handle_key, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        recyclerView.addItemDecoration(new RecyclerViewDivider(getActivity() , LinearLayoutManager.VERTICAL , 2 ,getResources().getColor(R.color.white)));
        relativeLayoutLoading = (RelativeLayout) view.findViewById(R.id.handlekeyapply_loading);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.personadmin_srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoad);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        initView();
        getApplyData();
        return view;
    }

    public void initView(){

        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.item_handle_key , parent , false);
                HandleKeyViewHolder VH = new HandleKeyViewHolder(itemView);
                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position =(int) v.getTag();
                        MHandleKeyItem item = mData.get(position);
                        Intent i = new Intent(getActivity() , HandleKeyApplyDetailActivity.class);
                        i.putExtra("keyItem" , item);
                        startActivity(i);
                    }
                });*/
                return VH;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if(mData!=null&&mData.size()>0){
                    ((HandleKeyViewHolder)holder).mTvName.setText(mData.get(position).username);
                    String time = mData.get(position).created_at;
                    String myTime = OperateHelper.formatData("yyyy-MM-dd HH:mm:ss" , time);
                    ((HandleKeyViewHolder)holder).mTvTime.setText(myTime);
                    ((HandleKeyViewHolder)holder).mTvPhone.setText(mData.get(position).phone);
                    String state = mData.get(position).state;
                    if("0".equalsIgnoreCase(state))
                        state = "待处理";
                    else if("2".equalsIgnoreCase(state)) {
                        state = "已驳回";
                                ((HandleKeyViewHolder)holder).mTvState.setTextColor(getResources().getColor(R.color.color_FF764F));
                    }
                    else if("1".equalsIgnoreCase(state)) {
                        state = "已同意";
                        ((HandleKeyViewHolder)holder).mTvState.setTextColor(getResources().getColor(R.color.google_blue));
                    }
                    ((HandleKeyViewHolder)holder).mTvState.setText(state);
                    holder.itemView.setTag(position);
                }

            }

            @Override
            public int getItemCount() {
                return mData.size();
            }

            class HandleKeyViewHolder extends RecyclerView.ViewHolder{

                private TextView mTvName,mTvTime,mTvPhone,mTvState;
                public HandleKeyViewHolder(View itemView) {
                    super(itemView);
                    mTvName = (TextView) itemView.findViewById(R.id.item_handle_key_tv_name);
                    mTvTime = (TextView) itemView.findViewById(R.id.item_handle_key_tv_time);
                    mTvPhone = (TextView) itemView.findViewById(R.id.item_handle_key_tv_phone);
                    mTvState = (TextView) itemView.findViewById(R.id.item_handle_key_tv_state);
                }
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    /*获取申请列表数据*/
    public void getApplyData(){

        if(callback == null){
            callback = new WlCallback() {
                public void onBefore(Request request, int id) {
                    try {
                        relativeLayoutLoading.setVisibility(View.VISIBLE);
                    }catch (Exception e){}

                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    try {
                        relativeLayoutLoading.setVisibility(View.INVISIBLE);
                    }catch (Exception e1){}
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onResponse(String response, int id) {
                    try {
                        relativeLayoutLoading.setVisibility(View.INVISIBLE);
                    }catch (Exception e2){}
                    swipeRefreshLayout.setRefreshing(false);
                    OperateHelper.Log("申请列表结果:"+response);
                    if(response != null) {
                        mHandleKey = com.alibaba.fastjson.JSONObject.parseObject(response, MHandleKey.class);
                        if(page == 1) mData =(ArrayList<MHandleKeyItem>) mHandleKey.items;
                        else mData.addAll(mHandleKey.items);
                        adapter.notifyDataSetChanged();
                        page++;
                    }

                }
            };}
        String token = SPHelper.getStringData(getActivity(),"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("page", page+"");
                prm.put("state" , "2");
                prm.put("type" , "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(getActivity());
            if(isConnected) {
                HttpHelper.request("api/apply", prm.toString(), callback);
            }else Toast.makeText(getActivity(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void update(MEventBusMsg msg){
        if(Config.KEYLIST_CHANGED.equalsIgnoreCase(msg.getMsg())){
            page = 1;
            getApplyData();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        getApplyData();
    }

    @Override
    public void onLoadMore() {
        swipeToLoadLayout.setLoadingMore(false);
        if(mHandleKey != null){
            /*还有下一页*/
            if(mHandleKey.total_pages>=page){
                getApplyData();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
