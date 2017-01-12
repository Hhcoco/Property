package com.xunhe.ilpw.utils;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.xunhe.ilpw.R;

/**
 * Created by wangliang on 2016/7/17.
 */
public class SureCancelDialog extends DialogFragment {

    private View.OnClickListener mOk,mCancel;
    private TextView mTvOk,mTvCancel,mTitle,mBody;
    private String title,body;


    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstance){
        View view = inflater.inflate(R.layout.surecanceldialog,null);
        mTitle = (TextView) view.findViewById(R.id.surecanceldialog_tv_title);
        mBody = (TextView) view.findViewById(R.id.surecanceldialog_tv_body);
        mTvOk = (TextView) view.findViewById(R.id.surecanceldialog_tv_ok);
        mTvCancel = (TextView) view.findViewById(R.id.surecanceldialog_tv_cancel);

        this.mTitle.setText(title);
        this.mBody.setText(body);
        mTvOk.setOnClickListener(mOk);
        mTvCancel.setOnClickListener(mCancel);

        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    public void setListener(View.OnClickListener okListener, View.OnClickListener noListener){
        this.mOk = okListener;
        this.mCancel = noListener;
    }

    public void setTitle(String title){
        this.title = title;
    }
    public void setBody(String body){
        this.body = body;
    }

}
