package com.xunhe.ilpw.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by wangliang on 2016/8/9.
 */
public class BaseViewPager extends ViewPager {

    private boolean isCanScroll;

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanScroll(boolean isCanScroll){
        this.isCanScroll = isCanScroll;
    }

    // 触摸没有反应就可以了
    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onTouchEvent(event);
        }

        return false;
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isCanScroll) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

}
