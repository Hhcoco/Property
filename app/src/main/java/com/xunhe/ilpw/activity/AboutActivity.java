package com.xunhe.ilpw.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityAbortBinding;

public class AboutActivity extends BaseActivity implements View.OnClickListener{

    private ActivityAbortBinding activityAbortBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityAbortBinding = setView(R.layout.activity_abort , false);
        }catch (Exception e){}
        if(activityAbortBinding != null) toDo();
    }

    @Override
    protected void toDo() {
        activityAbortBinding.aboutLlLeft.setOnClickListener(this);
        activityAbortBinding.aboutTvProtocol.setOnClickListener(this);
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName() , 0);
            String verName = packageInfo.versionName;
            activityAbortBinding.aboutTvVersion.setText("软件版本　"+verName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_ll_left:
                finish();
                break;
            case R.id.about_tv_protocol:
                startActivity(new Intent(AboutActivity.this , DetailWebActivity.class));
                break;
        }
    }
}
