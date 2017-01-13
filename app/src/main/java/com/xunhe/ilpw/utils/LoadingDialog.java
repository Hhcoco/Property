package com.xunhe.ilpw.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xunhe.ilpw.R;

/**
 * Created by wangliang on 2016/7/20.
 */
public class LoadingDialog extends DialogFragment {

    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * setStyle() 的第一个参数有四个可选值：
         * STYLE_NORMAL|STYLE_NO_TITLE|STYLE_NO_FRAME|STYLE_NO_INPUT
         * 其中 STYLE_NO_TITLE 和 STYLE_NO_FRAME 可以关闭标题栏
         * 每一个参数的详细用途可以直接看 Android 源码的说明
         */
        setStyle(DialogFragment.STYLE_NO_TITLE , 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstance){
        View view = inflater.inflate(R.layout.loadingdialog,null);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingdialog_progressbar);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

}
