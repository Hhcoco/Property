package com.xunhe.ilpw.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.databinding.ActivityHandleKeyApplyDetailBinding;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MHandleKeyItem;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.WlCallback;
import com.xunhe.ilpw.view.WheelView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Request;

public class HandleKeyApplyDetailActivity extends BaseActivity implements View.OnClickListener{

    private ActivityHandleKeyApplyDetailBinding activityHandleKeyApplyDetailBinding;
    private MHandleKeyItem item;
    private WlCallback callback , rejectCallback;
    private String time , type;
    private ArrayList<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            activityHandleKeyApplyDetailBinding = setView(R.layout.activity_handle_key_apply_detail , false);
        }catch (Exception e){}
        if(activityHandleKeyApplyDetailBinding != null){
            toDo();
        }
    }

    @Override
    protected void toDo() {
        try{
            item = getIntent().getParcelableExtra("keyItem");
            type = getIntent().getStringExtra("type");
        }
        catch (Exception e){}
        if(item != null) {
            String name = item.username;
            String phone = item.phone;
            String idcard = item.id_card;
            activityHandleKeyApplyDetailBinding.handlekeyapplydetailTvName.setText(name);
            activityHandleKeyApplyDetailBinding.handlekeyapplydetailTvIdcard.setText(idcard);
            activityHandleKeyApplyDetailBinding.handlekeyapplydetailTvPhone.setText(phone);
        }
        if("delay".equalsIgnoreCase(type))
            activityHandleKeyApplyDetailBinding.handlekeyapplydetailGivekey.setText("同意续期");
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailLlLeft.setOnClickListener(this);
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailGivekey.setOnClickListener(this);
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailReject.setOnClickListener(this);
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailTvTime.setOnClickListener(this);
        for(int i=1;i<13;i++){
            items.add(i+"个月");
        }
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailWheelview.setItems(items);
        activityHandleKeyApplyDetailBinding.handlekeyapplydetailWheelview.setOnWheelViewListener(new WheelView.OnWheelViewListener(){
            @Override
            public void onSelected(int selectedIndex, String item) {
                activityHandleKeyApplyDetailBinding.handlekeyapplydetailTvTime.setText(item);
                time = (selectedIndex+1)+"";
            }
        });

    }

    /*发放钥匙*/
    public void giveKey(){

        String descri = activityHandleKeyApplyDetailBinding.handlekeyapplydetailEtDescri.getText().toString();
        if(time == null){
            Toast.makeText(This() , "请选择时间!" , Toast.LENGTH_LONG).show();
            return;
        }

        if(item != null){
            if(callback == null) {
                callback = new WlCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        dialogShow();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(This(), "网络错误!", Toast.LENGTH_LONG).show();
                        dialogDismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialogDismiss();
                        if (response != null) {
                            if(type.equalsIgnoreCase("delay"))
                                EventBus.getDefault().post(new MEventBusMsg(Config.KEYLISTDELAY_CHANGED));
                            else
                                EventBus.getDefault().post(new MEventBusMsg(Config.KEYLIST_CHANGED));
                            Toast.makeText(This(), "钥匙已经发放!", Toast.LENGTH_LONG).show();
                        } else Toast.makeText(This(), "钥匙已存在!", Toast.LENGTH_LONG).show();
                    }
                };
            }
            String token = SPHelper.getStringData(this,"user_token");
            String applyId = item.id;
            if(!"".equalsIgnoreCase(token)) {
                JSONObject prm = new JSONObject();
                try {
                    prm.put("token", token);
                    prm.put("applyId", applyId);
                    prm.put("validity", time);
                    prm.put("description", descri);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean isConnected = OperateHelper.isNetworkConnected(This());
                if(isConnected) {
                    HttpHelper.request("api/apply/agree", prm.toString(), callback);
                    finish();
                }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*驳回*/
    public void reject(){
        if(item != null){
            if(rejectCallback == null) {
                rejectCallback = new WlCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        dialogShow();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(This(), "网络出问题了!", Toast.LENGTH_LONG).show();
                        dialogDismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialogDismiss();
                        if (response != null) {
                            if(type.equalsIgnoreCase("delay")) EventBus.getDefault().post(new MEventBusMsg(Config.KEYLISTDELAY_CHANGED));
                            else EventBus.getDefault().post(new MEventBusMsg(Config.KEYLIST_CHANGED));
                            Toast.makeText(This(), "已驳回!", Toast.LENGTH_LONG).show();
                            finish();
                        } else Toast.makeText(This(), "出错了，驳回失败!", Toast.LENGTH_LONG).show();
                    }
                };
            }
            String token = SPHelper.getStringData(this,"user_token");
            String applyId = item.id;
            if(!"".equalsIgnoreCase(token)) {
                JSONObject prm = new JSONObject();
                try {
                    prm.put("token", token);
                    prm.put("applyId", applyId);
                    prm.put("description" , "");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Boolean isConnected = OperateHelper.isNetworkConnected(This());
                if(isConnected) {
                    HttpHelper.request("api/apply/disagree", prm.toString(), rejectCallback);
                }else Toast.makeText(This(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.handlekeyapplydetail_givekey:
                giveKey();
                break;
            case R.id.handlekeyapplydetail_ll_left:
                finish();
                break;
            case R.id.handlekeyapplydetail_reject:
                reject();
                break;
            case R.id.handlekeyapplydetail_tv_time:
                if(activityHandleKeyApplyDetailBinding.handlekeyapplydetailWheelview.getVisibility() == View.INVISIBLE)
                    activityHandleKeyApplyDetailBinding.handlekeyapplydetailWheelview.setVisibility(View.VISIBLE);
                break;
        }
    }
}
