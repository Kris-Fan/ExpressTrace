package com.extrace.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.extrace.ui.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpressEditAdvanceInfoFragment extends Fragment {

    private TextView expressAccTime;

    public ExpressEditAdvanceInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.express_edit_info_advance,container, false);
        expressAccTime = view.findViewById(R.id.expressAccTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        expressAccTime.setText(simpleDateFormat.format(date));//"Date获取当前日期时间"+
        return view;
    }

}
