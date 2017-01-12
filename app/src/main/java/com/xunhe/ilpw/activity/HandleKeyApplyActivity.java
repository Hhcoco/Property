package com.xunhe.ilpw.activity;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityHandleKeyApplyBinding;
import com.xunhe.ilpw.fragments.HandleKeyNo;
import com.xunhe.ilpw.fragments.HandleKeyOk;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

public class HandleKeyApplyActivity extends BaseActivity implements View.OnClickListener{

    private ActivityHandleKeyApplyBinding activityHandleKeyApplyBinding;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityHandleKeyApplyBinding = setView(R.layout.activity_handle_key_apply,false);
        }catch (Exception e){}
        if(activityHandleKeyApplyBinding!=null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activityHandleKeyApplyBinding.handlekeyapplyTvOk.setOnClickListener(this);
        activityHandleKeyApplyBinding.handlekeyapplyTvNo.setOnClickListener(this);
        activityHandleKeyApplyBinding.handlekeyapplyLlLeft.setOnClickListener(this);

        fragments.add(new HandleKeyOk());
        fragments.add(new HandleKeyNo());

        activityHandleKeyApplyBinding.handlekeyapplyViewpager.setCanScroll(false);
        activityHandleKeyApplyBinding.handlekeyapplyViewpager.setOffscreenPageLimit(2);
        activityHandleKeyApplyBinding.handlekeyapplyViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        activityHandleKeyApplyBinding.handlekeyapplyViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /*点击变色*/
    public void changeColor(){
        activityHandleKeyApplyBinding.handlekeyapplyTvOk.setTextColor(Color.parseColor("#666666"));
        activityHandleKeyApplyBinding.handlekeyapplyTvNo.setTextColor(Color.parseColor("#666666"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.handlekeyapply_tv_ok:
                changeColor();
                activityHandleKeyApplyBinding.handlekeyapplyTvOk.setTextColor(Color.parseColor("#009FF8"));
                activityHandleKeyApplyBinding.handlekeyapplyViewpager.setCurrentItem(0 , false);
                break;
            case R.id.handlekeyapply_tv_no:
                changeColor();
                activityHandleKeyApplyBinding.handlekeyapplyTvNo.setTextColor(Color.parseColor("#009FF8"));
                activityHandleKeyApplyBinding.handlekeyapplyViewpager.setCurrentItem(1 , false);
                break;
            case R.id.handlekeyapply_ll_left:
                finish();
                break;
        }
    }

}
