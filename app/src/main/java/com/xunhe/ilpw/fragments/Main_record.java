package com.xunhe.ilpw.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.model.MHistroyRecord;
import com.xunhe.ilpw.model.MHistroyRecordItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.zhy.http.okhttp.callback.Callback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import okhttp3.Call;

/**
 * Created by wangliang on 2016/7/15.
 */
public class Main_record extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private ExpandableListView expandableListView;
    private RelativeLayout relativeLayoutLoading;
    private ArrayList<String> groupData = new ArrayList<String>();
    private ArrayList<ArrayList<String>> childData = new ArrayList<ArrayList<String>>();
    private HashMap<String , ArrayList<String>> tempData = new HashMap<String , ArrayList<String>>();
    private BaseExpandableListAdapter adapter;
    private WlCallback callback;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private LinearLayout mLinearLayoutNoMessage;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //可见时执行的操作
            if(view != null){
                page = 1;
                getRecordData();
            }
        } else {
            //不可见时执行的操作
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstanc){
        view = inflater.inflate(R.layout.fragment_main_record,null);
        expandableListView = (ExpandableListView) view.findViewById(R.id.main_record_expandablelistview);
        relativeLayoutLoading = (RelativeLayout) view.findViewById(R.id.record_loading);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.record_srl);
        swipeRefreshLayout.setColorSchemeResources(R.color.color_FF8764,R.color.color_009FF8);
        swipeRefreshLayout.setOnRefreshListener(this);
        mLinearLayoutNoMessage = (LinearLayout) view.findViewById(R.id.fragment_main_record_nomessage);
        bindData();
        getRecordData();
        return  view;

    }

    /*获取数据*/
    public void getRecordData(){

        callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefreshLayout.setRefreshing(false);
                if(groupData != null && groupData.size() > 0){
                    mLinearLayoutNoMessage.setVisibility(View.INVISIBLE);
                }else mLinearLayoutNoMessage.setVisibility(View.VISIBLE);

            }

            @Override
            public void onResponse(String response, int id) {
                Log.v("out" , "记录结果："+response);
                swipeRefreshLayout.setRefreshing(false);
                if(response!=null){
                    MHistroyRecord mHistroyRecord = com.alibaba.fastjson.JSONObject.parseObject(response , MHistroyRecord.class);
                    /*对结果进行处理分组*/
                    handleData(mHistroyRecord);
                }
            }
        };

        String token = SPHelper.getStringData(getActivity(),"user_token");


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
                HttpHelper.request("api/access/list", prm.toString(), callback);
            }else Toast.makeText(getActivity(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }

    }

    /*对数据分组*/
    public void handleData(MHistroyRecord data){

        tempData.clear();
        groupData.clear();
        childData.clear();

        if(data!=null) {
            ArrayList<MHistroyRecordItem> items = (ArrayList<MHistroyRecordItem>) data.items;

            if(items != null && items.size() > 0)
                mLinearLayoutNoMessage.setVisibility(View.INVISIBLE);
            else {
                mLinearLayoutNoMessage.setVisibility(View.VISIBLE);
                return;
            }

            /*获取所有组*/
            for(MHistroyRecordItem curData:items){
                String date = OperateHelper.formatData("yyyy年MM月dd日" , curData.created_at);
                if(!"".equalsIgnoreCase(date)){
                    if(!groupData.contains(date)){
                        groupData.add(date);
                        tempData.put(date, new ArrayList<String>());
                    }
                    /*tempData.get(date).add(curData.guard_code+"@"+curData.created_at);
                    Set<String> keys = tempData.keySet();
                    for(String group:groupData){
                        if(keys.contains(group)){
                            ArrayList<String> ss = tempData.get(group);
                            childData.add(ss);
                        }
                    }*/
                }
            }
            /*获取所有组对应的数据*/
            for(String group:groupData){
                ArrayList<String> tempchild = new ArrayList<String>();
                for(MHistroyRecordItem curData:items){
                    String date = OperateHelper.formatData("yyyy年MM月dd日" , curData.created_at);
                    if(group.equalsIgnoreCase(date)){
                        tempchild.add(curData.guard_code +"@"+curData.created_at);
                    }
                }
                childData.add(tempchild);
            }


            adapter.notifyDataSetChanged();
        }

    }

    /*创建adapter*/
    public void bindData(){

        adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return groupData.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return childData.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return groupData.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return childData.get(groupPosition).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                GroupViewHolder groupViewHolder = null;
                if(convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_group , null);
                    groupViewHolder = new GroupViewHolder();
                    groupViewHolder.mTvTime = (TextView) convertView.findViewById(R.id.item_group_tv_time);
                    groupViewHolder.mImg = (ImageView) convertView.findViewById(R.id.item_group_img);
                    convertView.setTag(groupViewHolder);
                }else {
                    groupViewHolder = (GroupViewHolder) convertView.getTag();
                }
                groupViewHolder.mTvTime.setText(groupData.get(groupPosition));
                if(isExpanded){
                    groupViewHolder.mImg.setBackgroundResource(R.drawable.sanjiao_show);
                }else {
                    groupViewHolder.mImg.setBackgroundResource(R.drawable.sanjiao_hide);
                }
                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                ChildViewHolder childViewHolder = null;
                if(convertView == null){
                    convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_child,null);
                    childViewHolder = new ChildViewHolder();
                    childViewHolder.mTvName = (TextView) convertView.findViewById(R.id.item_child_tv_name);
                    childViewHolder.mTvTime = (TextView) convertView.findViewById(R.id.item_child_tv_time);
                    convertView.setTag(childViewHolder);
                }else {
                    childViewHolder = (ChildViewHolder) convertView.getTag();
                }
                childViewHolder.mTvName.setText(childData.get(groupPosition).get(childPosition).split("@")[0]);
                childViewHolder.mTvTime.setText(OperateHelper.formatData("yyyy-MM-dd HH:mm:dd" , childData.get(groupPosition).get(childPosition).split("@")[1]));
                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return true;
            }

            class GroupViewHolder{
                TextView mTvTime;
                ImageView mImg;
            }
            class ChildViewHolder{
                TextView mTvName , mTvTime;
            }
        };
        expandableListView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        page = 1;
        getRecordData();
    }
}
