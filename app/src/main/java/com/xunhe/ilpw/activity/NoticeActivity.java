package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityNoticeBinding;
import com.xunhe.ilpw.model.MTip;
import com.xunhe.ilpw.model.MTipItem;
import com.xunhe.ilpw.model.MTipList;
import com.xunhe.ilpw.model.MTipListItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.SpaceItemDecoration;

import org.json.JSONException;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

public class NoticeActivity extends BaseActivity implements View.OnClickListener , SwipeRefreshLayout.OnRefreshListener{

    private ActivityNoticeBinding activityNoticeBinding;
    private WlCallback callback , deleteCallback ;
    private ArrayList<MTipListItem> mData = new ArrayList<MTipListItem>();
    private TipListAdapter adapter;
    /*默认请求第1页*/
    private String page = "1";
    private LoadingDialog loadingDialog;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityNoticeBinding = setView(R.layout.activity_notice , false);
        }catch (Exception e){}
        if(activityNoticeBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        loadingDialog = new LoadingDialog();

        activityNoticeBinding.noticeSrl.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        activityNoticeBinding.noticeSrl.setOnRefreshListener(this);
        activityNoticeBinding.noticeSrl.setRefreshing(false);

        activityNoticeBinding.noticeImgLeft.setOnClickListener(this);
        activityNoticeBinding.noticeRecyclerview.setLayoutManager(new LinearLayoutManager(This()));
        activityNoticeBinding.noticeRecyclerview.addItemDecoration(new SpaceItemDecoration(2));
        adapter = new TipListAdapter(mData);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) v.getTag();
                if(mData != null && mData.size() > 0) {
                    String id = mData.get(position).id;
                    Intent intent = new Intent(NoticeActivity.this , DetailWebViewActivity.class);
                    intent.putExtra("id" , id);
                    startActivity(intent);
                }
            }
        });
        activityNoticeBinding.noticeRecyclerview.setAdapter(adapter);
        getNoticeData();

    }

    /*删除公告*/
    public void DeleteTip(String deleteid , final int position){
        if(deleteCallback == null){
            deleteCallback = new WlCallback() {
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
                        mData.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                }
            };
        }
        if(token == null){
            token = SPHelper.getStringData(this,"user_token");
        }
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
                prm.put("id", deleteid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/bulletin/delete", prm.toString(), deleteCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    /*create adapter*/
    public class TipListAdapter extends RecyclerView.Adapter{

        public ArrayList<MTipListItem> mTip;
        private View.OnClickListener listener;
        public TipListAdapter(ArrayList<MTipListItem> mTip){
            this.mTip = mTip;
        }

        public void setOnItemClickListener(View.OnClickListener listener){
            this.listener = listener;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(This()).inflate(R.layout.item_tip , null);
            if(listener != null){
                //itemView.setOnClickListener(listener);
            }
            TipViewHolder VH = new TipViewHolder(itemView);
            return VH;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if(mTip != null && mTip.size()>0) {

                holder.itemView.setTag(position);

                final MTipListItem mTipItem = mTip.get(position);
                ((TipViewHolder)holder).mTvTitle.setText(mTipItem.title);
                ((TipViewHolder)holder).mTvBody.setText(mTipItem.digest);
                ((TipViewHolder)holder).mTvName.setText(mTipItem.author_name);
                ((TipViewHolder)holder).mTvFlag.setText(mTipItem.state.equalsIgnoreCase("0")?"已读":"未读");
                String time = OperateHelper.formatData("yy-MM-dd" , mTipItem.created_at);
                ((TipViewHolder)holder).mTvTime.setText(time);
                ((TipViewHolder) holder).mTvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeleteTip(mTipItem.id , position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mTip.size();
        }

        public class TipViewHolder extends RecyclerView.ViewHolder{

            private TextView mTvTitle , mTvBody , mTvName , mTvFlag , mTvTime;
            public RelativeLayout mTvDelete;
            public LinearLayout linearLayout;

            public TipViewHolder(View itemView) {
                super(itemView);
                mTvTitle = (TextView) itemView.findViewById(R.id.item_tip_tv_title);
                mTvBody = (TextView) itemView.findViewById(R.id.item_tip_tv_body);
                mTvName = (TextView) itemView.findViewById(R.id.item_tip_tv_name);
                mTvFlag = (TextView) itemView.findViewById(R.id.item_tip_tv_flag);
                mTvTime = (TextView) itemView.findViewById(R.id.item_tip_tv_time);
                mTvDelete = (RelativeLayout) itemView.findViewById(R.id.itemtip_tv_delete);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.itemtip_top_ll);
            }
        }
    }

    /*获取网络数据*/
    public void getNoticeData(){

        if(callback == null) {
            callback = new WlCallback() {
                @Override
                public void onBefore(Request request, int id) {
                    activityNoticeBinding.noticeLoading.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Call call, Exception e, int id) {
                    activityNoticeBinding.noticeSrl.setRefreshing(false);
                    activityNoticeBinding.noticeLoading.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onResponse(String response, int id) {
                    activityNoticeBinding.noticeLoading.setVisibility(View.INVISIBLE);
                    activityNoticeBinding.noticeSrl.setRefreshing(false);
                    Log.v("out", "消息列表结果:" + response);
                    if (response != null) {
                        MTipList mTiplist = JSONObject.parseObject(response, MTipList.class);
                        if("1".equalsIgnoreCase(page))
                            mData.clear();
                        mData.addAll(mTiplist.items);
                        if (adapter != null)
                            adapter.notifyDataSetChanged();
                    }
                }
            };
        }
        if(OperateHelper.isNetworkConnected(This())){
            String token = SPHelper.getStringData(This(),"user_token");
            if(!"".equalsIgnoreCase(token)) {
                org.json.JSONObject prm = new org.json.JSONObject();
                try {
                    prm.put("page" , page);
                    prm.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpHelper.request("api/bulletin", prm.toString(), callback);
            }
        }else { Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();}

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notice_img_left:
                finish();
                break;
        }
    }
    @Override
    public void onRefresh() {

        activityNoticeBinding.noticeSrl.setRefreshing(true);
        page = "1";
        getNoticeData();

    }
}
