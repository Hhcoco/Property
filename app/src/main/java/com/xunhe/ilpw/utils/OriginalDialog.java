package com.xunhe.ilpw.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xunhe.ilpw.R;

/**
 * Created by wangliang on 2016/7/26.
 */
public class OriginalDialog extends DialogFragment {

    private TextView mTvTakePhoto , mTvFromAlbum;
    private View.OnClickListener listener1,listener2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        Dialog dialog = new Dialog(getActivity(), R.style.FullScreenDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        dialog.setContentView(R.layout.originaldialog);
        dialog.setCanceledOnTouchOutside(true);
        mTvTakePhoto = (TextView) dialog.findViewById(R.id.original_tv_takephoto);
        mTvFromAlbum = (TextView) dialog.findViewById(R.id.original_tv_from_album);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(wlp);

        if(listener1 != null)
        mTvTakePhoto.setOnClickListener(listener1);
        if(listener2 != null)
        mTvFromAlbum.setOnClickListener(listener2);

        return dialog;
    }
    public void setListener(View.OnClickListener listener1 , View.OnClickListener listener2){
        this.listener1 = listener1;
        this.listener2 = listener2;
    }
}
