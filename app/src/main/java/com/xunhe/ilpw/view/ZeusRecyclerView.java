package com.xunhe.ilpw.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.xunhe.ilpw.activity.PersonAdminActivity;

/**
 * Created by wangliang on 2016/7/31.
 */
public class ZeusRecyclerView extends RecyclerView {

    private Rect rect = new Rect();
    private int curClickedPosition ;
    private float firstPoint , firstPointY , mScrollX = 0;
    private LinearLayout linearLayout =  null;

    public ZeusRecyclerView(Context context) {
        super(context);
    }

    public ZeusRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ZeusRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                linearLayout = null;
                firstPoint = event.getX();
                firstPointY = event.getY();
                int x = (int)event.getX();
                int y = (int)event.getY();
                int mFirstVisibleViewPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                int viewCount = getChildCount();
                for(int i=0;i<viewCount;i++){
                    View view = getChildAt(i);
                    if(view.getVisibility()==View.VISIBLE){
                        view.getHitRect(rect);
                        if(rect.contains(x , y)){
                            curClickedPosition = mFirstVisibleViewPosition+i;
                            PersonAdminActivity.PersonAdminAdapter.PersonAdminVH myViewHolder =(PersonAdminActivity.PersonAdminAdapter.PersonAdminVH) getChildViewHolder(view);
                            linearLayout = myViewHolder.linearLayout;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(event.getY() - firstPointY) < Math.abs(event.getX() - firstPoint)) {
                    if (linearLayout != null) {
                        if (Math.abs(event.getX() - firstPoint) > 5) {
                            int newScrollX = (int) (firstPoint - event.getX());
                            if (newScrollX < 0 && mScrollX <= 0) {
                                newScrollX = 0;
                            } else if (newScrollX > 0 && mScrollX > 100 * 3) {
                                newScrollX = 0;
                            }
                            Log.v("out", newScrollX + "/" + mScrollX);
                            mScrollX = linearLayout.getScrollX();
                            if (mScrollX + newScrollX <= 305 && mScrollX + newScrollX >= -5)
                                linearLayout.scrollBy(newScrollX, 0);
                            firstPoint = event.getX();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(linearLayout != null) {
                    if (mScrollX >= 150) {
                        linearLayout.scrollTo(300, 0);
                    } else if (mScrollX < 150) {
                       linearLayout.scrollTo(0, 0);
                        //getContext().startActivity(new Intent(getContext() , TestActivity.class));
                    }
                }
                break;
        }

        return  super.onTouchEvent(event);
    }

}
