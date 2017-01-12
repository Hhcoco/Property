package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityDetailWebBinding;

public class DetailWebActivity extends BaseActivity {

    private ActivityDetailWebBinding activityDetailWebBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityDetailWebBinding = setView(R.layout.activity_detail_web , false);
        }catch (Exception e){}
        if(activityDetailWebBinding != null) toDo();
    }

    @Override
    protected void toDo() {
        activityDetailWebBinding.detailwebLlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityDetailWebBinding.detailwebWebview.loadUrl("http://kaimen.cdxunhe.com/protocol");
        activityDetailWebBinding.detailwebWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView , String url){
                webView.loadUrl(url);
                return  true;
            }
        });
    }
}
