package com.xunhe.ilpw.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstance){
        View view = inflater.inflate(R.layout.loadingdialog,null);
        progressBar = (ProgressBar) view.findViewById(R.id.loadingdialog_progressbar);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

}
