package com.extrace.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.extrace.ui.fragment.ExpressRevTaskFragment;
import com.extrace.ui.fragment.ExpressTaskFragment;
import com.extrace.ui.fragment.MainMenuFragment;

import java.util.List;

/**
 * 订单中心的适配器！
 */
public class MainAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    String[] tabs = {"首页","mmm","mmm","mmm"};
    public MainAdapter(FragmentManager fm, List<Fragment> mFragments, String[] tabs) {
        super(fm);
        this.mFragments = mFragments;
        if (tabs != null || tabs.length != 0) {
            this.tabs = tabs;
        }
    }
    @Override
    public Fragment getItem(int position) {//必须实现

            switch(position)
            {
                case 0:
                    ExpressTaskFragment fragment1 = new ExpressTaskFragment();
                    return fragment1;
                case 1:
                    ExpressRevTaskFragment fragment2 = new ExpressRevTaskFragment();
                    return fragment2;
                case 2:
                    ExpressTaskFragment fragment3 = new ExpressTaskFragment();
                    return fragment3;
            }
            return null;
    }

    @Override
    public int getCount() {//必须实现
        return mFragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {//选择性实现
        return tabs[position];
        //return mFragments.get(position).getClass().getSimpleName();
    }
}

