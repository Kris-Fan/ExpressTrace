package com.extrace.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.extrace.ui.R;
import com.extrace.ui.adapter.MainAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainOrderFragment extends Fragment {
    private ViewPager pager;
    private View view;
    private List<View> viewList = new ArrayList<View>();
    private PagerAdapter viewAdapter;
    private List<String> titles = new ArrayList<String>();//标题
    private TabLayout tabLayout;
    private MainAdapter pagerAdapter;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    public MainOrderFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_order_center,container,false);
        initView();
        bindView();
        return view;
    }

    private void bindView() {

    }


    private void initView() {

        mFragments.add(new ExpressTaskFragment());

        mFragments.add(new PackageTransTaskFragment());
        mFragments.add(new ExpressTaskFragment());
        //mFragments.add(new ());
/*        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.con, f1).commit();*/
        /**
         * a这个一定要是 getChildFragmentManager()不能是getFragmentManager啊，
         * 开始就是后者，导致了闪退！
         */
        String[] tabs = {"派送任务", "揽收任务","转运任务"};
        pagerAdapter = new MainAdapter(this.getChildFragmentManager(),mFragments,tabs);
        pager =view.findViewById(R.id.view_pager);
        pager.setAdapter(pagerAdapter);
        //pager.setCurrentItem(1);
        tabLayout  = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager);
        //tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
    protected Activity mActivity;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.mActivity =(Activity) context;
    }

}
