package com.extrace.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.extrace.ui.fragment.ExpressTaskFragment;
import com.extrace.ui.fragment.PackageTransTaskFragment;

import java.util.List;

import static com.extrace.net.OkHttpClientManager.BASE_URL;

/**
 * 订单中心的适配器！
 */
public class MainAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private String url0=BASE_URL+"/ExtraceSystem/weipaisongList/";//派送任务
    private String url1=BASE_URL+"/ExtraceSystem/weipaisongList/";//揽收任务
    private String url2=BASE_URL+"/ExtraceSystem/customerInfos/";//转运任务
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
                case 0://派送任务
                    ExpressTaskFragment newInstance = new ExpressTaskFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("url",url0);
                    newInstance.setArguments(bundle);
                    return newInstance;
                case 1://揽收任务
                    ExpressTaskFragment newInstance1 = new ExpressTaskFragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("url",url1);
                    newInstance1.setArguments(bundle1);
                    return newInstance1;
                case 2://转运任务
                    PackageTransTaskFragment fragment2 = new PackageTransTaskFragment();
                    return fragment2;
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

