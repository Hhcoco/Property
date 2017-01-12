package com.xunhe.ilpw.activity;

import android.app.Application;
import android.content.Context;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hzblzx.miaodou.sdk.MiaodouKeyAgent;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.utils.HomeWatcher;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract  class BaseActivity extends AppCompatActivity {

    private ViewDataBinding viewDataBinding = null;
    private LinearLayout mLinearContent;
    public LoadingDialog BaseLoadingDialog;
    private HomeWatcher mHomeWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        ((BaseApplication)getApplication()).Activitys.add(this);
    }
    /*dialog的show方法*/
    public void dialogShow(){
        if(BaseLoadingDialog == null)
            BaseLoadingDialog = new LoadingDialog();
        BaseLoadingDialog.show(getSupportFragmentManager() , "basedialog");
    }
    public void dialogDismiss(){
        try {
            BaseLoadingDialog.dismiss();
        }catch (Exception e){}
    }
    /*return context*/
    public Context This(){
        return this;
    }
    /*绑定view，并且返回binding对象*/
    public <T extends ViewDataBinding> T setView(int layoutId) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //return  setView(layoutId,false);


        /*统一加actionbar会导致databingding无法使用*/

        mLinearContent = (LinearLayout) getLayoutInflater().inflate(R.layout.base,null);
        View actionbar  = getLayoutInflater().inflate(R.layout.action_bar,null);
        actionbar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ViewDataBinding viewDataBinding = getBinding(layoutId);
        View view = viewDataBinding.getRoot();
        //View view = getLayoutInflater().inflate(layoutId,null);
        view.setLayoutParams(new ActionBar.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLinearContent.addView(actionbar);
        mLinearContent.addView(view);

        setContentView(mLinearContent);
        return getBinding(layoutId);

    }
    /*重载方法，不需要actionbar的*/
    public <T extends ViewDataBinding> T setView(int layoutId,boolean isNeedActionbar) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        setContentView(layoutId);
        return getBinding(layoutId);
    }

    public <T extends ViewDataBinding> T getBinding(int layoutId) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        /*重要：所有数据绑定都是通过下面的方法*/
        View decorView = getWindow().getDecorView();
        android.databinding.DataBindingComponent bindingComponent = DataBindingUtil.getDefaultComponent();
        ViewGroup contentView = (ViewGroup) decorView.findViewById(android.R.id.content);
        /*利用反射获取Android系统私有方法*/
        Class c = Class.forName("android.databinding.DataBindingUtil");
        Constructor constructor = c.getDeclaredConstructor();
        constructor.setAccessible(true);
        DataBindingUtil dataBindingUtil = (DataBindingUtil) constructor.newInstance();
        Method m = c.getDeclaredMethod("bindToAddedViews", DataBindingComponent.class, ViewGroup.class, int.class, int.class);
        m.setAccessible(true);
        viewDataBinding = (T) m.invoke(dataBindingUtil, bindingComponent, contentView, 0, layoutId);
        return (T) viewDataBinding;
    }

    protected abstract void toDo();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(MEventBusMsg msg){
        if(Config.SCAN_DEVICE.equalsIgnoreCase(msg.getMsg())){
            if(BaseLoadingDialog == null) BaseLoadingDialog = new LoadingDialog();
            if(!BaseLoadingDialog.isAdded())
            BaseLoadingDialog.show(getSupportFragmentManager() , "");
        }else if(Config.END_SCAN.equalsIgnoreCase(msg.getMsg()))
            try {
                BaseLoadingDialog.dismiss();
            }catch (Exception e){}

        }

    @Override
    public void onRestart(){

        boolean state = SPHelper.getBooleanData(This() , "user_isOpen");
        if(state) {
            MiaodouKeyAgent.setNeedSensor(true);
        }
        super.onRestart();
    }

    @Override
    public void onResume(){
        super.onResume();
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                MiaodouKeyAgent.setNeedSensor(false);
            }

            @Override
            public void onHomeLongPressed() {

            }
        });
        mHomeWatcher.startWatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHomeWatcher.setOnHomePressedListener(null);
        mHomeWatcher.stopWatch();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
