package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivitySuggestionBinding;
import com.xunhe.ilpw.model.MHouse;
import com.xunhe.ilpw.model.MHouseInfo;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;

import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Request;

public class SuggestionActivity extends BaseActivity implements View.OnClickListener{

    private ActivitySuggestionBinding activitySuggestionBinding;
    private WlCallback getHouseCallback , submitCallback;
    private LoadingDialog loadingDialog;
    private String token;
    private LinkedList<String> mItems = new LinkedList<String>() , mHouseCode = new LinkedList<String>();
    private String selectedHouseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activitySuggestionBinding = setView(R.layout.activity_suggestion , false);
        }catch (Exception e){}
        if(activitySuggestionBinding != null) toDo();
    }

    @Override
    protected void toDo() {
        loadingDialog = new LoadingDialog();
        activitySuggestionBinding.suggestLlLeft.setOnClickListener(this);
        activitySuggestionBinding.suggestTvOk.setOnClickListener(this);
        getData();
    }

    public void updateView(MHouseInfo mHouseInfo){
        mItems.clear();
        for(MHouse houses:mHouseInfo.houses){
            mItems.add(houses.unit+"单元"+houses.building+"栋"+houses.house);
            /*记录位置*/
            mHouseCode.add(houses.depart_code);
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySuggestionBinding.suggestSpinner.setAdapter(adapter);
        activitySuggestionBinding.suggestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mHouseCode.size()>0)
                    selectedHouseId = mHouseCode.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedHouseId = "";
            }
        });
    }

    /*获取网络数据*/
    public void getData(){
        if(getHouseCallback == null){
            getHouseCallback = new WlCallback() {
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
                    OperateHelper.Log("用户详细信息："+response);
                    if(response != null){
                        if(response != null) {
                            MHouseInfo mHouseInfo = com.alibaba.fastjson.JSONObject.parseObject(response, MHouseInfo.class);
                            updateView(mHouseInfo);
                        }
                    }
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/user/info", prm.toString(), getHouseCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }
    /*上传意见*/
    public void submit(String content){
        if(submitCallback == null){
            submitCallback = new WlCallback() {
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
                    if(response != null)
                        Toast.makeText(This(),"意见已提交，我们将尽快处理!",Toast.LENGTH_SHORT).show();
                }
            };
        }
        if(token == null)
            token = SPHelper.getStringData(this,"user_token");
        if(!"".equalsIgnoreCase(token)) {
            org.json.JSONObject prm = new org.json.JSONObject();
            try {
                prm.put("token", token);
                prm.put("content", content);
                prm.put("depart_code", selectedHouseId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(This());
            if(isConnected) {
                HttpHelper.request("api/feedback/add", prm.toString(), submitCallback);
            }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.suggest_ll_left:
                finish();
                break;
            case R.id.suggest_tv_ok:
                if(selectedHouseId != null && !"".equalsIgnoreCase(selectedHouseId)){
                    String suggestion = activitySuggestionBinding.suggestEt.getText().toString();
                    if("".equalsIgnoreCase(suggestion.trim())){
                        Toast.makeText(This(),"留言不能为空!",Toast.LENGTH_SHORT).show();
                        break;
                    }else {
                        submit(suggestion);
                    }
                }else {
                    Toast.makeText(This(),"没有选择社区信息!",Toast.LENGTH_SHORT).show();
                    break;
                }
                break;

        }
    }
}
