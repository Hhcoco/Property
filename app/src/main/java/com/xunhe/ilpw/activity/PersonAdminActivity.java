package com.xunhe.ilpw.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.squareup.picasso.Picasso;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityPersonAdminBinding;
import com.xunhe.ilpw.databinding.ActivityPersonDataBinding;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MPerson;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.SpaceItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

public class PersonAdminActivity extends BaseActivity implements View.OnClickListener ,OnLoadMoreListener , SwipeRefreshLayout.OnRefreshListener{

    private ActivityPersonAdminBinding activityPersonAdminBinding;
    /*区分是家人管理还是租户管理 0x001 家人，0x002租户*/
    private int type = 0x002;
    private String uri_list , uri_delete;
    private ArrayList<MPerson> mData = new ArrayList<MPerson>();
    private int flag = 0;
    private LoadingDialog loadingDialog;
    private WlCallback callback , deleteCallback;
    private PersonAdminAdapter adapter;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        try{
            activityPersonAdminBinding = setView(R.layout.activity_person_admin , false);
        }catch (Exception e){}
        if(activityPersonAdminBinding != null){
            toDo();
        }
    }



    @Override
    protected void toDo() {

        loadingDialog = new LoadingDialog();
        activityPersonAdminBinding.personadminLlLeft.setOnClickListener(this);
        activityPersonAdminBinding.personadminLlRight.setOnClickListener(this);
        initData();
        getData();
    }

    /*获取数据*/
    public void initData(){
        type = getIntent().getIntExtra("persontype", 0x000);
        if(0x001 == type){
            activityPersonAdminBinding.personadminTvTitle.setText("家人管理");
            uri_list = "api/relation/family";
            uri_delete = "api/relation/del";
        }else if(0x002 == type){
            activityPersonAdminBinding.personadminTvTitle.setText("租户管理");
            uri_list = "api/relation/tenant";
            uri_delete = "api/relation/del";
        }

        activityPersonAdminBinding.personadminSrl.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        activityPersonAdminBinding.personadminSrl.setOnRefreshListener(this);
        activityPersonAdminBinding.swipeTarget.setLayoutManager(new LinearLayoutManager(This()));
        activityPersonAdminBinding.swipeTarget.addItemDecoration(new SpaceItemDecoration(2));
        activityPersonAdminBinding.swipeToLoad.setOnLoadMoreListener(this);

        adapter = new PersonAdminAdapter(mData , This());
        activityPersonAdminBinding.swipeTarget.setAdapter(adapter);
    }

    /*请求家人列表数据*/
    public void getData(){
        if(callback == null){
            callback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                    if(flag == 0)
                        loadingDialog.show(getSupportFragmentManager(), "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    activityPersonAdminBinding.personadminSrl.setRefreshing(false);
                    loadingDialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    activityPersonAdminBinding.personadminSrl.setRefreshing(false);
                    loadingDialog.dismiss();
                    OperateHelper.Log("家人列表结果:" + response);
                    if (response != null) {
                        mData = (ArrayList<MPerson>) com.alibaba.fastjson.JSONObject.parseArray(response, MPerson.class);
                        adapter.setData(mData);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        }
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
                HttpHelper.request(uri_list, prm.toString(), callback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }

    }

    /*删除人员*/
    public void deletePerson(String id , final int position){
        if(deleteCallback == null){
            deleteCallback = new WlCallback() {
                @Override
                public void onBefore(Request request , int id){
                        loadingDialog.show(getSupportFragmentManager(), "");
                }
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingDialog.dismiss();
                }

                @Override
                public void onResponse(String response, int id) {
                    loadingDialog.dismiss();
                    if(response != null){
                        mData.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        }
        String token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("relationId", id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request(uri_delete, prm.toString(), deleteCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadMore() {
        activityPersonAdminBinding.swipeTarget.getAdapter().notifyDataSetChanged();
        activityPersonAdminBinding.swipeToLoad.setLoadingMore(false);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }


    /*create adapter*/
    public class PersonAdminAdapter extends RecyclerView.Adapter{

        public ArrayList<MPerson> data;
        private Context context;

        public PersonAdminAdapter(ArrayList<MPerson> data , Context context){
            this.data = data;
            this.context = context;
        }

        public void setData(ArrayList<MPerson> data){
            this.data = data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.item_person_admin , null);
            PersonAdminVH VH = new PersonAdminVH(itemView);
            return VH;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(data != null && data.size()>0) {
                Picasso.with(context).load(data.get(position).head_picture).error(R.drawable.error).into(((PersonAdminVH) holder).img);
                ((PersonAdminVH) holder).mTvName.setText(data.get(position).username);
                ((PersonAdminVH) holder).mTvPhone.setText(data.get(position).phone);
                ((PersonAdminVH) holder).mTvTime.setText(OperateHelper.formatData("yyyy-MM-dd" , data.get(position).created_at));
                ((PersonAdminVH) holder).mTvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePerson(data.get(position).id ,position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        public class PersonAdminVH extends RecyclerView.ViewHolder{

            public LinearLayout linearLayout;
            public ImageView img;
            public TextView mTvName, mTvPhone , mTvTime ;
            public RelativeLayout mTvDelete;

            public PersonAdminVH(View itemView) {
                super(itemView);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.top_ll);
                img = (ImageView) itemView.findViewById(R.id.itempersonadmin_img);
                mTvName = (TextView) itemView.findViewById(R.id.itempersonadmin_tv_name);
                mTvPhone = (TextView) itemView.findViewById(R.id.itempersonadmin_tv_phone);
                mTvTime = (TextView) itemView.findViewById(R.id.itempersonadmin_tv_time);
                mTvDelete = (RelativeLayout) itemView.findViewById(R.id.itempersonadmin_tv_delete);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void update(MEventBusMsg msg){
        if(msg.getMsg().equalsIgnoreCase(Config.PERSON_CHANGED)){
            /*添加人员后返回需要刷新*/
            flag = 1;
            getData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personadmin_ll_left:
                finish();
                break;
            case R.id.personadmin_ll_right:
                Intent intent = new Intent(PersonAdminActivity.this , AddPersonActivity.class);
                intent.putExtra("persontype" , type);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
