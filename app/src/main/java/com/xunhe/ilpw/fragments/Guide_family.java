package com.xunhe.ilpw.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.MainActivity;
import com.xunhe.ilpw.activity.WelcomActivity;
import com.xunhe.ilpw.utils.SPHelper;

/**
 * Created by wangliang on 2016/7/13.
 */
public class Guide_family extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstance){
        View view = inflater.inflate(R.layout.fragment_guide_family,null);
        TextView img = (TextView) view.findViewById(R.id.fragment_guide_family);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WelcomActivity.class));
                SPHelper.setBooleanData(getActivity(),"isFirstOpen",true);
                getActivity().finish();
            }
        });
        return  view;
    }
}
