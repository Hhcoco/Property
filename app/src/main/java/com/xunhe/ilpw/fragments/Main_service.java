package com.xunhe.ilpw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.HandleKeyApplyActivity;
import com.xunhe.ilpw.activity.HandleKeyDelayActivity;
import com.xunhe.ilpw.activity.PreRegisterActivity;

/**
 * Created by wangliang on 2016/7/15.
 */
public class Main_service extends Fragment implements View.OnClickListener{

    private View view;
    private FrameLayout mFlRegister,mFlHandleKey,mFlDelayKey;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstanc){
        view = inflater.inflate(R.layout.fragment_main_service,null);
        mFlRegister = (FrameLayout) view.findViewById(R.id.tabapply_fl_register);
        mFlHandleKey = (FrameLayout) view.findViewById(R.id.tabapply_fl_apply);
        mFlDelayKey = (FrameLayout) view.findViewById(R.id.tabapply_fl_extension);
        mFlRegister.setOnClickListener(this);
        mFlHandleKey.setOnClickListener(this);
        mFlDelayKey.setOnClickListener(this);
        return  view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.tabapply_fl_register:
                intent.setClass(getActivity() , PreRegisterActivity.class);
                break;
            case R.id.tabapply_fl_apply:
                intent.setClass(getActivity() , HandleKeyApplyActivity.class);
                break;
            case R.id.tabapply_fl_extension:
                intent.setClass(getActivity() , HandleKeyDelayActivity.class);
                break;
        }
        startActivity(new Intent(intent));
    }
}
