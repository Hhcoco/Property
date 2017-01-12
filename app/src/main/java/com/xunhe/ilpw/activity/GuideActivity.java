package com.xunhe.ilpw.activity;

import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.xunhe.ilpw.R;
import com.xunhe.ilpw.databinding.ActivityGuideBinding;
import com.xunhe.ilpw.fragments.Guide_cloud;
import com.xunhe.ilpw.fragments.Guide_family;
import com.xunhe.ilpw.fragments.Guide_rocket;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;


public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityGuideBinding activityGuideBinding = DataBindingUtil.setContentView(this, R.layout.activity_guide);
        final ViewPager viewPager = activityGuideBinding.guideViewpager;
        CircleIndicator indicator = activityGuideBinding.guideIndicator;
        ArrayList<Fragment> datas = new ArrayList<Fragment>();
        datas.add(new Guide_rocket());
        datas.add(new Guide_cloud());
        datas.add(new Guide_family());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),datas);
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);
    }

    @Override
    protected void toDo() {

    }

    /*viewpager adapter*/
    class ViewPagerAdapter extends FragmentPagerAdapter{

        ArrayList<Fragment> datas;

        public ViewPagerAdapter(FragmentManager fm,ArrayList<Fragment> datas){
            super(fm);
            this.datas = datas;
        }

        @Override
        public Fragment getItem(int position) {
            return datas.get(position);
        }

        @Override
        public int getCount() {
            return datas.size();
        }
    }
}
