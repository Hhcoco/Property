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
import com.xunhe.ilpw.databinding.ActivityHandleKeyDelayBinding;
import com.xunhe.ilpw.fragments.HandleKeyDelayNo;
import com.xunhe.ilpw.fragments.HandleKeyDelayOk;
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

public class HandleKeyDelayActivity extends BaseActivity implements View.OnClickListener{

    private ActivityHandleKeyDelayBinding activityHandleKeyDelayBinding;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityHandleKeyDelayBinding = setView(R.layout.activity_handle_key_delay,false);
        }catch (Exception e){}
        if(activityHandleKeyDelayBinding!=null){
            toDo();
        }
    }

    @Override
    protected void toDo() {

        activityHandleKeyDelayBinding.handlekeydelayTvOk.setOnClickListener(this);
        activityHandleKeyDelayBinding.handlekeydelayTvNo.setOnClickListener(this);
        activityHandleKeyDelayBinding.handlekeydelayLlLeft.setOnClickListener(this);

        fragments.add(new HandleKeyDelayOk());
        fragments.add(new HandleKeyDelayNo());

        activityHandleKeyDelayBinding.handlekeydelayViewpager.setCanScroll(false);
        activityHandleKeyDelayBinding.handlekeydelayViewpager.setOffscreenPageLimit(2);
        activityHandleKeyDelayBinding.handlekeydelayViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        activityHandleKeyDelayBinding.handlekeydelayViewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /*点击变色*/
    public void changeColor(){
        activityHandleKeyDelayBinding.handlekeydelayTvOk.setTextColor(Color.parseColor("#666666"));
        activityHandleKeyDelayBinding.handlekeydelayTvNo.setTextColor(Color.parseColor("#666666"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.handlekeydelay_tv_ok:
                changeColor();
                activityHandleKeyDelayBinding.handlekeydelayTvOk.setTextColor(Color.parseColor("#009FF8"));
                activityHandleKeyDelayBinding.handlekeydelayViewpager.setCurrentItem(0 , false);
                break;
            case R.id.handlekeydelay_tv_no:
                changeColor();
                activityHandleKeyDelayBinding.handlekeydelayTvNo.setTextColor(Color.parseColor("#009FF8"));
                activityHandleKeyDelayBinding.handlekeydelayViewpager.setCurrentItem(1 , false);
                break;
            case R.id.handlekeydelay_ll_left:
                finish();
                break;
        }
    }

}
