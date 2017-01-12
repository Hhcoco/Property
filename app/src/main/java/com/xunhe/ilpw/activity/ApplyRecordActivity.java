package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityApplyRecordBinding;
import com.xunhe.ilpw.model.MApplyRecode;
import com.xunhe.ilpw.model.MApplyRecordItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.SpaceItemDecoration;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ApplyRecordActivity extends BaseActivity implements View.OnClickListener , SwipeRefreshLayout.OnRefreshListener , OnLoadMoreListener {

    private ActivityApplyRecordBinding activityApplyRecordBinding;
    private WlCallback callback;
    private ArrayList<MApplyRecordItem> mData = new ArrayList<MApplyRecordItem>();
    private RecyclerView.Adapter adapter;
    private int page = 1;
    private MApplyRecode mApplyRecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activityApplyRecordBinding = setView(R.layout.activity_apply_record , false);
        }catch (Exception e){}
        if(activityApplyRecordBinding!=null){
            toDo();
        }
    }

    @Override
    protected void toDo(){
        activityApplyRecordBinding.applyrecordSrl.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        activityApplyRecordBinding.applyrecordSrl.setOnRefreshListener(this);
        activityApplyRecordBinding.swipeToLoad.setOnLoadMoreListener(this);
        activityApplyRecordBinding.applyrecordLlLeft.setOnClickListener(this);
        activityApplyRecordBinding.swipeTarget.setLayoutManager(new LinearLayoutManager(This()));
        activityApplyRecordBinding.swipeTarget.addItemDecoration(new SpaceItemDecoration(2));
        adapter = new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(This()).inflate(R.layout.item_handle_key , parent , false);
                ApplyRecordViewHolder VH = new ApplyRecordViewHolder(itemView);
                return VH;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                if(mData!=null&&mData.size()>0){

                    /*这样会一直new OnClickListener，不然需要继承重写Adapter*/
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ApplyRecordActivity.this , RecordDetailActivity.class);
                            intent.putExtra("applicationId" , mData.get(position).id);
                            intent.putExtra("time" , OperateHelper.formatData("yyyy-mm-dd hh:mm:ss",mData.get(position).created_at));
                            startActivity(intent);
                        }
                    });

                    ((ApplyRecordViewHolder)holder).mTvName.setText(mData.get(position).username);
                    ((ApplyRecordViewHolder)holder).mTvTime.setText(OperateHelper.formatData("yyyy-MM-dd HH:mm:ss",mData.get(position).created_at));
                    ((ApplyRecordViewHolder)holder).mTvPhone.setText(mData.get(position).phone);
                    String state = mData.get(position).state;
                    if("0".equalsIgnoreCase(state))
                        state = "待处理";
                    else if("2".equalsIgnoreCase(state)) {
                        state = "已驳回";
                        ((ApplyRecordViewHolder)holder).mTvState.setTextColor(getResources().getColor(R.color.color_FF764F));
                    }
                    else if("1".equalsIgnoreCase(state)) {
                        state = "已同意";
                        ((ApplyRecordViewHolder)holder).mTvState.setTextColor(getResources().getColor(R.color.google_blue));
                    }
                    ((ApplyRecordViewHolder)holder).mTvState.setText(state);
                }
            }

            @Override
            public int getItemCount() {
                return mData.size();
            }

            class ApplyRecordViewHolder extends RecyclerView.ViewHolder{

                private TextView mTvName,mTvTime,mTvPhone,mTvState;
                public ApplyRecordViewHolder(View itemView) {
                    super(itemView);
                    mTvName = (TextView) itemView.findViewById(R.id.item_handle_key_tv_name);
                    mTvTime = (TextView) itemView.findViewById(R.id.item_handle_key_tv_time);
                    mTvPhone = (TextView) itemView.findViewById(R.id.item_handle_key_tv_phone);
                    mTvState = (TextView) itemView.findViewById(R.id.item_handle_key_tv_state);
                }
            }
        };
        activityApplyRecordBinding.swipeTarget.setAdapter(adapter);
        getRecord();
    }


    /*请求记录数据*/
    public void getRecord(){
        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    activityApplyRecordBinding.applyrecordLoading.setVisibility(View.INVISIBLE);
                    activityApplyRecordBinding.applyrecordSrl.setRefreshing(false);
                    activityApplyRecordBinding.swipeToLoad.setLoadingMore(false);
                }

                @Override
                public void onResponse(String response, int id) {
                    activityApplyRecordBinding.applyrecordLoading.setVisibility(View.INVISIBLE);
                    activityApplyRecordBinding.applyrecordSrl.setRefreshing(false);
                    activityApplyRecordBinding.swipeToLoad.setLoadingMore(false);
                    OperateHelper.Log("申请记录结果:" + response);
                    if (response != null) {
                        mApplyRecode = JSONObject.parseObject(response, MApplyRecode.class);
                        if(page == 1)
                            mData = (ArrayList<MApplyRecordItem>) mApplyRecode.items;
                        else mData.addAll(mApplyRecode.items);
                        adapter.notifyDataSetChanged();
                        page++;
                    }
                }
            };
        }
        if(OperateHelper.isNetworkConnected(This())){
            String token = SPHelper.getStringData(This(),"user_token");
            if(!"".equalsIgnoreCase(token)) {
                org.json.JSONObject prm = new org.json.JSONObject();
                try {
                    prm.put("page" , page+"");
                    prm.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpHelper.request("api/application/list", prm.toString(), callback);
            }
        }else { Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();}
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.applyrecord_ll_left:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMore() {

        if(mApplyRecode != null){
            if(mApplyRecode.total_pages >= page){
                getRecord();
            }else activityApplyRecordBinding.swipeToLoad.setLoadingMore(false);
        }else activityApplyRecordBinding.swipeToLoad.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getRecord();
    }
}
