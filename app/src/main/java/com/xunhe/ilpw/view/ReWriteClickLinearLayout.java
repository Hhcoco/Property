package com.xunhe.ilpw.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by wangliang on 2016/8/10.
 */
public class ReWriteClickLinearLayout extends LinearLayout {

    public ReWriteClickLinearLayout(Context context) {
        super(context);
    }

    public ReWriteClickLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReWriteClickLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
        public boolean onTouchEvent(MotionEvent event) {
            return super.onTouchEvent(event);
        }
}
