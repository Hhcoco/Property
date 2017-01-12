package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivitySelectCommunityBinding;
import com.xunhe.ilpw.model.MCommunity;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SelectCommunityActivity extends BaseActivity {

    private ActivitySelectCommunityBinding activitySelectCommunityBinding;
    private WlCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            activitySelectCommunityBinding = setView(R.layout.activity_select_community, false);
        }catch (Exception e){}
        if(activitySelectCommunityBinding!=null) toDo();
    }

    @Override
    protected void toDo() {

        activitySelectCommunityBinding.selectcommunityLlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        callback = new WlCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(activitySelectCommunityBinding.selectcommunityLoading.getVisibility()==View.VISIBLE){
                    activitySelectCommunityBinding.selectcommunityLoading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                if(activitySelectCommunityBinding.selectcommunityLoading.getVisibility()==View.VISIBLE){
                    activitySelectCommunityBinding.selectcommunityLoading.setVisibility(View.GONE);
                }
                Log.v("out","社区信息:"+response);
                List<MCommunity> datas = com.alibaba.fastjson.JSONObject.parseArray(response , MCommunity.class);
                updateView(datas);
            }
        };
        if(OperateHelper.isNetworkConnected(This())){
            String token = SPHelper.getStringData(This(),"user_token");
            if(!"".equalsIgnoreCase(token)) {
                JSONObject prm = new JSONObject();
                try {
                    prm.put("token", token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    HttpHelper.request("api/user/house", prm.toString(), callback);
            }
        }else { Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();}

    }

    /*展示社区信息*/
    public  void updateView(List<MCommunity> data){
        if(data!=null&&data.size()>0) {
            for (final MCommunity mCommunity : data) {

                RelativeLayout relativeLayout = new RelativeLayout(This());
                TextView mTvBuild = new TextView(This());
                TextView mTvUnit = new TextView(This());
                LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                ll_lp.setMargins(0,12,0,0);
                relativeLayout.setBackgroundColor(Color.parseColor("#ffffff"));
                RelativeLayout.LayoutParams rl_lp_build = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams rl_lp_unit = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeLayout.setLayoutParams(ll_lp);
                rl_lp_build.setMargins(16,0,0,0);
                mTvBuild.setPadding(0,12,0,12);
                mTvBuild.setLayoutParams(rl_lp_build);
                mTvBuild.setText(mCommunity.depart_name);
                rl_lp_unit.setMargins(0,0,16,0);
                mTvUnit.setPadding(0,12,0,12);
                rl_lp_unit.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mTvUnit.setLayoutParams(rl_lp_unit);
                mTvUnit.setText(mCommunity.unit);
                mTvBuild.setTextColor(Color.parseColor("#8B8B8B"));
                mTvUnit.setTextColor(Color.parseColor("#8B8B8B"));
                mTvBuild.setTextSize(18);
                mTvUnit.setTextSize(18);

                relativeLayout.addView(mTvBuild);
                relativeLayout.addView(mTvUnit);

                relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("depart_name", mCommunity.depart_name);
                        intent.putExtra("depart_code", mCommunity.depart_code);
                        intent.putExtra("unit", mCommunity.unit);
                        setResult(200, intent);
                        finish();
                    }
                });

                activitySelectCommunityBinding.selectcommunityLl.addView(relativeLayout);

            }
        }
    }
}
